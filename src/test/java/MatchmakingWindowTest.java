import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MatchmakingWindowTest {

    @Test
    void rejectsNegativeInitialHalfWidth() {
        assertThrows(IllegalArgumentException.class, () -> new MatchmakingWindow(-1, 0));
    }

    @Test
    void rejectsNegativeGrowth() {
        assertThrows(IllegalArgumentException.class, () -> new MatchmakingWindow(0, -0.001));
    }

    @Test
    void effectiveHalfWidthGrowsWithWaitTime() {
        MatchmakingWindow w = new MatchmakingWindow(10.0, 2.0);
        QueuedPlayer p = new QueuedPlayer(1000, 100L);
        assertEquals(10.0, w.effectiveHalfWidth(p, 100L));
        assertEquals(20.0, w.effectiveHalfWidth(p, 105L));
    }

    @Test
    void closedRatingBoundsUsesFloorAndCeil() {
        MatchmakingWindow w = new MatchmakingWindow(50.0, 0.0);
        QueuedPlayer p = new QueuedPlayer(1000, 0L);
        MatchmakingWindow.IntRatingBounds b = w.closedRatingBounds(p, 0L);
        assertEquals(950, b.lo);
        assertEquals(1050, b.hi);
    }
}
