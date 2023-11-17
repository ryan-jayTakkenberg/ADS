package spotifycharts;

import java.util.*;

public class SorterImpl<E> implements Sorter<E> {

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> selInsBubSort(List<E> items, Comparator<E> comparator) {
        // TODO implement selection sort or insertion sort or bubble sort

        for (int i = 1; i < items.size(); i++) {
            E key = items.get(i);
            int j = i - 1;

            while (j >= 0 && comparator.compare(items.get(j), key) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }
            items.set(j + 1, key);
        }
        return items;
        // replace as you find appropriate
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        quickSortPart(items, 0, items.size() - 1, comparator);
        return items;
    }

    private void quickSortPart(List<E> items, int from, int to, Comparator<E> comparator) {
        if (from < to) {
            int partitionIndex = partition(items, from, to, comparator);
            quickSortPart(items, from, partitionIndex - 1, comparator);
            quickSortPart(items, partitionIndex + 1, to, comparator);
        }
    }

    private int partition(List<E> items, int from, int to, Comparator<E> comparator) {
        E pivot = items.get(to);
        int i = from - 1;

        for (int j = from; j < to; j++) {
            if (comparator.compare(items.get(j), pivot) < 0) {
                i++;
                swap(items, i, j);
            }
        }

        swap(items, i + 1, to);
        return i + 1;
    }

    private void swap(List<E> items, int i, int j) {
        E temp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, temp);
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {

        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }

        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        for (int i = numTops-1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection

            // TODO swap item[0] and item[i];
            //  this moves item[0] to its designated position

            E temp = items.get(0);
            items.set(0, items.get(i));
            items.set(i, temp);

            heapSink(items, i, reverseComparator);


            // TODO the new root may have violated the heap condition
            //  repair the heap condition on the remaining heap of size i



        }

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        int childIndex = heapSize - 1;
        int parentIndex = (childIndex - 1) / 2;
        E swimmer = items.get(childIndex);

        while (childIndex > 0 && comparator.compare(swimmer, items.get(parentIndex)) < 0) {
            // Swap the child and parent
            E temp = items.get(parentIndex);
            items.set(parentIndex, swimmer);
            items.set(childIndex, temp);

            // Move up the heap
            childIndex = parentIndex;
            parentIndex = (childIndex - 1) / 2;
        }


    }
    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    protected void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        int parentIndex = 0;
        int childIndex = 2 * parentIndex + 1;
        E sinker = items.get(parentIndex);

        while (childIndex < heapSize) {
            // Find the smaller child
            if (childIndex + 1 < heapSize && comparator.compare(items.get(childIndex), items.get(childIndex + 1)) > 0) {
                childIndex++;
            }

            // If the sinker is smaller than or equal to the smaller child, it's in the correct position.
            if (comparator.compare(sinker, items.get(childIndex)) <= 0) {
                break;
            }

            // Swap the parent and the smaller child
            items.set(parentIndex, items.get(childIndex));
            parentIndex = childIndex;
            childIndex = 2 * parentIndex + 1;
        }

        // Place the sinker in its final position
        items.set(parentIndex, sinker);
    }

    public long measureExecutionTime(List<E> items, Comparator<E> comparator, int numTops) {
        long totalTime = 0;

        for (int i = 0; i < 10; i++) {
            // Create a copy for each measurement
            List<E> copy = new ArrayList<>(items);

            // Run garbage collection to minimize its impact on measurements
            System.gc();

            // Disable JIT compiler to eliminate its impact on measurements
            System.setProperty("java.compiler", "NONE");

            long startTime = System.nanoTime();
            topsHeapSort(numTops, copy, comparator);
            long endTime = System.nanoTime();

            // Re-enable JIT compiler for subsequent runs
            System.setProperty("java.compiler", "javac");

            totalTime += endTime - startTime;
        }

        return totalTime / 10;  // average execution time over 10 runs
    }

    public static void main(String[] args) {
        SorterImpl<Integer> sorter = new SorterImpl<>();

        int numTops = 10;  // specify the number of tops

        int initialSize = 100;  // initial size

        for (int j = 1; j <= 10; j++) {
            int size = initialSize * j;  // size incremented for each run
            for (int k = 0; k < 15; k++) {  // iterate 15 times to reach 5,000,000
                if (size > 5_000_000) {
                    size = 5_000_000;  // cap the size at 5,000,000 songs
                }

                List<Integer> items = generateRandomList(size);
                Comparator<Integer> comparator = Integer::compareTo;

                long selInsBubSortTotalTime = sorter.measureExecutionTime(new ArrayList<>(items), comparator, numTops);
                long quickSortTotalTime = sorter.measureExecutionTime(new ArrayList<>(items), comparator, numTops);
                long topsHeapSortTotalTime = sorter.measureExecutionTime(new ArrayList<>(items), comparator, numTops);

                long avgSelInsBubSortTime = selInsBubSortTotalTime / 10;
                long avgQuickSortTime = quickSortTotalTime / 10;
                long avgTopsHeapSortTime = topsHeapSortTotalTime / 10;

                System.out.println("Run " + j + " - List Size: " + size +
                        " - selInsBubSort: " + avgSelInsBubSortTime + " ns" +
                        " - quickSort: " + avgQuickSortTime + " ns" +
                        " - topsHeapSort: " + avgTopsHeapSortTime + " ns");

                if (avgTopsHeapSortTime > 20 * 1_000_000_000) {
                    break;  // break if sorting takes more than 20 seconds
                }
            }
        }
    }




    private static List<Integer> generateRandomList(int size) {
        Random random = new Random();
        List<Integer> randomList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            randomList.add(random.nextInt());  // Adjust this based on your data type and generation logic
        }

        return randomList;
    }



}

