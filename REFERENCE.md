QueuedPlayer.java
Immutable snapshot of a player when they enter the queue. Stores skill rating and join timestamp (ms). No extra methods.

MatchmakingWindow.java
Expands the match range based on wait time.
Constructor: `MatchmakingWindow(double initialHalfWidth, double growthPerMs)`. Throws if arguments are negative.
`defaultWindow()`: Returns default policy. Initial half-width 50, grows by 10 per second (0.01 per ms).
`effectiveHalfWidth(QueuedPlayer seeker, long nowEpochMs)`: Returns current radius.
`closedRatingBounds(...)`: Returns `[lo, hi]` integer bounds. Uses safeFloor/safeCeil to prevent overflow.

IntRatingBounds (inner class)
Container for lo and hi.

MatchmakingQueue.java
Maintains a Map and an AVL tree in sync.
Constructors: Custom or default window policy.
`enqueue(long playerId, int skillRating, long joinedAtEpochMs)`: Adds a player. Throws if ID exists.
`remove(long playerId)`: Removes a player. Returns false if not found.
`size()`: Returns queue size.
`findOpponentId(long seekerId, long nowEpochMs)`: Returns first eligible opponent, or -1L if none.
`countEligibleOpponents(...)`: Returns count of eligible opponents excluding the seeker.
`tryMatchAndRemove(...)`: Matches, removes both players, returns opponent ID.
`ratingInClosedRange(...)`: Checks if rating is within `[lo, hi]`.

RatingAVLTree.java
AVL tree sorted by rating. Supports range count and ordered lookups.
`insert(long playerId, int rating)`: Inserts. Throws on duplicate.
`delete(long playerId, int rating)`: Removes a node.
`countInClosedRatingRange(int lo, int hi)`: Returns node count in range.
`findFirstInRange(int lo, int hi, long excludeId)`: Returns first node ID not matching excludeId, or -1L.

Internal Node class: stores playerId, rating, height, size.  
Private helpers: standard AVL rotations, balancing, comparison (rating then playerId), subtree size, rank queries.

Main.java
Runs MatchmakingWindowTest, RatingAvlTreeTest, MatchmakingQueueTest. Exits with code 1 if any test fails. Private constructor.