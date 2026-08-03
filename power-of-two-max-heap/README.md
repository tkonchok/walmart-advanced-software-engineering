# Power-of-Two Max Heap

A Java 17 array-backed max heap whose branching factor is selected using a power-of-two exponent.

## Public API

```java
PowerOfTwoMaxHeap(int childExponent)
void insert(int value)
int popMax()
```

The constructor accepts exponents from `0` through `30`. Invalid exponents throw `IllegalArgumentException`. Calling `popMax()` on an empty heap throws `NoSuchElementException`.

## Design

For child exponent `e`, the branching factor is:

```text
d = 2^e
```

For a zero-based array:

```text
parent(child) = (child - 1) / d
firstChild(parent) = d * parent + 1
lastExistingChild = min(d * parent + d, size - 1)
```

Insertion uses iterative sift-up. Parent values move downward until the inserted value reaches its correct position.

Removal saves the root, replaces it conceptually with the final array value, finds the largest existing child, and performs iterative sift-down. Child-index calculations use `long` to avoid overflow when the branching factor is large.

The primitive `int[]` doubles when full. Resizing is the only allocation performed by heap operations.

## Complexity

| Operation | `d = 1` | `d >= 2` |
|---|---:|---:|
| Insert | `O(n)` | `O(log_d n)` |
| Pop maximum | `O(n)` | `O(d log_d n)` |
| Storage | `O(n)` | `O(n)` |

Exponent `0` creates a unary heap, so logarithms with base one do not apply. It behaves like a heap-ordered chain.

## Testing

The deterministic test suite covers:

- Empty and single-element heaps
- Ascending and descending insertions
- Negative values and duplicates
- `Integer.MIN_VALUE` and `Integer.MAX_VALUE`
- Dynamic resizing
- Interleaved insertions and removals
- Exponents `0`, `1`, `2`, and `30`

Randomized differential tests compare the implementation with a reverse-order Java `PriorityQueue`. Fixed seeds make failures reproducible, and every drain is checked for non-increasing order.

## Running the Tests

From the repository root:

```bash
mkdir -p out/manual

javac -Xlint:all \
  -d out/manual \
  power-of-two-max-heap/src/PowerOfTwoMaxHeap.java \
  power-of-two-max-heap/test/PowerOfTwoMaxHeapTest.java \
  power-of-two-max-heap/test/PowerOfTwoMaxHeapRandomizedTest.java

java -cp out/manual PowerOfTwoMaxHeapTest
java -cp out/manual PowerOfTwoMaxHeapRandomizedTest
```

## Benchmark

The benchmark uses Java 17, fixed random seeds, two warm-up rounds, five measured rounds, `System.nanoTime()`, and result checksums.

Pathological exponents `0` and `30` use bounded workloads. Timings are local measurements from August 2, 2026 and should be interpreted as directional rather than universal.

| Exponent | Branching factor | Values | Insert ns/op | Pop ns/op |
|---:|---:|---:|---:|---:|
| 0 | 1 | 2,000 | 1,133.00 | 1,360.07 |
| 1 | 2 | 100,000 | 11.68 | 52.52 |
| 2 | 4 | 100,000 | 5.51 | 52.55 |
| 4 | 16 | 100,000 | 4.11 | 107.77 |
| 8 | 256 | 50,000 | 1.45 | 955.19 |
| 30 | 1,073,741,824 | 2,000 | 1.20 | 1,529.83 |

Larger branching factors reduce heap height and improve insertion in this workload. Removal eventually becomes slower because each level requires scanning more children. Branching factors `2` and `4` provided the best removal balance in this run.

Run the benchmark with:

```bash
javac -Xlint:all \
  -d out/benchmark \
  power-of-two-max-heap/src/PowerOfTwoMaxHeap.java \
  power-of-two-max-heap/benchmark/PowerOfTwoMaxHeapBenchmark.java

java -cp out/benchmark PowerOfTwoMaxHeapBenchmark
```

## Project Structure

- `src/` — heap implementation
- `test/` — deterministic and randomized verification
- `benchmark/` — branching-factor performance comparison

## Lessons Learned

- Array index formulas generalize binary heaps to arbitrary branching factors.
- The hole technique avoids repeated swaps during sift-up and sift-down.
- A shorter heap is not always faster because removals must scan every existing child.
- Deterministic differential testing catches interactions that isolated examples may miss.
