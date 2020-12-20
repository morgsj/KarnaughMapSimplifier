package src.main;

import src.common.IllegalGroupingSizeException;

import java.awt.*;
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

    private final int startCol;
    private final int endCol;
    private final int startRow;
    private final int endRow;
    private final int mapWidth;
    private final int mapHeight;

    /**
     * Initialises a new grouping
     * @param startRow the row of the top edge of the group
     * @param startCol the column of the left edge of the group
     * @param endRow the row of the bottom edge of the group
     * @param endCol the column of the right edge of the group
     * @param mapWidth the width of the Karnaugh Map
     * @param mapHeight the height of the Karnaugh Map
     */
    public Grouping(int startRow, int startCol, int endRow, int endCol, int mapWidth, int mapHeight) throws IllegalGroupingSizeException {
        if (mapWidth <= 0) throw new IllegalArgumentException("mapWidth was 0 or less: " + mapWidth);
        if (mapHeight <= 0) throw new IllegalArgumentException("mapHeight was 0 or less: " + mapHeight);

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

        if (!this.isValidSize()) throw new IllegalGroupingSizeException();
    }

    /**
     * Determines if a grouping is of the size 2^m by 2^n.
     * @return True/False
     */
    private boolean isValidSize() {
        // stores the log of the width or height to determine if it is a power of 2
        double a;

        if (startCol > endCol) a = Math.log(mapWidth - startCol + endCol + 1) / Math.log(2);
        else a = Math.log(endCol - startCol + 1) / Math.log(2);

        if (a != Math.round(a)) return false;

        if (startRow > endRow) a = Math.log(mapHeight - startRow + endRow + 1) / Math.log(2);
        else a = Math.log(endRow - startRow + 1) / Math.log(2);

        return a == Math.round(a);
    }

    /**
     * The size of the group
     * @return The number of squares that is contained within the group
     */
    public int size() {
        if (startCol <= endCol && startRow <= endRow) return (endCol - startCol + 1) * (endRow - startRow + 1); // no overlap case
        else if (startCol > endCol && startRow <= endRow) return (mapWidth - startCol + endCol + 1) * (endRow - startRow + 1); // col overlap case
        else if (startCol <= endCol && startRow > endRow) return (endCol - startCol + 1) * (mapHeight - startRow + endRow + 1); // y overlap case
        else return (mapWidth - startCol + endCol + 1) * (mapHeight - startRow + endRow + 1); // double overlap case
    }

    /**
     * Compares two groupings based off their {@code size()}
     * @param that the grouping we are comparing this grouping to
     * @return -1 (less than w), 0 (equal), or 1 (greater than w)
     */
    public int compareTo(Grouping that) {
        return Integer.compare(this.size(), that.size());
    }

    /**
     * Counts the number of 1s in the grouping.
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
                    if (map[row][col]) count++;


        // col overlap
        if (startCol > endCol && startRow <= endRow) {
            for (col = 0; col <= endCol; col++)
                for (row = startRow; row <= endRow; row++)
                    if (map[row][col]) count++;

            for (col = startCol; col <= mapWidth - 1; col++)
                for (row = startRow; row <= endRow; row++)
                    if (map[row][col] != false) count++;
        }

        // row overlap
        if (startCol <= endCol && startRow > endRow) {
            for (col = startCol; col <= endCol; col++)
                for (row = 0; row <= endRow; row++)
                    if (map[row][col] != false) count++;

            for (col = startCol; col <= endCol; col++)
                for (row = startRow; row <= mapHeight - 1; row++)
                    if (map[row][col] != false) count++;
        }

        // col and row overlap
        if (startCol > endCol && startRow > endRow) {
            for (col = 0; col <= endCol; col++)
                for (row = 0; row <= endRow; row++)
                    if (map[row][col])  count++;

            for (col = 0; col <= endCol; col++)
                for (row = startRow; row <= mapHeight - 1; row++)
                    if (map[row][col])  count++;

            for (col = startCol; col <= mapWidth - 1; col++)
                for (row = 0; row <= endRow; row++)
                    if (map[row][col])  count++;

            for (col = startCol; col <= mapWidth - 1; col++)
                for (row = startRow; row <= mapHeight - 1; row++)
                    if (map[row][col])  count++;
        }

        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Grouping) {
            Grouping obj = (Grouping)o;
            return (startCol == obj.startCol && endCol == obj.endCol && startRow == obj.startRow && endRow == obj.endRow);
        }
        else return false;
    }

    /**
     * Returns a list of points that are contained in a grouping.
     * @return List of points
     */
    public Iterable<Point> pointsInGroup() {
        ArrayList<Point> l = new ArrayList<>();

        int i, j;

        if (startCol <= endCol && startRow <= endRow )
            for (i = startCol; i <= endCol; i++)
                for (j = startRow; j <= endRow; j++)
                    l.add(new Point(i, j));


        if (startCol <= endCol && startRow > endRow) {
            for (i = startCol; i <= endCol; i++) {
                for (j = startRow; j <= mapHeight - 1; j++)
                    l.add(new Point(i, j));
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
     * Gets the dimensions of the karnaugh map grouping
     * @return the dimension as a Dimension object
     */
    public Dimension dimension() {
        return new Dimension(mapWidth, mapHeight);
    }

    /**
     * Gets the column of the left edge of the grouping
     * @return index of column
     */
    public int getStartCol() {
        return startCol;
    }

    /**
     * Gets the column of the right edge of the grouping
     * @return index of column
     */
    public int getEndCol() {
        return endCol;
    }

    /**
     * Gets the row of the top edge of the grouping
     * @return index of row
     */
    public int getStartRow() {
        return startRow;
    }

    /**
     * Gets the row of the bottom edge of the grouping
     * @return index of row
     */
    public int getEndRow() {
        return endRow;
    }

    @Override
    public String toString()
    {return "Grouping: (" + startCol + ", " + startRow + ") ~ (" + endCol + ", " + endRow + ")";}
}