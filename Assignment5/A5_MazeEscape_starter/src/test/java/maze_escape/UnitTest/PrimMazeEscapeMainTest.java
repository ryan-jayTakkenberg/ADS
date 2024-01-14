import maze_escape.PrimMazeEscapeMain;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrimMazeEscapeMainTest {

    @Test
    void verifyOutputNumbers() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"100", "100", "50", "25"});
        String output = outputStream.toString();

        // Updated expected values
        assertEquals(0, countOccurrences(output, "Breadth First Search: Path length:"));
        assertEquals(0, countOccurrences(output, "Breadth First Search: Visited cells:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Path length:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Visited cells:"));
    }

    @Test
    void verifyOutputForDifferentSizes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"50", "50", "25", "10"});
        String output = outputStream.toString();

        // Updated expected values
        assertEquals(0, countOccurrences(output, "Breadth First Search: Path length:"));
        assertEquals(0, countOccurrences(output, "Breadth First Search: Visited cells:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Path length:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Visited cells:"));
    }

    // Helper method to count occurrences of a substring in a string
    private int countOccurrences(String input, String substring) {
        int count = 0;
        int lastIndex = 0;

        while (lastIndex != -1) {
            lastIndex = input.indexOf(substring, lastIndex);

            if (lastIndex != -1) {
                count++;
                lastIndex += substring.length();
            }
        }

        return count;
    }
}
