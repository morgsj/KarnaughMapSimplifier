package src.tests;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import src.common.IllegalGroupingSizeException;
import src.main.Grouping;
import src.main.GroupingAlgorithm;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GroupingAlgorithmTest {

    @Test
    public void emptyArgument() {
        boolean[][] map = new boolean[8][8];

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            fail();

    }

    @Test
    public void nullArgument() {
        assertThrows(IllegalArgumentException.class, () -> GroupingAlgorithm.findOptimalGroupings((boolean[][]) null));
    }

    @Test
    public void basic2x2() throws IllegalGroupingSizeException {
        int[][] map = {{0, 0},
                       {0, 1}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(1, 1, 1, 1, 2, 2));
    }

    @Test
    public void twoUnitGrouping2x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 0},
                       {0, 1}};

        ArrayList<Grouping> expected = new ArrayList<>();
        expected.add(new Grouping(0, 0, 0, 0, 2, 2));
        expected.add(new Grouping(1, 1, 1, 1, 2, 2));

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            Assert.assertTrue(expected.contains(g));
    }

    @Test
    public void full2x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 1},
                       {1, 1}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(0, 0, 1, 1, 2, 2));
    }

    @Test
    public void longGroup4x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 0},
                {1, 0},
                {1, 0},
                {1, 0}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(0, 0, 3, 0, 2, 4));
    }

    @Test
    public void yOverlap4x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 0},
                {0, 0},
                {0, 0},
                {1, 0}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(3, 0, 0, 0, 2, 4));
    }

    @Test
    public void doubleYOverlap4x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 1},
                {0, 0},
                {0, 0},
                {1, 1}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(3, 0, 0, 1, 2, 4));
    }

    @Test
    public void full4x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 1},
                {1, 1},
                {1, 1},
                {1, 1}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(0, 0, 3, 1, 2, 4));
    }

    @Test
    public void twoGroup4x2() throws IllegalGroupingSizeException {
        int[][] map = {{1, 1},
                {1, 1},
                {0, 1},
                {0, 0}};

        ArrayList<Grouping> expected = new ArrayList<>();
        expected.add(new Grouping(0, 0, 1, 1, 2, 4));
        expected.add(new Grouping(1, 1, 2, 1, 2, 4));

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            Assert.assertTrue(expected.contains(g));
    }

    @Test
    public void simple4x4() throws IllegalGroupingSizeException {
        int[][] map = {{0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(2, 1, 2, 2, 4, 4));
    }

    @Test
    public void XYOverlap4x4() throws IllegalGroupingSizeException {
        int[][] map = {{1, 0, 0, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {1, 0, 0, 1}};

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            assertEquals(g, new Grouping(3, 3, 0, 0, 4, 4));
    }

    @Test
    public void ambiguous4x4() throws IllegalGroupingSizeException {
        int[][] map = {{1, 1, 0, 1},
                       {1, 1, 0, 1},
                       {0, 1, 1, 0},
                       {0, 0, 0, 0}};

        ArrayList<Grouping> expected = new ArrayList<>();
        expected.add(new Grouping(0, 0, 1, 1, 4, 4));
        expected.add(new Grouping(2, 1, 2, 2, 4, 4));
        expected.add(new Grouping(0, 3, 1, 0, 4, 4));

        for (Grouping g : GroupingAlgorithm.findOptimalGroupings(map))
            Assert.assertTrue(expected.contains(g));
    }
}
