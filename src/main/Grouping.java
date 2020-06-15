package src.main;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *  The {@code Grouping} class represents a single grouping
 *  on a Karnaugh map with given size. It is able to deal with
 *  groups that wrap around the edge of the map.
 *
 *  @author Morgan Jones
 *
 */
public class Grouping implements Comparable<Grouping> {

        public int startCol, endCol, startRow, endRow, mapWidth, mapHeight;

        /**
         * Initialises a new grouping
         *
         * @param startCol the col coordinate of the left edge of the group
         * @param endCol the col coordinate of the right edge of the group
         * @param startRow the y coordinate of the top edge of the group
         * @param endRow the y coordinate of the bottom edge of the group
         * @param mapWidth the width of the Karnaugh Map
         * @param mapHeight the height of the Karnaugh Map
         */
        public Grouping(int startCol, int endCol, int startRow, int endRow, int mapWidth, int mapHeight) {
            if (mapWidth <= 0) throw new IllegalArgumentException("mapWidth was 0 or less: " + mapWidth);
            if (mapHeight <= 0) throw new IllegalArgumentException("mapHeight was 0 or less: " + mapHeight);

            // check Karnaugh map dimensions are a power of two
            double logWidth = Math.log(mapWidth) / Math.log(2);
            double logHeight = Math.log(mapHeight) / Math.log(2);
            if (logWidth != Math.round(logWidth)) throw new IllegalArgumentException("mapWidth was not a power of 2: " + mapWidth);
            if (logHeight != Math.round(logHeight)) throw new IllegalArgumentException("mapHeight was not a power of 2: " + mapHeight);

            if (startCol < 0 || startCol > mapWidth) throw new IllegalArgumentException("startCol was out of range: " + startCol);
            if (endCol < 0 || endCol > mapWidth) throw new IllegalArgumentException("endCol was out of range: " + endCol);
            if (startRow < 0 || startRow > mapHeight) throw new IllegalArgumentException("startRow was out of range: " + startRow);
            if (endRow < 0 || endRow > mapHeight) throw new IllegalArgumentException("endCol was out of range: " + endRow);

            this.startCol = startCol;
            this.endCol = endCol;
            this.startRow = startRow;
            this.endRow = endRow;
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
        }


        /**
         * The size of the group
         * 
         * @return The number of squares that is contained within the group
         */
        public int size() {
            if (startCol <= endCol && startRow <= endRow) return (endCol - startCol + 1) * (endRow - startRow + 1); // no overlap case
            else if (startCol > endCol && startRow <= endRow) return (mapWidth - startCol + endCol + 1) * (endRow - startRow + 1); // col overlap case
            else if (startCol <= endCol && startRow > endRow) return (endCol - startCol + 1) * (mapHeight - startRow + endRow + 1); // y overlap case
            else return (mapWidth - startCol + endCol + 1) * (mapHeight - startRow + endRow + 1); // double overlap case
        }

        /**
         * Determines if a grouping is of the size 2^m by 2^n, which is a requirement
         * 
         * @return True/False
         */
        public boolean isSquare() {
            Double a;
            
            if (startCol > endCol) a = Math.log(mapWidth - startCol + endCol + 1) / Math.log(2);
            else a = Math.log(endCol - startCol + 1) / Math.log(2);

            if (a != Math.round(a)) return false;

            if (startRow > endRow) a = Math.log(mapHeight - startRow + endRow + 1) / Math.log(2);
            else a = Math.log(endRow - startRow + 1) / Math.log(2);

            if (a != Math.round(a)) return false;

            return true;
        }

        // ''' <summary>
        // ''' Compares two groupings by their size.
        // ''' </summary>
        // ''' <param name="w">The grouping to compare Me to</param>
        // ''' <returns>-1 (less than w), 0 (equal), or 1 (greater than w)</returns>
        

        /**
         * Compares two groupings based off their {@code size()}
         */
        public int compareTo(Grouping that) {
            int thisSize = this.size();
            int thatSize = that.size();

            if (thisSize > thatSize) return 1;
            else if (thisSize < thatSize) return -1;
            else return 0;
        }

        /**
         * Counts the number of 1s in the grouping.
         * 
         * @param map the Karnaugh Map
         * @return the number of 1s
         */
        public int mapSection(boolean[][] map) {
            if (map.length <= 0 || map[0].length <= 0) throw new IllegalArgumentException("Map is not of appropriate dimensions");

            if (map.length <= startCol || map[0].length <= startRow) throw new IllegalArgumentException("Grouping goes outside argument map");

            int count = 0, col, row;

            if (startCol <= endCol && startRow <= endRow) 
                for (col = startCol; col <= endCol; col++)
                    for (row = startRow; row <= endRow; row++)
                        if (map[col][row] != false) count++;
            

            // col overlap
            if (startCol > endCol && startRow <= endRow) {
                for (col = 0; col <= endCol; col++)
                    for (row = startRow; row <= endRow; row++)
                        if (map[col][row] != false) count++;

                for (col = startCol; col <= mapWidth - 1; col++)
                    for (row = startRow; row <= endRow; row++)
                        if (map[col][row] != false) count++;
            }

            // row overlap
            if (startCol <= endCol && startRow > endRow) {
                for (col = startCol; col <= endCol; col++)
                    for (row = 0; row <= endRow; row++)
                        if (map[col][row] != false) count++;
                        
                for (col = startCol; col <= endCol; col++)
                    for (row = startRow; row <= mapHeight - 1; row++)
                        if (map[col][row] != false) count++;
            }
            
            // col and row overlap
            if (startCol > endCol && startRow > endRow) {
                for (col = 0; col <= endCol; col++)
                    for (row = 0; row <= endRow; row++)
                        if (map[col][row] != false)  count++;
                        
                for (col = 0; col <= endCol; col++)
                    for (row = startRow; row <= mapHeight - 1; row++)
                        if (map[col][row] != false)  count++;

                for (col = startCol; col <= mapWidth - 1; col++)
                    for (row = 0; row <= endRow; row++)
                        if (map[col][row] != false)  count++;

                for (col = startCol; col <= mapWidth - 1; col++)
                    for (row = startRow; row <= mapHeight - 1; row++)
                        if (map[col][row] != false)  count++;
            }
            

            return count;
        }

        /**
         * Checks if two Groupings are the same.
         * 
         * @param obj The object (should be a Grouping) to compare
         * @return True/False
         */
        public boolean equals(Object o) {
            if (o.getClass() == this.getClass()) {
                Grouping obj = (Grouping)o;
                return (startCol == obj.startCol && endCol == obj.endCol && startRow == obj.startRow && endRow == obj.endRow);
            }
            else return false;
        }

        /**
         * Returns a list of points that are contained in a grouping. 
         * This is often useful for displaying the group.
         * @return List of points
         */
        public Iterable<Point> pointsInGroup() {
            ArrayList<Point> l = new ArrayList<Point>();

            int i, j;

            if (startCol <= endCol && startRow <= endRow )
                for (i = startCol; i <= endCol; i++)
                    for (j = startRow; j <= endRow; j++)
                        l.add(new Point(i, j));
            

            if (startCol <= endCol && startRow > endRow) {
                for (i = startCol; i <= endCol; i++) {
                    for (j = startRow; j <= mapHeight - 1; j++)
                        l.add(new Point(i, j))
                    for (j = 0; j <= endRow; j++)
                        l.add(new Point(i, j));
                }
            }

            if (startCol > endCol && startRow <= endRow) { 
                for (i = startCol; i <= mapWidth - 1; i++)
                    for (j = startRow; j <= endRow; j++)
                        l.add(new Point(i, j));
                for (i = 0; i <= endCol; i++) 
                    for (j = startRow; j <= endRow; j++)
                        l.add(new Point(i, j));
            }
            

            if (startCol > endCol && startRow > endRow) {
                for (i = startCol; i <= mapWidth - 1; i++) {
                    for (j = startRow; j <= mapHeight - 1; j++)
                        l.add(new Point(i, j));
                        for (j = 0; j <= endRow; j++)
                        l.add(new Point(i, j));
                }
                for (i = 0; i <= endCol; i++) {
                    for (j = startRow; j <= mapHeight - 1; j++)
                        l.add(new Point(i, j));
                    for (j = 0; j <= endRow; j++)
                        l.add(new Point(i, j));
                }
            }
            
            return l;
        }

        /**
         * @return string representation of grouping 
         */
        public String toString() 
        {return "Grouping: (" + startCol + ", " + startRow + ") ~ (" + endCol + ", " + endRow + ")";}
}