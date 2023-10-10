package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> sortOrder;   // the comparator that has been used with the latest sort
    protected int nSorted;                       // the number of sorted items in the first section of the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given sortOrder comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> sortOrder) {
        super();
        this.sortOrder = sortOrder;
        this.nSorted = 0;
    }

    public Comparator<? super E> getSortOrder() {
        return this.sortOrder;
    }

    @Override
    public boolean add(E element) {
        int index = nSorted;
        if (index < 0) {
            index = -index - 1;
        }
        super.add(index, element);
        if (index < nSorted) {
            nSorted++;
        }
        return true;
    }


    // TODO override the ArrayList.add(index, item), ArrayList.remove(index) and Collection.remove(object) methods
    //  such that they both meet the ArrayList contract of these methods (see ArrayList JavaDoc)
    //  and sustain the representation invariant of OrderedArrayList
    //  (hint: only change nSorted as required to guarantee the representation invariant,
    //   do not invoke a sort or reorder items otherwise differently than is specified by the ArrayList contract)


    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.sortOrder);
        }
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
    }

    @Override
    public E remove(int index) {
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public int indexOf(Object item) {
        // efficient search can be done only if you have provided an sortOrder for the list
        if (this.getSortOrder() != null) {
            return indexOfByIterativeBinarySearch((E) item);
        } else {
            return super.indexOf(item);
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        int low = 0; //lowest point of the array
        int high = nSorted - 1;//highest point of de sorted array

        while (low <= high) {
            int mid = (low + high) / 2;// find the middel of the array
            int compare = sortOrder.compare(get(mid), searchItem);// compare with de middel one

            if (compare < 0) {// if it is lower dan de middel f cmp is less than 0, it means that searchItem
                // should be on the right side of the
                // mid element in the sorted section
                low = mid + 1;
            } else if (compare > 0) {//If cmp is greater than 0, it means that searchItem should be on
                // the left side of the mid
                // element in the sorted section.
                high = mid - 1;
            } else {
                return mid;
            }
        }


        for (int i = 0; i < size(); i++) {
            if (sortOrder.compare(get(i), searchItem) == 0) {
                return i;
            }


        }


        // TODO implement an iterative binary search on the sorted section of the arrayList, 0 <= index < nSorted
        //   to find the position of an item that matches searchItem (this.sortOrder comparator yields a 0 result)
        // TODO if no match was found, attempt a linear search of searchItem in the section nSorted <= index < size()
        return -1;  // nothing was found ???
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.sortOrder comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.sortOrder comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for sorting items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.sortOrder
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        return recursiveBinarySearch(searchItem, 0, nSorted - 1);
    }

    private int recursiveBinarySearch(E searchItem, int low, int high) {
        if (low <= high) {
            int mid = (low + high) / 2;
            int cmp = sortOrder.compare(get(mid), searchItem);

            if (cmp < 0) {
                return recursiveBinarySearch(searchItem, mid + 1, high);
            } else if (cmp > 0) {
                return recursiveBinarySearch(searchItem, low, mid - 1);
            } else {
                return mid;  // Found a match
            }
        }

        // If the loop finishes and the item is not found, attempt a linear search in the unsorted section
        return linearSearch(searchItem);
    }

    private int linearSearch(E searchItem) {
        for (int i = nSorted; i < size(); i++) {
            if (sortOrder.compare(get(i), searchItem) == 0) {
                return i;  // Found a match
            }
        }

        return -1;  // Item not found
    }


    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            E matcheditem = this.get(matchedItemIndex);//item that match et as variable
            E mergedItem = merger.apply(matcheditem, newItem);

            this.set(matchedItemIndex, mergedItem);// adden by yhe index
            return false;


            // TODO retrieve the matched item and
            //  replace the matched item in the list with the merger of the matched item and the newItem

        }
    }

    /**
     * calculates the total sum of contributions of all items in the list
     *
     * @param mapper a function that calculates the contribution of a single item
     * @return the total sum of all contributions
     */
    @Override
    public double aggregate(Function<E, Double> mapper) {
        double sum = 0.0;

         for (E item : this){
             double contribution = mapper.apply(item);
             sum += contribution;
         }

        return sum;

        // TODO loop over all items and use the mapper
        //  to calculate and accumulate the contribution of each item

    }
}
