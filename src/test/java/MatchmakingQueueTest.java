import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchmakingQueueTest {

    @Test
    void enqueueDuplicateThrows() {
        MatchmakingQueue q = new MatchmakingQueue(50.0, 0.0);
        q.enqueue(1L, 1000, 0L);
        assertThrows(IllegalStateException.class, () -> q.enqueue(1L, 1000, 0L));
    }

    @Test
    void findOpponentReturnsMinusOneWhenOutOfWindow() {
        MatchmakingQueue q = new MatchmakingQueue(50.0, 0.0);
        long t0 = 0L;
        q.enqueue(1L, 1000, t0);
        q.enqueue(2L, 1200, t0);
        assertEquals(-1L, q.findOpponentId(1L, t0));
    }

    @Test
    void windowExpandsWithWaitTimeSoDistantRatingBecomesEligible() {
        MatchmakingQueue q = new MatchmakingQueue(50.0, 1.0);
        long join = 100L;
        q.enqueue(1L, 1000, join);
        q.enqueue(2L, 1090, join);
        assertEquals(-1L, q.findOpponentId(1L, join));
        long later = join + 40L;
        assertEquals(2L, q.findOpponentId(1L, later));
    }

    @Test
    void countEligibleOpponentsExcludesSeeker() {
        MatchmakingQueue q = new MatchmakingQueue(100.0, 0.0);
        q.enqueue(1L, 1000, 0L);
        q.enqueue(2L, 1050, 0L);
        q.enqueue(3L, 950, 0L);
        assertEquals(2, q.countEligibleOpponents(1L, 0L));
    }

    @Test
    void tryMatchAndRemoveRemovesBothPlayers() {
        MatchmakingQueue q = new MatchmakingQueue(200.0, 0.0);
        q.enqueue(1L, 1000, 0L);
        q.enqueue(2L, 1100, 0L);
        assertEquals(2L, q.tryMatchAndRemove(1L, 0L));
        assertEquals(0, q.size());
    }

    @Test
    void tryMatchAndRemoveReturnsMinusOneWhenNoOpponent() {
        MatchmakingQueue q = new MatchmakingQueue(10.0, 0.0);
        q.enqueue(1L, 1000, 0L);
        q.enqueue(2L, 2000, 0L);
        assertEquals(-1L, q.tryMatchAndRemove(1L, 0L));
        assertEquals(2, q.size());
    }

    @Test
    void removeAbsentReturnsFalse() {
        MatchmakingQueue q = new MatchmakingQueue();
        assertFalse(q.remove(99L));
    }

    @Test
    void removePresentReturnsTrue() {
        MatchmakingQueue q = new MatchmakingQueue();
        q.enqueue(5L, 500, 0L);
        assertTrue(q.remove(5L));
        assertEquals(0, q.size());
    }

    @Test
    void unknownSeekerYieldsMinusOneAndZeroCount() {
        MatchmakingQueue q = new MatchmakingQueue();
        assertEquals(-1L, q.findOpponentId(404L, 0L));
        assertEquals(0, q.countEligibleOpponents(404L, 0L));
        assertEquals(-1L, q.tryMatchAndRemove(404L, 0L));
    }
}
