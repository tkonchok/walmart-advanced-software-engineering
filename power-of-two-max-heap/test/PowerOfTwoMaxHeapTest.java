import java.lang.reflect.Field;

public class PowerOfTwoMaxHeapTest {
    public static void main(String[] args) throws Exception {
        assertHeapAfterInsertions(1, new int[] {42});
        assertHeapAfterInsertions(1, new int[] {1, 2, 3, 4, 5});
        assertHeapAfterInsertions(2, new int[] {5, 4, 3, 2, 1});
        assertHeapAfterInsertions(1, new int[] {-10, -1, -30, -5});
        assertHeapAfterInsertions(2, new int[] {7, 7, 7, 7});
        assertHeapAfterInsertions(1, new int[] {Integer.MIN_VALUE, 0, Integer.MAX_VALUE});

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
        System.out.println("All insertion tests passed.");
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
}
