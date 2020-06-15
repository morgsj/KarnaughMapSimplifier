package src.main;

public class PrefixSumMatrix {
    private int[][] psm;

        public PrefixSumMatrix(int[][] kmap) {

            if (kmap.length == 0 || kmap[0].length == 0) return;

            psm = new int[kmap.length][kmap[0].length];

            for (int i = 0; i < kmap.length; i++) {
                for (int j = 0; j < kmap[0].length; j++) {
                    if (i == 0 && j == 0) psm[i][j] = kmap[i][j];
                    else if (i == 0 && j > 0)  psm[i][j] = psm[i][j - 1] + kmap[i][j];
                    else if (i > 0 && j == 0)  psm[i][j] = psm[i - 1][j] + kmap[i][j];
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
                if (col0 > 0)  res = res - psm(col0 - 1, row1);

                // Remove elements between (0, 0) && (xx, y-1) 
                if (row0 > 0)  res = res - psm(col1, row0 - 1);

                // Add psm(x-1)(y-1) as elements between (0, 0) 
                // && (x-1, y-1) are subtracted twice 
                if (col0 > 0 && row0 > 0)  res = res + psm(col0 - 1, row0 - 1);

                return res;

            } else {
                // overlap cases
                if (col0 > col1 && row0 <= row1 ) return sumRegion(0, row0, col1, row1) + sumRegion(col0, row0, psm.length - 1, row1);

                else if (col0 <= col1 && row0 > row1) return sumRegion(col0, 0, col1, row1) + sumRegion(col0, row0, col1, psm[0].length - 1);
                
                else return sumRegion(0, 0, col1, row1) + sumRegion(0, row0, col1, psm[0].length - 1) + sumRegion(col0, 0, psm.length - 1, row1) + sumRegion(col0, row0, psm.length - 1, psm[0].length - 1)
            }
        }
}