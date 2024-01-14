package maze_escape_unit_tests;

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

        PrimMazeEscapeMain.main(new String[]{"5", "5", "1", "1"});
        String output = outputStream.toString();

        // Add assertions to verify relevant numbers in the output
        assertEquals(1, countOccurrences(output, "Path length:"));
        assertEquals(1, countOccurrences(output, "Visited cells:"));
        // Add more assertions as needed
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
