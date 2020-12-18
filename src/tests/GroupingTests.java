package src.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import src.main.Grouping;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GroupingTests {

    @Test
    public void testInvalidDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> new Grouping(1, 1, 3, 1, 16, 16));

        assertThrows(IllegalArgumentException.class,
                () -> new Grouping(1, 1, 4, 6, 16, 16));

        assertThrows(IllegalArgumentException.class,
                () -> new Grouping(15, 1, 4, 8, 16, 16));
    }

    @Test
    public void testSize() {

    }


}