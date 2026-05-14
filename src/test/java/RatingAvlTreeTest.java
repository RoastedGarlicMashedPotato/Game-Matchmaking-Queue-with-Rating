import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RatingAvlTreeTest {

    private RatingAvlTree tree;

    @BeforeEach
    void setUp() {
        tree = new RatingAvlTree();
    }

    @Test
    void emptyTreeHasZeroCountAndNoFind() {
        assertEquals(0, tree.countInClosedRatingRange(0, 10_000));
        assertEquals(-1L, tree.findFirstInRange(0, 10_000, 1L));
    }

    @Test
    void countAndFindRespectRatingOrderAndPlayerIdTieBreak() {
        tree.insert(30L, 100);
        tree.insert(10L, 100);
        tree.insert(20L, 200);
        assertEquals(2, tree.countInClosedRatingRange(100, 100));
        assertEquals(10L, tree.findFirstInRange(100, 100, 30L));
        assertEquals(30L, tree.findFirstInRange(100, 100, 10L));
    }

    @Test
    void findFirstInRangeSkipsExcludedIdWhenOnlyCandidate() {
        tree.insert(1L, 500);
        assertEquals(-1L, tree.findFirstInRange(400, 600, 1L));
    }

    @Test
    void deleteUpdatesCounts() {
        tree.insert(1L, 100);
        tree.insert(2L, 200);
        tree.insert(3L, 300);
        assertEquals(3, tree.countInClosedRatingRange(0, 1000));
        tree.delete(2L, 200);
        assertEquals(2, tree.countInClosedRatingRange(0, 1000));
        assertEquals(1L, tree.findFirstInRange(0, 150, 99L));
    }

    @Test
    void duplicateInsertThrows() {
        tree.insert(1L, 100);
        assertThrows(IllegalStateException.class, () -> tree.insert(1L, 100));
    }
}
