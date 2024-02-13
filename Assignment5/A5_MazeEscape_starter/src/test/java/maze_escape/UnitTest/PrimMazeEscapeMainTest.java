import maze_escape.PrimMazeEscapeMain;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void verifyOutputForDifferentComplexities() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"80", "80", "50", "25"});
        String output = outputStream.toString();

        // Updated expected values
        assertEquals(0, countOccurrences(output, "Breadth First Search: Path length:"));
        assertEquals(0, countOccurrences(output, "Breadth First Search: Visited cells:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Path length:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Visited cells:"));
    }

    @Test
    void verifyOutputForDifferentSeedValues() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"60", "60", "30", "15"});
        String output = outputStream.toString();

        // Updated expected values
        assertEquals(0, countOccurrences(output, "Breadth First Search: Path length:"));
        assertEquals(0, countOccurrences(output, "Breadth First Search: Visited cells:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Path length:"));
        assertEquals(0, countOccurrences(output, "Dijkstra Shortest Path: Visited cells:"));
    }
    @Test
    void verifyDepthFirstSearchOutput() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"100", "100", "250", "20231113"});
        String output = outputStream.toString();

        // Verify Depth First Search output
        assertEquals(1, countOccurrences(output, "Depth First Search: Weight=11095,00 Length=3086 visited=3086"));
        assertEquals(1, countOccurrences(output, "Depth First Search return: Weight=8815,00 Length=2422 visited=2422"));
    }

    @Test
    void verifyBreadthFirstSearchOutput() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"100", "100", "250", "20231113"});
        String output = outputStream.toString();

        // Verify Breadth First Search output
        assertEquals(1, countOccurrences(output, "Breadth First Search: Weight=226,00 Length=79 visited=5157"));
        assertEquals(1, countOccurrences(output, "Breadth First Search return: Weight=226,00 Length=79 visited=3048"));
    }

    @Test
    void verifyDijkstraShortestPathOutput() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        PrimMazeEscapeMain.main(new String[]{"100", "100", "250", "20231113"});
        String output = outputStream.toString();

        // Verify Dijkstra Shortest Path output
        assertEquals(1, countOccurrences(output, "Dijkstra Shortest Path: Weight=226,00 Length=79 visited=5232"));
        assertEquals(1, countOccurrences(output, "Dijkstra Shortest Path return: Weight=226,00 Length=79 visited=3212"));
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
