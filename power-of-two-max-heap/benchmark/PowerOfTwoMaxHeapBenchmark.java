import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class PowerOfTwoMaxHeapBenchmark {
    private static final int[] EXPONENTS =
            {0, 1, 2, 4, 8, 30};

    private static final int[] VALUE_COUNTS =
            {2_000, 100_000, 100_000, 100_000, 50_000, 2_000};

    private static final int WARMUP_ROUNDS = 2;
    private static final int MEASURED_ROUNDS = 5;
    private static final long SEED = 20260802L;

    public static void main(String[] args) {
        System.out.println(
                "exponent,branchingFactor,valueCount,"
                        + "insertNsPerOperation,popNsPerOperation,checksum");

        for (int scenario = 0;
             scenario < EXPONENTS.length;
             scenario++) {
            int exponent = EXPONENTS[scenario];
            int valueCount = VALUE_COUNTS[scenario];
            int[] values = createValues(valueCount);
            long expectedChecksum = expectedChecksum(values);

            for (int round = 0;
                 round < WARMUP_ROUNDS;
                 round++) {
                Measurement measurement =
                        measure(exponent, values);
                verifyChecksum(
                        expectedChecksum, measurement.checksum());
            }

            long insertionNanos = 0;
            long popNanos = 0;

            for (int round = 0;
                 round < MEASURED_ROUNDS;
                 round++) {
                Measurement measurement =
                        measure(exponent, values);

                verifyChecksum(
                        expectedChecksum, measurement.checksum());

                insertionNanos += measurement.insertionNanos();
                popNanos += measurement.popNanos();
            }

            double insertNsPerOperation =
                    insertionNanos
                            / (double) (MEASURED_ROUNDS * valueCount);

            double popNsPerOperation =
                    popNanos
                            / (double) (MEASURED_ROUNDS * valueCount);

            System.out.printf(
                    Locale.ROOT,
                    "%d,%d,%d,%.2f,%.2f,%d%n",
                    exponent,
                    1 << exponent,
                    valueCount,
                    insertNsPerOperation,
                    popNsPerOperation,
                    expectedChecksum);
        }
    }

    private static int[] createValues(int valueCount) {
        Random random = new Random(SEED);
        int[] values = new int[valueCount];

        for (int index = 0; index < values.length; index++) {
            values[index] = random.nextInt();
        }

        return values;
    }

    private static long expectedChecksum(int[] values) {
        int[] sorted = Arrays.copyOf(values, values.length);
        Arrays.sort(sorted);

        long checksum = 1;

        for (int index = sorted.length - 1;
             index >= 0;
             index--) {
            checksum = checksum * 31 + sorted[index];
        }

        return checksum;
    }

    private static Measurement measure(
            int exponent, int[] values) {
        PowerOfTwoMaxHeap heap =
                new PowerOfTwoMaxHeap(exponent);

        long insertionStart = System.nanoTime();

        for (int value : values) {
            heap.insert(value);
        }

        long insertionNanos =
                System.nanoTime() - insertionStart;

        long checksum = 1;
        long popStart = System.nanoTime();

        for (int ignored : values) {
            checksum = checksum * 31 + heap.popMax();
        }

        long popNanos = System.nanoTime() - popStart;

        return new Measurement(
                insertionNanos, popNanos, checksum);
    }

    private static void verifyChecksum(
            long expected, long actual) {
        if (actual != expected) {
            throw new AssertionError(
                    "Checksum mismatch: expected "
                            + expected + ", got " + actual);
        }
    }

    private record Measurement(
            long insertionNanos,
            long popNanos,
            long checksum) {
    }
}
