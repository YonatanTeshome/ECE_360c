/*
 * Name: Yonatan Teshome
 * EID: YH23572
 */

import java.util.*;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {


    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching problem) {
        /* TODO implement this function */
        int numSchools = problem.getHighSchoolCount();
        int numStudents = problem.getStudentCount();

        ArrayList<Integer> studentMatching = problem.getStudentMatching();
        ArrayList<Integer> highschool_spots = problem.getHighSchoolSpots();
        ArrayList<ArrayList<Integer>> highschool_preference = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> student_preference = problem.getStudentPreference();

        if(studentMatching == null || studentMatching.isEmpty()) return false; // handle null/ empty matchings

        /*
         *Creating a rank map for comparison
         *
         * SchoolRank[hs][s] = rank of student s in highschool hs's preference list
         */
        int[][] schoolRank = new int[numSchools][numStudents];
        for (int highSchool = 0; highSchool < numSchools; highSchool++) {
            ArrayList<Integer> prefs = highschool_preference.get(highSchool);
            for (int rank = 0; rank < prefs.size(); rank++) {
                int student = prefs.get(rank);
                schoolRank[highSchool][student] = rank;
            }
            //System.out.println(Arrays.toString(schoolRank[highSchool])); //Prints the preference of highschools based on rank
        }

        /*
         * StudentRank[s][hs] = rank of school hs in student s's preference list
         */
        int[][] studentRank = new int[numStudents][numSchools];
        for (int student = 0; student < numStudents; student++) {
            ArrayList<Integer> prefs = student_preference.get(student);
            for (int rank = 0; rank < prefs.size(); rank++) {
                studentRank[student][prefs.get(rank)] = rank;
            }
            //System.out.println(Arrays.toString(studentRank[student])); //Prints the preference of students based on rank
        }

        /*
         * Build map of which students are currently matched to each school
         */
        Map<Integer, List<Integer>> schoolMatches = new HashMap<>();
        for (int school = 0; school < numSchools; school++) {
            schoolMatches.put(school, new ArrayList<>());
        }

        for (int student = 0; student < numStudents; student++) {

            int matchedSchool = studentMatching.get(student);
            if (matchedSchool != -1) {
                schoolMatches.get(matchedSchool).add(student);
            }
        }

        /*
         * Instability type 1: Unmatched student s' is preferred over matched student s at h high school
         */
        for (int s = 0; s < numStudents; s++) {
            if (studentMatching.get(s) != -1) continue; //filters for unmatched students

            for (int h = 0; h < numSchools; h++) {
                List<Integer> matchedStudents = schoolMatches.get(h);
                if (!highschool_preference.get(h).contains(s)) continue; // school h doesn't want student s

                for(int current : matchedStudents) {
                    if (schoolRank[h][s] < schoolRank[h][current]) {
                        return false; // school h prefers unmatched student s over their current student match
                    }
                }
            }
        }

        /*
         * Instability type 2: Mutually preferred switch
         *
         * school h1 prefers student s1 over student s2
         *              And
         * student s1 prefers school h1 over school h2
         */
        for (int s1 = 0; s1 < numStudents; s1++) {
            int h1 = studentMatching.get(s1);
            if (h1 == -1) continue;

            for (int s2 = 0; s2 < numStudents; s2++) {
                if (s1 == s2) continue; // makes sure students s1 and s2 are not the same
                int h2 = studentMatching.get(s2);
                if (h2 == -1) continue;

                boolean h1PrefS2 = schoolRank[h1][s2] < schoolRank[h1][s1]; // The lower the rank the better
                boolean s2PrefH1 = studentRank[s2][h1] < studentRank[s2][h2]; // The lower the rank the better

                if(h1PrefS2 && s2PrefH1) { // both had to be true since its mutual
                    return false;
                }
            }
        }
        return true; // No instabilities found
    }

    /*
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        /* TODO implement this function */
        int numSchools = problem.getHighSchoolCount();
        int numStudents = problem.getStudentCount();

        ArrayList<Integer> highschool_spots = problem.getHighSchoolSpots();
        ArrayList<ArrayList<Integer>> highschool_preference = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> student_preference = problem.getStudentPreference();

        /*
         * Initialize student matches to -1 so everyone is unmatched
         */
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(numStudents, -1));

        /*
         * Creating a rank lookup for comparisons of offers
         */
        int[][] schoolRank = new int[numSchools][numStudents];
        for (int school = 0; school < numSchools; school++) {
            ArrayList<Integer> prefs = highschool_preference.get(school);
            for (int rank = 0; rank < prefs.size(); rank++) {
                schoolRank[school][prefs.get(rank)] = rank; // the rank of student s in schools h's preference list
            }
        }

        /*
         * Proposer status
         *             And
         * Track accepted students at each school
         */
        int[] nextProposal = new int[numStudents]; // proposers are the schools due to the limited spots
        Queue<Integer> freeStudents = new LinkedList<>(); // track schools that will want students

        for (int student = 0; student < numStudents; student++) {
            freeStudents.add(student); // all students start free
        }

        List<Set<Integer>> acceptedStudents = new ArrayList<>();
        for (int school = 0; school < numSchools; school++) {
            acceptedStudents.add(new HashSet<>());
        }

        /*
         * Proposal loop
         */
        while (!freeStudents.isEmpty()) {
            int student = freeStudents.poll(); // .poll returns the element at the front of the queue

            if (nextProposal[student] >= student_preference.get(student).size()) continue;

            int school = student_preference.get(student).get(nextProposal[student]++); // next preferred school
            Set<Integer> accepted = acceptedStudents.get(school);

            if (accepted.size() < highschool_spots.get(school)) { // accept is school has room
                accepted.add(student);
                studentMatching.set(student, school);
            }else {
                // check if school prefers this student over the current
                int worstRank = -1;
                int worstStudent = -1;

                for (int current : accepted) {
                    int rank = schoolRank[school][current];
                    if (rank > worstRank){
                        worstRank = rank;
                        worstStudent = current;
                    }
                }

                if (schoolRank[school][student] < worstRank) {
                    accepted.remove(Integer.valueOf(worstStudent)); // replace worststudent
                    accepted.add(student);

                    studentMatching.set(student, school);
                    studentMatching.set(worstStudent, -1);

                    freeStudents.add(worstStudent);
                }else {
                    freeStudents.add(student); // school rejects student
                }
            }
        }
        problem.setStudentMatching(studentMatching);
        return problem;
    }

    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_highschooloptimal(Matching problem) {
        /* TODO implement this function */
        int numSchools = problem.getHighSchoolCount();
        int numStudents = problem.getStudentCount();

        ArrayList<Integer> highschool_spots = problem.getHighSchoolSpots();
        ArrayList<ArrayList<Integer>> highschool_preference = problem.getHighSchoolPreference();
        ArrayList<ArrayList<Integer>> student_preference = problem.getStudentPreference();

        /*
         * Initialize student matches to -1 so everyone is unmatched
         */
        ArrayList<Integer> studentMatching = new ArrayList<>(Collections.nCopies(numStudents, -1));

        /*
         * Creating a rank lookup for comparisons of offers
         */
        int[][] studentRank = new int[numStudents][numSchools];
        for (int student = 0; student < numStudents; student++) {
            ArrayList<Integer> prefs = student_preference.get(student);
            for (int rank = 0; rank < prefs.size(); rank++) {
                studentRank[student][prefs.get(rank)] = rank; // the rank of high school h in student s's preference list
            }
        }

        /*
         * High schools Proposal tracker
         *             And
         * Track the current proposed students per school for quota
         */
        int[] nextProposal = new int[numSchools]; // proposers are the schools due to the limited spots
        Queue<Integer> freeSchools = new LinkedList<>(); // track schools that will want students

        List<Set<Integer>> acceptedStudents = new ArrayList<>();
        for (int school = 0; school < numSchools; school++) {
            acceptedStudents.add(new HashSet<>()); // gives each school a set int where they can store accepted students IDs
            if (highschool_spots.get(school) > 0) {
                freeSchools.add(school); // enqueue if the school has available spots
            }
        }

        /*
         * Gale Shapley loop
         */
        while (!freeSchools.isEmpty()) { // runs till schools meet their quota
            int school = freeSchools.poll(); // .poll returns the element at the front of the queue

            ArrayList<Integer> prefs = highschool_preference.get(school);
            //int proposals = 0;

            while (acceptedStudents.get(school).size() < highschool_spots.get(school)
                                            && nextProposal[school] < prefs.size()) {

                int student = prefs.get(nextProposal[school]++);
                int currMatch = studentMatching.get(student);

                if (currMatch == -1) { // student is unmatched and accepts the school's proposal
                    studentMatching.set(student, school);
                    acceptedStudents.get(school).add(student);
                }else {
                    int currRank = studentRank[student][currMatch]; // the student is matched so has to decide between current match or new offer
                    int newRank = studentRank[student][school];

                    if (newRank < currRank) {
                        acceptedStudents.get(currMatch).remove(student); // new proposal is better so gets replaced
                        studentMatching.set(student, school);
                        acceptedStudents.get(school).add(student);
                        if (acceptedStudents.get(currMatch).size() < highschool_spots.get(currMatch)) {
                            freeSchools.add(currMatch); // requeue old school if it still has space
                        }
                    }
                    // rejected by student
                }
            }
            if (acceptedStudents.get(school).size() < highschool_spots.get(school) && nextProposal[school] < prefs.size()) {
                freeSchools.add(school);
            }
        }
        problem.setStudentMatching(studentMatching);
        return problem;
    }
}