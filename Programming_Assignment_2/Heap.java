/*
 * Name: Yonatan Teshome
 * EID: YH23572
 */

// Implement your heap here
// Methods may be added to this file, but don't remove anything
// Include this file in your final submission

import java.util.ArrayList;

public class Heap {
    private ArrayList<Student> minHeap;

    public Heap() {
        minHeap = new ArrayList<Student>();
    }
    // USE AFTER REMOVING THE ROOT OF MOVING A LARGE ELEMENT TO TOP
    private void heapifyDown(int i) {
        int smallest = i; // ASSUME CURRENT NODE IS SMALLEST
        int left = 2*i + 1;
        int right = 2*i + 2;

        // COMPARE WITH LEFT CHILD
        if (left < minHeap.size() && isSmaller(minHeap.get(left), minHeap.get(smallest))) {
            smallest = left;
        }
        // COMPARE WITH RIGHT CHILD
        if (right < minHeap.size() && isSmaller(minHeap.get(right), minHeap.get(smallest))) {
            smallest = right;
        }

        if (smallest != i) {
            swap(i, smallest);
            heapifyDown(smallest); // RECURSIVE
        }
    }
    // COMPARING TWO STUDENTS AND RETURNS TRUE IF a<b IN HEAP
    private boolean isSmaller(Student a, Student b) {
        if (a.getminCost() != b.getminCost()) {
            return a.getminCost() < b.getminCost();
        }
        return a.getName() < b.getName(); // THERE IS A TIE SO GO OFF OF STUDENT NAME
    }
    // SWAPS TWO ELEMENTS IN THE HEAP
    private void swap(int i, int j) {
        Student temp = minHeap.get(i);
        minHeap.set(i, minHeap.get(j));
        minHeap.set(j, temp);
    }

    public int length() {
        return minHeap.size();
    }

    private void heapifyUp(int i) {
        int parent = (i-1) / 2;
        while (i > 0 && isSmaller(minHeap.get(i), minHeap.get(parent))) {
            swap(i, parent);
            i = parent;
            parent = (i-1) / 2;
        }
    }
    /**
     * buildHeap(ArrayList<Student> students)
     * Given an ArrayList of Students, build a min-heap keyed on each Student's minCost
     * Time Complexity - O(nlog(n)) or O(n)
     *
     * @param students
     */
    public void buildHeap(ArrayList<Student> students) {

//        minHeap = new ArrayList<>(students); // COPY STUDENTS INTO THE HEAP
//        for (int i = minHeap.size() / 2 -1; i >= 0; i--) { // i IS THE LAST NON-LEAF NODE (n/2-1)
//            heapifyDown(i); // MOVES NODE DOWN TILL MIN HEAP IS RESTORED
//        }
        minHeap = new ArrayList<>();
        for (Student s : students) {
            insertNode(s);
        }
    }

    /**
     * insertNode(Student in)
     * Insert a Student into the heap.
     * Time Complexity - O(log(n))
     *
     * @param in - the Student to insert.
     */
    public void insertNode(Student in) {
        minHeap.add(in); // ADD TO END
        heapifyUp(minHeap.size() - 1); // MOVE UP TILL HEAP IS IN PROPER ORDER
    }

    /**
     * findMin()
     * Time Complexity - O(1)
     *
     * @return the minimum element of the heap.
     */
    public Student findMin() {
        if (minHeap.isEmpty()) return null;
        return minHeap.get(0);
    }

    /**
     * extractMin()
     * Time Complexity - O(log(n))
     *
     * @return the minimum element of the heap, AND removes the element from said heap.
     */
    public Student extractMin() {
        if (minHeap.size() == 0) return null;
        Student min = minHeap.get(0);
        Student last = minHeap.remove(minHeap.size() - 1); // MOVE LAST ELEMENT TO ROOT AND REMOVE LAST

        if (minHeap.size() > 0) {
            minHeap.set(0,last);
            heapifyDown(0);
        }
        return min;
    }

    /**
     * delete(int index)
     * Deletes an element in the min-heap given an index to delete at.
     * Time Complexity - O(log(n))
     *
     * @param index - the index of the item to be deleted in the min-heap.
     */
    public void delete(int index) {
        if(index < 0 || index >= minHeap.size()) return;

        // MOVE LAST ELEMENT TO THE INDEX
        Student last = minHeap.remove(minHeap.size() - 1);

        if (index < minHeap.size()) {
            minHeap.set(index, last);
            // RESTORE HEAP PROPERTY
            heapifyDown(index);
            heapifyUp(index);
        }
    }

    /**
     * changeKey(Student r, int newCost)
     * Changes minCost of Student s to newCost and updates the heap.
     * Time Complexity - O(log(n))
     *
     * @param r       - the Student in the heap that needs to be updated.
     * @param newCost - the new cost of Student r in the heap (note that the heap is keyed on the values of minCost)
     */
    public void changeKey(Student r, int newCost) {
        int index = minHeap.indexOf(r);
        if (index == -1) return; // STUDENT NOT FOUND
        delete(index);
        r.setminCost(newCost);
        insertNode(r);

//        int oldCost = minHeap.get(index).getminCost();
//        minHeap.get(index).setminCost(newCost);
//
//        // DECIDED THE DIRECTION TO HEAPIFY
//        if (newCost < oldCost) {
//            heapifyUp(index);
//        } else if (newCost > oldCost) {
//            heapifyDown(index);
//        }
    }

    public String toString() {
        String output = "";
        for (int i = 0; i < minHeap.size(); i++) {
            output += minHeap.get(i).getName() + " ";
        }
        return output;
    }

///////////////////////////////////////////////////////////////////////////////
//                           DANGER ZONE                                     //
//                everything below is used for grading                       //
//                      please do not change :)                              //
///////////////////////////////////////////////////////////////////////////////

    public ArrayList<Student> toArrayList() {
        return minHeap;
    }
}
