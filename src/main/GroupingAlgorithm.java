package src.main;

import src.common.IllegalGroupingSizeException;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class GroupingAlgorithm {

    /**
     * Finds the simplest set of groupings for a Karnaugh map.
     * @param kmap the Karnaugh map
     * @return A list of Grouping objects
     */
    public static Iterable<Grouping> findOptimalGroupings(boolean[][] kmap) {

        if (kmap == null) throw new IllegalArgumentException("Karnaugh map input was null");
        if (isMapEmpty(kmap)) return new ArrayList<>();

        // Step 1: Set up Prefix Sum Matrix
        int[][] temp = new int[kmap.length][kmap[0].length];

        for (int i = 0; i < kmap.length; i++)
            for (int j = 0; j < kmap[0].length; j++)
                temp[i][j] = kmap[i][j] ? 1 : 0;
                
        PrefixSumMatrix psm = new PrefixSumMatrix(temp);

        Grouping thisGroup;

        // Step 2: Add all possible groups
        PriorityQueue<Grouping> AllGroups = new PriorityQueue<>(new ByGroupSize());

        for (int row0 = 0; row0 < kmap.length; row0++)
            for (int col0 = 0; col0 < kmap[0].length; col0++)
                for (int row1 = 0; row1 < kmap.length; row1++)
                    for (int col1 = 0; col1 < kmap[0].length; col1++) {

                        if (col0 == col1 + 1 || row0 == row1 + 1) continue; //removes instances of unnecessary wrapping

                        try {
                            thisGroup = new Grouping(row0, col0, row1, col1, kmap[0].length, kmap.length);
                            if (psm.sumRegion(row0, col0, row1, col1) == thisGroup.size()) AllGroups.add(thisGroup);
                        } catch (IllegalGroupingSizeException ignored) {}

                    }

        // Step 3: Remove groups until K-map is empty
        ArrayList<Grouping> FinalGroups = new ArrayList<>();
        PriorityQueue<Grouping> GroupsOfSameSize = new PriorityQueue<>(new ByNumSquares(kmap));

        Grouping reserve = AllGroups.poll();
        int currentSize;
        Grouping currentGroup;

        while (!isMapEmpty(kmap)) {
            currentSize = reserve.size();
            while (reserve.size() == currentSize) {
                GroupsOfSameSize.add(reserve);
                if (AllGroups.isEmpty()) break;
                reserve = AllGroups.poll();
            }

            while (!(GroupsOfSameSize.isEmpty() || isMapEmpty(kmap))) {
                currentGroup = GroupsOfSameSize.poll();
                if (currentGroup.mapSection(kmap) != 0) {
                    FinalGroups.add(currentGroup);
                    removeFromKmap(currentGroup, kmap);
                }
            }
        }

        return FinalGroups;
    }

    /**
     * Finds the simplest set of groupings for a Karnaugh map.
     * @param kmap the Karnaugh map
     * @return A list of Grouping objects
     */
    public static Iterable<Grouping> findOptimalGroupings(int[][] kmap) {
        return findOptimalGroupings(toBooleanArray(kmap));
    }

    private static boolean[][] toBooleanArray(int[][] arr) {
        boolean[][] map = new boolean[arr.length][arr[0].length];

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                map[i][j] = arr[i][j] == 1;

        return map;
    }

    private static void removeFromKmap(Grouping currentGroup, boolean[][] kmap) {
        int r0 = currentGroup.getStartRow();
        int r1 = currentGroup.getEndRow();
        int c0 = currentGroup.getStartCol();
        int c1 = currentGroup.getEndCol();

        for (int row = r0; row <= (r1 >= r0 ? r1 : r0 + kmap.length); row++)
                for (int col = c0; col <= (c1 >= c0 ? c1 : c0 + kmap[0].length); col++)
                    kmap[row % kmap.length][col % kmap[0].length] = false;
    }

    private static boolean isMapEmpty(boolean[][] map) {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                if (map[i][j]) return false;

        return true;
    }

    private static class ByGroupSize implements Comparator<Grouping> {
        public int compare(Grouping v, Grouping w) {
            if (v == null) return -1;
            if (w == null) return 1;
            return -v.compareTo(w);
        }
    }

    private static class ByNumSquares implements Comparator<Grouping> {
        private final boolean[][] kmap;

        public ByNumSquares(boolean[][] k)
            {kmap = k;}

        public int compare(Grouping v, Grouping w) {
            if (v == null) return -1;
            if (w == null) return 1;

            int vm = v.mapSection(kmap), wm = w.mapSection(kmap);
            return -Integer.compare(vm, wm);
        }
    }

    private static class PrefixSumMatrix {
        private int[][] psm;

        /**
         * Create a new prefix sum matrix for the Karnaugh Map
         * @param kmap The karnaugh map
         */
        public PrefixSumMatrix(int[][] kmap) {

            if (kmap.length == 0 || kmap[0].length == 0) return;

            psm = new int[kmap.length][kmap[0].length];

            for (int i = 0; i < kmap.length; i++) {
                for (int j = 0; j < kmap[0].length; j++) {
                    if (i == 0 && j == 0) psm[i][j] = kmap[i][j];
                    else if (i == 0)  psm[i][j] = psm[i][j - 1] + kmap[i][j];
                    else if (j == 0)  psm[i][j] = psm[i - 1][j] + kmap[i][j];
                    else psm[i][j] = psm[i - 1][j] + psm[i][j - 1] - psm[i - 1][j - 1] + kmap[i][j] ;
                }
            }
        }

        /**
         * Sums a region of the Karnaugh map in constant time
         *
         * @param col0 The x coordinate of the top left corner
         * @param row0 The y coordinate of the top left corner
         * @param col1 The x coordinate of the bottom right corner
         * @param row1 The x coordinate of the bottom right corner
         * @return the sum of the region
         */
        public int sumRegion(int col0, int row0, int col1, int row1) {
            if (psm.length == 0) return 0;

            if (col0 <= col1 && row0 <= row1) {

                int res = psm[col1][row1];

                // Remove elements between (0, 0) && (x-1, yy)
                if (col0 > 0)  res = res - psm[col0 - 1][row1];

                // Remove elements between (0, 0) && (xx, y-1)
                if (row0 > 0)  res = res - psm[col1][row0 - 1];

                // Add psm(x-1)(y-1) as elements between (0, 0)
                // && (x-1, y-1) are subtracted twice
                if (col0 > 0 && row0 > 0)  res = res + psm[col0 - 1][row0 - 1];

                return res;

            } else {
                // overlap cases
                if (col0 > col1 && row0 <= row1 )
                    return sumRegion(0, row0, col1, row1) +
                            sumRegion(col0, row0, psm.length - 1, row1);

                else if (col0 <= col1)
                    return sumRegion(col0, 0, col1, row1) +
                            sumRegion(col0, row0, col1, psm[0].length - 1);

                else
                    return sumRegion(0, 0, col1, row1) +
                            sumRegion(0, row0, col1, psm[0].length - 1) +
                            sumRegion(col0, 0, psm.length - 1, row1) +
                            sumRegion(col0, row0, psm.length - 1, psm[0].length - 1);
            }
        }
    }
}