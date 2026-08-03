import java.lang.reflect.Field;
import java.util.NoSuchElementException;

public class PowerOfTwoMaxHeapTest {
    public static void main(String[] args) throws Exception {
        assertHeapAfterInsertions(1, new int[] {42});
        assertHeapAfterInsertions(1, new int[] {1, 2, 3, 4, 5});
        assertHeapAfterInsertions(2, new int[] {5, 4, 3, 2, 1});
        assertHeapAfterInsertions(1, new int[] {-10, -1, -30, -5});
        assertHeapAfterInsertions(2, new int[] {7, 7, 7, 7});
        assertHeapAfterInsertions(1, new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE});

        assertEmptyPopThrows(new PowerOfTwoMaxHeap(1));

        assertPopOrder(
                1,
                new int[] {42},
                new int[] {42});

        assertPopOrder(
                2,
                new int[] {5, 5, 3, 5},
                new int[] {5, 5, 5, 3});

        int[] mixedValues = {3, -1, 7, 7, 2};
        int[] expectedOrder = {7, 7, 3, 2, -1};

        assertPopOrder(0, mixedValues, expectedOrder);
        assertPopOrder(1, mixedValues, expectedOrder);
        assertPopOrder(2, mixedValues, expectedOrder);
        assertPopOrder(30, mixedValues, expectedOrder);

        int[] resizingValues = new int[17];
        for (int index = 0; index < resizingValues.length; index++) {
            resizingValues[index] = index;
        }
        int[] resizedStorage =
                assertHeapAfterInsertions(1, resizingValues);

        if (resizedStorage.length != 32) {
            throw new AssertionError(
                    "Expected capacity 32, but got " + resizedStorage.length);
        }

        testInterleavedOperations();

        System.out.println("All heap tests passed.");
    }

    private static int[] readStorage(PowerOfTwoMaxHeap heap)
            throws Exception {
        Field field =
                PowerOfTwoMaxHeap.class.getDeclaredField("storage");
        field.setAccessible(true);
        return (int[]) field.get(heap);
    }

    private static int readSize(PowerOfTwoMaxHeap heap)
            throws Exception {
        Field field = PowerOfTwoMaxHeap.class.getDeclaredField("size");
        field.setAccessible(true);
        return field.getInt(heap);
    }

    private static int[] assertHeapAfterInsertions(
            int exponent, int[] values) throws Exception {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(exponent);
        for (int value : values) {
            heap.insert(value);
        }

        int[] storage = readStorage(heap);
        int size = readSize(heap);
        if (size != values.length) {
            throw new AssertionError("Expected size " + values.length + ", but got " + size);
        }

        int expectedMaximum = values[0];

        for (int value : values) {
            if (value > expectedMaximum) {
                expectedMaximum = value;
            }
        }

        if (storage[0] != expectedMaximum) {
            throw new AssertionError("Expected root " + expectedMaximum +
                    ", but got " + storage[0]);
        }

        int branchingFactor = 1 << exponent;

        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / branchingFactor;

            if (storage[parent] < storage[child]) {
                throw new AssertionError(
                        "Parent at index " + parent
                                + " is smaller than child at index " + child);
            }
        }
        return storage;
    }

    private static void assertPopOrder(
            int exponent,
            int[] inserted,
            int[] expected) {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(exponent);

        for (int value : inserted) {
            heap.insert(value);
        }

        for (int index = 0; index < expected.length; index++) {
            int actual = heap.popMax();

            if (actual != expected[index]) {
                throw new AssertionError(
                        "Expected " + expected[index]
                                + " at pop " + index
                                + ", but got " + actual);
            }
        }

        assertEmptyPopThrows(heap);
    }

    private static void assertEmptyPopThrows(
            PowerOfTwoMaxHeap heap) {
        try {
            heap.popMax();
            throw new AssertionError(
                    "Expected NoSuchElementException");
        } catch (NoSuchElementException expected) {
            // Expected behavior.
        }
    }

    private static void testInterleavedOperations() {
        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(1);

        heap.insert(10);
        heap.insert(4);
        assertPopValue(heap, 10);

        heap.insert(7);
        heap.insert(20);
        assertPopValue(heap, 20);
        assertPopValue(heap, 7);
        assertPopValue(heap, 4);

        assertEmptyPopThrows(heap);
    }

    private static void assertPopValue(
            PowerOfTwoMaxHeap heap, int expected) {
        int actual = heap.popMax();

        if (actual != expected) {
            throw new AssertionError(
                    "Expected pop " + expected + ", but got " + actual);
        }
    }
}
