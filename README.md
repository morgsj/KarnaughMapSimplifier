# KarnaughMapSimplifier

A simple algorithm that produces the optimal groupings for a Karnaugh map, which can be used to generate the most simplified boolean expression for the logic system.

## Usage

See javadoc for full usage. Once all the classes are imported, one may call
```
GroupingAlgorithm.findOptimalGroupings(kmap)
```
with either a boolean array or integer array, returning an iterable list of Grouping objects.

## Algorithm Method

1. A prefix sum matrix is constructed for the Karnaugh map. This is purely for efficiency as it allows sums of regions of the Karnaugh map to occur in constant time.

2. We generate all possible Karnaugh map groupings and add them to a priority queue. A grouping is valid if and only if the region of the Karnaugh map contains only 1s and the dimensions of the groupings is 2^m by 2^n. This first priority queue sorts groupings based off their size, such that the largest groups are dequeued first. 

3. Then, until the Karnaugh map is empty (containing only zeros), we dequeue a grouping from the priority queue, add it to a list of final groupings (which we return), then set all elements in the Karnaugh map contained within this grouping to 0.

4. However, to avoid the case where some grouping is a better choice than other groupings of the same size, when we dequeue a group, we dequeue all groups of the same size into a second priority queue which dequeues the groupings in order of how many 1s on the current Karnaugh map it covers. This ensures that overlapping groups are chosen last, in case non-overlapping groups cover all the 1s still not covered.
