import java.util.HashMap;
import java.util.Map;

public class MatchmakingQueue {

    private final Map<Long, QueuedPlayer> players = new HashMap<>();
    private final RatingAvlTree tree = new RatingAvlTree();
    private final MatchmakingWindow window;

    public MatchmakingQueue(MatchmakingWindow window) {
        this.window = window;
    }

    public MatchmakingQueue(double initialHalfWidth, double growthPerMs) {
        this(new MatchmakingWindow(initialHalfWidth, growthPerMs));
    }

    public MatchmakingQueue() {
        this(MatchmakingWindow.defaultWindow());
    }

    public void enqueue(long playerId, int skillRating, long joinedAtEpochMs) {
        if (players.containsKey(playerId)) {
            throw new IllegalStateException("player already in queue: " + playerId);
        }
        players.put(playerId, new QueuedPlayer(skillRating, joinedAtEpochMs));
        tree.insert(playerId, skillRating);
    }

    public boolean remove(long playerId) {
        QueuedPlayer qp = players.remove(playerId);
        if (qp == null) {
            return false;
        }
        tree.delete(playerId, qp.rating);
        return true;
    }

    public int size() {
        return players.size();
    }

    public long findOpponentId(long seekerId, long nowEpochMs) {
        QueuedPlayer seeker = players.get(seekerId);
        if (seeker == null) {
            return -1L;
        }
        MatchmakingWindow.IntRatingBounds b = window.closedRatingBounds(seeker, nowEpochMs);
        return tree.findFirstInRange(b.lo, b.hi, seekerId);
    }

    public int countEligibleOpponents(long seekerId, long nowEpochMs) {
        QueuedPlayer seeker = players.get(seekerId);
        if (seeker == null) {
            return 0;
        }
        MatchmakingWindow.IntRatingBounds b = window.closedRatingBounds(seeker, nowEpochMs);
        int total = tree.countInClosedRatingRange(b.lo, b.hi);
        if (ratingInClosedRange(seeker.rating, b.lo, b.hi)) {
            total--;
        }
        return Math.max(0, total);
    }
    
    public long tryMatchAndRemove(long seekerId, long nowEpochMs) {
        QueuedPlayer seeker = players.get(seekerId);
        if (seeker == null) {
            return -1L;
        }
        MatchmakingWindow.IntRatingBounds b = window.closedRatingBounds(seeker, nowEpochMs);
        long oppId = tree.findFirstInRange(b.lo, b.hi, seekerId);
        if (oppId < 0L) {
            return -1L;
        }
        remove(seekerId);
        remove(oppId);
        return oppId;
    }

    private static boolean ratingInClosedRange(int rating, int lo, int hi) {
        return rating >= lo && rating <= hi;
    }
}
