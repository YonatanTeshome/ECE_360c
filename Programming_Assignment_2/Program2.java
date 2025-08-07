/*
 * Name: Yonatan Teshome
 * EID: YH23572
 */

// Implement your algorithms here
// Methods may be added to this file, but don't remove anything
// Include this file in your final submission

import java.util.ArrayList;

public class Program2 {
    private ArrayList<Student> students;    // this is a list of all Students, populated by Driver class
    private Heap minHeap;



    // additional constructors may be added, but don't delete or modify anything already here
    public Program2(int numStudents) {
        minHeap = new Heap();
        students = new ArrayList<Student>();
    }
    /**
     * findMinimumStudentCost(Student start, Student dest)
     *
     * @param start - the starting Student.
     * @param dest  - the end (destination) Student.
     * @return the minimum cost possible to get from start to dest.
     * Assume the given graph is always connected.
     */
    public int findMinimumStudentCost(Student start, Student dest) {
        for (Student s : students) {s.resetminCost();} // RESET ALL STUDENTS MINCOST
        start.setminCost(0);

        Heap heap = new Heap(); // INITIALIZE HEAP WITH ALL STUDENTS
        heap.buildHeap(students);

        while (heap.toArrayList().size() > 0) { // DIJKSTRA LOOP
            Student current = heap.extractMin();

            if (current.getName() == dest.getName()) { // IF WE REACH UT WE FOUND THE CHEAPEST PATH
                return current.getminCost();
            }

            // RELAX NEIGHBORS
            ArrayList<Student> neighbors = current.getNeighbors();
            ArrayList<Integer> prices = current.getPrices();

            for (int i = 0; i < neighbors.size(); i++) {
                Student neighbor = neighbors.get(i);
                int edgeCost = prices.get(i);

                int newCost = current.getminCost() + edgeCost;

                if (newCost < neighbor.getminCost()) { // RELAX EDGE IF WE FOUND CHEAPER PATH
                    neighbor.setminCost(newCost);
                    heap.changeKey(neighbor, newCost);
                }
            }
        }

        return -1; // UT WAS NEVER REACHED
    }

    /**
     * findMinimumClassCost()
     *
     * @return the minimum total cost required to connect (span) each student in the class.
     * Assume the given graph is always connected.
     */
    public int findMinimumClassCost() {

        for (Student s : students) {
            s.resetminCost();
        }
        students.get(0).setminCost(0);
        Heap heap = new Heap();
        heap.buildHeap(students);

//        int count = 0;
//        while (heap.toArrayList().size() > 0) {
//            Student current = heap.extractMin();
//            count++;
//            if (count % 10 == 0)
//                System.out.println("Extracted " + count + " students so far...");
//        }
//


        while (heap.toArrayList().size() > 0) {
            Student current = heap.extractMin();

            for (int i = 0; i < current.getNeighbors().size(); i++) {
                Student neighbor = current.getNeighbors().get(i);
                int edgeCost = current.getPrices().get(i);

                if (heap.toArrayList().contains(neighbor) && edgeCost < neighbor.getminCost()) {
                    heap.changeKey(neighbor, edgeCost);
                }
            }
        }

        int totalCost = 0;
        for (Student s : students) {
            if (s.getminCost() == Integer.MAX_VALUE) {
                return -1;
            }
            totalCost += s.getminCost();
        }

        return totalCost;

    }

    //returns edges and prices in a string.
    public String toString() {
        String o = "";
        for (Student v : students) {
            boolean first = true;
            o += "Student ";
            o += v.getName();
            o += " has neighbors ";
            ArrayList<Student> ngbr = v.getNeighbors();
            for (Student n : ngbr) {
                o += first ? n.getName() : ", " + n.getName();
                first = false;
            }
            first = true;
            o += " with prices ";
            ArrayList<Integer> wght = v.getPrices();
            for (Integer i : wght) {
                o += first ? i : ", " + i;
                first = false;
            }
            o += System.getProperty("line.separator");

        }

        return o;
    }

///////////////////////////////////////////////////////////////////////////////
//                           DANGER ZONE                                     //
//                everything below is used for grading                       //
//                      please do not change :)                              //
///////////////////////////////////////////////////////////////////////////////

    public Heap getHeap() {
        return minHeap;
    }

    public ArrayList<Student> getAllstudents() {
        return students;
    }

    // used by Driver class to populate each Student with correct neighbors and corresponding prices
    public void setEdge(Student curr, Student neighbor, Integer price) {
        curr.setNeighborAndPrice(neighbor, price);
    }

    // used by Driver.java and sets students to reference an ArrayList of all Students
    public void setAllNodesArray(ArrayList<Student> x) {
        students = x;
    }
}
