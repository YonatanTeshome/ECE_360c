/*
 * Name: <your name>
 * EID: <your EID>
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

        /**
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

        /**
         * StudentRank[s][hs] = rank of school hs in student s's preference list
         */
        int[][] studentRank = new int[numStudents][numSchools];
        for (int student = 0; student < numStudents; student++) {
            ArrayList<Integer> prefs = student_preference.get(student);
            for (int rank = 0; rank < prefs.size(); rank++) {
                int highSchool = prefs.get(rank);
                schoolRank[student][highSchool] = rank;
            }
            //System.out.println(Arrays.toString(studentRank[student])); //Prints the preference of students based on rank
        }

        /**
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

        int spot = highschool_spots.get(0);

        for (int i = 1; i < highschool_spots.size(); i++) {
            System.out.println(highschool_preference.get(i));
        }

        int[] highschool_spots_copy1 = new int[highschool_spots.size()]; //copy of spots so we can modify
        for (int i = 0; i < highschool_spots.size(); i++) {
            highschool_spots_copy1[i] = highschool_spots.get(i);
        }
        int[] highschool_spots_copy2 = new int[highschool_spots.size()]; //copy of spots so we can modify
        for (int i = 0; i < highschool_spots.size(); i++) {
            highschool_spots_copy2[i] = highschool_spots.get(i);
        }

        int preference = highschool_preference.get(0).indexOf(3);
         */
        return false;
    }

    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        /* TODO implement this function */

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

        return problem;
    }
}