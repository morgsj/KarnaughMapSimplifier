package src.main;

import src.common.IllegalGroupingSizeException;

import java.util.Comparator;
import java.util.ArrayList;

public class GroupingAlgorithm {

    /**
     * Finds the simplest groupings for a Karnaugh map.
     * @param kmap the Karnaugh map
     * @return a minimal list of Grouping objects
     */
    public static Iterable<Grouping> findOptimalGroupings(boolean[][] kmap) {

        // Step 1: set up Prefix Sum Matrix
        int[][] temp = new int[kmap.length][kmap[0].length];

        for (int i = 0; i < kmap.length; i++)
            for (int j = 0; j < kmap[0].length; j++)
                temp[i][j] = kmap[i][j] ? 1 : 0;
                
        PrefixSumMatrix pS = new PrefixSumMatrix(temp);

        Grouping thisGroup;

        // Step 2: Add all possible groups
        MaxPQ<Grouping> AllGroups = new MaxPQ<Grouping>(new ByGroupSize());

        for (int row0 = 0; row0 < kmap.length; row0++)
            for (int col0 = 0; col0 < kmap[0].length; col0++)
                for (int row1 = 0; row1 < kmap.length; row1++)
                    for (int col1 = 0; col1 < kmap[0].length; col1++) {

                        if (col0 == col1 + 1 || row0 == row1 + 1) continue; //removes instances of unnecessarry wrapping

                        try {
                            thisGroup = new Grouping(col0, row0, col1, row1, kmap[0].length, kmap.length);
                            if (pS.sumRegion(row0, col0, row1, col1) == thisGroup.size()) AllGroups.insert(thisGroup);
                        } catch (IllegalGroupingSizeException ignored) {}
                    }

        // Step 3: Remove groups until K-map is empty
        ArrayList<Grouping> FinalGroups = new ArrayList<Grouping>();
        MaxPQ<Grouping> GroupsOfSameSize = new MaxPQ<Grouping>(new ByNumSquares(kmap));

        Grouping reserve = AllGroups.delMax(), currentGroup;
        int size;

        while (!isMapEmpty(kmap)) {

            size = reserve.size();
            while (reserve.size() == size) {
                GroupsOfSameSize.insert(reserve);
                if (AllGroups.isEmpty()) break;
                reserve = AllGroups.delMax();
            }

            while (!(GroupsOfSameSize.isEmpty() || isMapEmpty(kmap))) {
                currentGroup = GroupsOfSameSize.delMax();
                if (currentGroup.mapSection(kmap) != 0) {
                    FinalGroups.add(currentGroup);
                    removeFromKmap(currentGroup, kmap);
                }
            }
        }
        return FinalGroups;
    }

    private static void removeFromKmap(Grouping currentGroup, boolean[][] kmap) {

        int row, col;

        if (currentGroup.getStartCol() <= currentGroup.getEndCol() && currentGroup.getStartRow() <= currentGroup.getEndRow())
            for (col = currentGroup.getStartCol(); col <= currentGroup.getEndCol(); col++)
                for (row = currentGroup.getStartRow(); row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;

        if (currentGroup.getStartCol() > currentGroup.getEndCol() && currentGroup.getStartRow() <= currentGroup.getEndRow())  {
            for (col = 0; col <= currentGroup.getEndCol(); col++)
                for (row = currentGroup.getStartRow(); row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;

            for (col = currentGroup.getStartCol(); col < currentGroup.dimension().width; col++)
                for (row = currentGroup.getStartRow(); row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;
        }

        if (currentGroup.getStartCol() <= currentGroup.getEndCol() && currentGroup.getStartRow() > currentGroup.getEndRow()) {
            for (col = currentGroup.getStartCol(); col <= currentGroup.getEndCol(); col++)
                for (row = 0; row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;
                    
            for (col = currentGroup.getStartCol(); col <= currentGroup.getEndCol(); col++)
                for (row = currentGroup.getStartRow(); row < currentGroup.dimension().height; row++)
                    kmap[row][col] = false;
        }

        if (currentGroup.getStartCol() > currentGroup.getEndCol() && currentGroup.getStartRow() > currentGroup.getEndRow()) {
            for (col = 0; col <= currentGroup.getEndCol(); col++)
                for (row = 0; row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;
                    
            for (col = 0; col <= currentGroup.getEndCol(); col++)
                for (row = currentGroup.getStartRow(); row < currentGroup.dimension().height; row++)
                    kmap[row][col] = false;
                    
            for (col = currentGroup.getStartCol(); col < currentGroup.dimension().width; col++)
                for (row = 0; row <= currentGroup.getEndRow(); row++)
                    kmap[row][col] = false;
                    
            for (col = currentGroup.getStartCol(); col < currentGroup.dimension().width; col++)
                for (row = currentGroup.getStartRow(); row < currentGroup.dimension().height; row++)
                    kmap[row][col] = false;            
        }
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
            return v.compareTo(w);
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
            if (vm > wm) return 1;
            else if (wm > vm) return -1;
            else return 0;
        }
    }
}