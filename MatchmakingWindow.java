public final class MatchmakingWindow {
    private final double initialHalfWidth;
    private final double growthPerMs;

    public MatchmakingWindow(double initialHalfWidth, double growthPerMs) {
        if (initialHalfWidth < 0 || growthPerMs < 0) {
            throw new IllegalArgumentException("initialHalfWidth and growthPerMs must be non-negative");
        }
        this.initialHalfWidth = initialHalfWidth;
        this.growthPerMs = growthPerMs;
    }

    public static MatchmakingWindow defaultWindow() {
        return new MatchmakingWindow(50.0, 10.0 / 1000.0);
    }

    public double effectiveHalfWidth(QueuedPlayer seeker, long nowEpochMs) {
        long waited = Math.max(0L, nowEpochMs - seeker.joinedAtMs);
        return initialHalfWidth + growthPerMs * waited;
    }

    public IntRatingBounds closedRatingBounds(QueuedPlayer seeker, long nowEpochMs) {
        double half = effectiveHalfWidth(seeker, nowEpochMs);
        int lo = safeFloorToInt(seeker.rating - half);
        int hi = safeCeilToInt(seeker.rating + half);
        return new IntRatingBounds(lo, hi);
    }

    public static final class IntRatingBounds {
        public final int lo;
        public final int hi;

        public IntRatingBounds(int lo, int hi) {
            this.lo = lo;
            this.hi = hi;
        }
    }

    private static int safeFloorToInt(double x) {
        if (x >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (x <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.floor(x);
    }

    private static int safeCeilToInt(double x) {
        if (x >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (x <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) Math.ceil(x);
    }
}
