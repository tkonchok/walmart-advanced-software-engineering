import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;

public class PowerOfTwoMaxHeapRandomizedTest {
    private static final int[] EXPONENTS = {0, 1, 2, 4, 30};
    private static final long[] SEEDS = {1L, 42L, 20260802L};
    private static final int OPERATIONS = 2_000;

    public static void main(String[] args) {
        for (int exponent : EXPONENTS) {
            for (long seed : SEEDS) {
                runScenario(exponent, seed);
            }
        }

        System.out.println("All randomized tests passed.");
    }

    private static void runScenario(int exponent, long seed) {

        PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(exponent);
        PriorityQueue<Integer> reference = new PriorityQueue<>(Comparator.reverseOrder());
        Random random = new Random(seed);

        for (int operation = 0; operation < OPERATIONS; operation++) {
            boolean shouldInsert = reference.isEmpty() || random.nextInt(100) < 60;

            if (shouldInsert) {
                int value = random.nextInt();
                heap.insert(value);
                reference.add(value);
            } else {
                int expected = reference.remove();
                int actual = heap.popMax();

                assertEqual(
                        expected,
                        actual,
                        exponent,
                        seed,
                        "operation " + operation);
            }
        }

        Integer previous = null;
        int drainIndex = 0;

        while (!reference.isEmpty()) {
            int expected = reference.remove();
            int actual = heap.popMax();

            assertEqual(
                    expected,
                    actual,
                    exponent,
                    seed,
                    "drain " + drainIndex);

            if (previous != null && actual > previous) {
                throw new AssertionError(
                        "Drain order increased for exponent "
                                + exponent
                                + ", seed " + seed
                                + ": previous=" + previous
                                + ", current=" + actual);
            }

            previous = actual;
            drainIndex++;
        }

        try {
            heap.popMax();
            throw new AssertionError(
                    "Heap was not empty after draining for exponent "
                            + exponent + ", seed " + seed);
        } catch (NoSuchElementException expected) {
            // Expected after draining.
        }
    }

    private static void assertEqual(
            int expected,
            int actual,
            int exponent,
            long seed,
            String phase) {
        if (actual != expected) {
            throw new AssertionError(
                    "Mismatch for exponent " + exponent
                            + ", seed " + seed
                            + ", " + phase
                            + ": expected=" + expected
                            + ", actual=" + actual);
        }
    }
}
