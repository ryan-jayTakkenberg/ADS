package spotifycharts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SortingPerfomanceTest {

    private SorterImpl<Integer> sorter = new SorterImpl<>();

    @Test
    public void testSelInsBubSort() {
        List<Integer> items = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        List<Integer> sortedItems = sorter.selInsBubSort(items, Comparator.naturalOrder());

        assertEquals(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9), sortedItems);
    }

    @Test
    public void testQuickSort() {
        List<Integer> items = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        List<Integer> sortedItems = sorter.quickSort(items, Comparator.naturalOrder());

        assertEquals(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9), sortedItems);
    }

    @Test
    public void testTopsHeapSort() {
        List<Integer> items = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        List<Integer> sortedItems = sorter.topsHeapSort(5, items, Comparator.naturalOrder());

        assertEquals(Arrays.asList(5, 5, 5, 6, 9, 1, 1, 2, 3, 3, 4), sortedItems);
    }
}
