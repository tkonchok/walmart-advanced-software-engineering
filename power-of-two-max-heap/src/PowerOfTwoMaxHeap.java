import java.util.Arrays;
import java.util.NoSuchElementException;

public class PowerOfTwoMaxHeap {
    private int[] storage;
    private int size;
    private final int childExponent;
    private final int branchingFactor;

    public PowerOfTwoMaxHeap(int childExponent) {
        if (childExponent < 0 || childExponent > 30) {
            throw new IllegalArgumentException(
                    "childExponent must be between 0 and 30");
        }
        this.childExponent = childExponent;
        this.storage = new int[16];
        this.size = 0;
        this.branchingFactor = 1 << childExponent;
    }

    private void ensureCapacity() {
        if (size == storage.length) {
            storage = Arrays.copyOf(storage, storage.length * 2);
        }
    }

    public void insert(int value) {
        ensureCapacity();
        int insertionIndex = size;
        size++;
        while (insertionIndex != 0) {
            int parent = (insertionIndex - 1) / branchingFactor;

            if (storage[parent] >= value) {
                break;
            }
            storage[insertionIndex] = storage[parent];
            insertionIndex = parent;
        }
        storage[insertionIndex] = value;
    }

    public int popMax() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        int maxValue = storage[0];
        int lastValue = storage[size - 1];
        size--;

        if (size == 0) {
            return maxValue;
        }

        int replacementIndex = 0;

        while (true) {
            long firstChild =
                    (long) replacementIndex * branchingFactor + 1;

            if (firstChild >= size) {
                break;
            }

            long childLimit = Math.min(
                    firstChild + branchingFactor,
                    (long) size);

            int largestChild = (int) firstChild;

            for (long child = firstChild + 1;
                 child < childLimit;
                 child++) {
                int childIndex = (int) child;

                if (storage[childIndex] > storage[largestChild]) {
                    largestChild = childIndex;
                }
            }

            if (lastValue >= storage[largestChild]) {
                break;
            }

            storage[replacementIndex] = storage[largestChild];
            replacementIndex = largestChild;
        }

        storage[replacementIndex] = lastValue;
        return maxValue;
    }
}
