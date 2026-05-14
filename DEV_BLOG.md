MatchmakingQueue:

`Map<Long, QueuedPlayer>` for O(1) lookup by player id
`RatingAvlTree` for the actual ordered operations
`MatchmakingWindow` for policy decisions

Public api:
- `enqueue` / `remove` / `size`
- `findOpponentId` — peek at the first eligible opponent without dequeuing
- `countEligibleOpponents` — how many other players are in range
- `tryMatchAndRemove` — pair two players and remove them both

One thing I enforced early: the queue never calculates window math itself. It asks the window object for `[lo, hi]` bounds, then delegates to the tree. That separation saved me later when I changed the growth function.



MatchmakingWindow:

This class holds the policy — how the acceptable rating range expands over time.

It stores `initialHalfWidth` and `growthPerMs`. Given a `QueuedPlayer` and a current timestamp, it computes how wide the net should be. The `closedRatingBounds` method returns integer bounds using safe floor/ceil, because floating point math and tree keys don't mix.



QueuedPlayer.java:
Two fields: `rating` and `joinedAtMs`.



RatingAvlTree.java:

Each node stores:

- `height` — for balance checks
- `size` — subtree node count, which enables order statistic queries without traversing everything

Operations the queue actually uses:

- `insert` / `delete`
- `countInClosedRatingRange(lo, hi)` — counts nodes with rating in `[lo, hi]`, using `countLeq` / `countLt` with sentinel ids at boundaries
- `findFirstInRange(lo, hi, excludeId)` — in order traversal to find the first node whose id isn't the excluded one

All operations are O(log n). The AVL guarantees height stays logarithmic, so I don't wake up to degenerate trees after a bad day of matchmaking.



src/main/java/Main.java:

A tiny entrypoint that uses the JUnit Platform Launcher to run all `*Test` classes programmatically, prints a summary, and exits with a non zero code if anything failed.



src/test/java/*Test.java:

Three test files
