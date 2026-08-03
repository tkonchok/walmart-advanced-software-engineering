import java.util.Arrays;

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
}
