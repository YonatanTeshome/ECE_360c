/*
 * Name: Yonatan Teshome
 * EID: YH23572
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

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
public class Program3 extends AbstractProgram3 {
    private static int ceilHalf (int span) {
        return (span + 1) / 2;
    }
    /**
     * Determines the solution of the optimal response time for the given input TownPlan. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return Updated TownPlan town with the "responseTime" field set to the optimal response time
     */
    @Override
    public TownPlan findOptimalResponseTime(TownPlan town) {
        // SORTED HOUSE POSITIONS, SIZE N
        ArrayList<Integer> x = town.getHousePositions();
        int n = (x == null) ? 0 : x.size();

        // NUMBER OF POLICE STATIONS, NUMPOLICE
        int numPolice = town.getStationCount();

        // HEAD CASES
        if (n == 0 || numPolice <= 0) {
            town.setResponseTime(0);
            return town;
        }

        if (numPolice >= n) { // IF WE HAVE AT LEAST AS MANY STATIONS AS HOUSES, EACH HOUSE CAN GET ITS OWN STATION
            town.setResponseTime(0);
            return town;
        }

        final int INF = Integer.MAX_VALUE / 4; // AVOID OVERFLOW ON +1
        int[][] R = new int[n][numPolice + 1];

        for (int t = 0; t < n; t++) {
            Arrays.fill(R[t], INF);
        }

        for (int t = 0; t < n; t++) {
            int span = x.get(t) - x.get(0);
            R[t][1] = ceilHalf(span);
        }

        for (int j = 2; j <= numPolice; j++) {
            for (int t = 0; t < n; t++) {

                int best = INF;

                //  SPLITS WHERE THE LAST STATION COVERS
                for (int i = 0; i <= t; i++) {
                    int prev = (i > 0) ? R[i - 1][j - 1] : 0;
                    int span = x.get(t) - x.get(i);
                    int block = ceilHalf(span);
                    int cand = Math.max(prev, block);

                    if (cand < best) {
                        best = cand;
                    }
                }

                R[t][j] = best;
            }
        }

        town.setResponseTime(R[n - 1][numPolice]);
        return town;
    }

    /**
     * Determines the solution of the set of police station positions that optimize response time for the given input TownPlan. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return Updated TownPlan town with the "policeStationPositions" field set to the optimal police station positions
     */
    @Override
    public TownPlan findOptimalPoliceStationPositions(TownPlan town) {

        ArrayList<Integer> x = town.getHousePositions();
        int numHouses = (x == null) ? 0 : x.size();
        int numStations = town.getStationCount();

        ArrayList<Integer> stations = new ArrayList<>();

        // EDGE CASE
        if (numHouses == 0 ||  numStations <= 0) {
            town.setPoliceStationPositions(stations);
            return town;
        }
        if ( numStations >= numHouses) {
            for (int i = 0; i <  numStations; i++) stations.add(x.get(i));
            town.setPoliceStationPositions(stations);
            return town;
        }

        final int INF = Integer.MAX_VALUE / 4;
        int[][] R = new int[numHouses][ numStations + 1]; // SAME DP FOR RESPONCE TIME
        int[][] cut = new int[numHouses][ numStations + 1]; // REMEMBERS THE BEST STARTING INDEX

        for (int t = 0; t < numHouses; t++) {Arrays.fill(R[t], INF);}

        for (int t = 0; t < numHouses; t++) {
            int span = x.get(t) - x.get(0);
            R[t][1] = ceilHalf(span);
            cut[t][1] = 0;
        }

        // OPTIMAL CUTS
        for (int j = 2; j <=  numStations; j++) {
            for (int t = 0; t < numHouses; t++) {
                int best = INF;
                int bestIndex = 0;

                for (int i = 0; i <= t; i++) {
                    int prev = (i > 0) ? R[i - 1][j - 1] : 0;
                    int span = x.get(t) - x.get(i);
                    int block = ceilHalf(span);
                    int cand = Math.max(prev, block);

                    if (cand < best || (cand == best && i < bestIndex)) {
                        best = cand;
                        bestIndex = i; // REMEMBER THE START OF THE LAST SEGMENT
                    }
                }

                R[t][j] = best;
                cut[t][j] = bestIndex;
            }
        }

        // RECOVER K SEGMENTS FROM RIGHT TO LEFT AND PLACE POLICE STATION IN THE MIDDLE
        int tIndex = numHouses - 1;
        int j =  numStations;
        while (j >= 1) {
            int i = cut[tIndex][j]; // SEGMENTS FROM I TO TINDEX
            int center = (x.get(i) + x.get(tIndex)) / 2;
            stations.add(center); // COLLECT FROM RIGHT SIDE
            tIndex = i - 1; // END AT PREV SEGMENTS LAST INDEX
            j--;
        }

        // BUILT THE POSITIONS FROM RIGHT TO LEFT
        // NEED TO REVERSE SO ITS IN ASCENDING ORDER
        Collections.reverse(stations);
        town.setPoliceStationPositions(stations);
        return town;
    }
}
