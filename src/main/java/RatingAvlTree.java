public final class RatingAvlTree {

    private static final class Node {
        final long playerId;
        final int rating;
        Node left;
        Node right;
        int height;
        int size;

        Node(long playerId, int rating) {
            this.playerId = playerId;
            this.rating = rating;
            this.height = 0;
            this.size = 1;
        }
    }

    private Node root;

    public void insert(long playerId, int rating) {
        root = insert(root, playerId, rating);
    }

    public void delete(long playerId, int rating) {
        root = delete(root, playerId, rating);
    }

    public int countInClosedRatingRange(int lo, int hi) {
        int leUpper = countLeq(root, hi, Long.MAX_VALUE);
        int ltLower = countLt(root, lo, Long.MIN_VALUE);
        return leUpper - ltLower;
    }

    public long findFirstInRange(int lo, int hi, long excludeId) {
        Node n = findFirstInRange(root, lo, hi, excludeId);
        return n == null ? -1L : n.playerId;
    }

    private static int cmpKey(int r1, long id1, int r2, long id2) {
        int c = Integer.compare(r1, r2);
        if (c != 0) {
            return c;
        }
        return Long.compare(id1, id2);
    }

    private static int height(Node n) {
        return n == null ? -1 : n.height;
    }

    private static int size(Node n) {
        return n == null ? 0 : n.size;
    }

    private static void refresh(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
        n.size = 1 + size(n.left) + size(n.right);
    }

    private static int balanceFactor(Node n) {
        return height(n.left) - height(n.right);
    }

    private static Node rotateRight(Node y) {
        Node x = y.left;
        Node t2 = x.right;
        x.right = y;
        y.left = t2;
        refresh(y);
        refresh(x);
        return x;
    }

    private static Node rotateLeft(Node x) {
        Node y = x.right;
        Node t2 = y.left;
        y.left = x;
        x.right = t2;
        refresh(x);
        refresh(y);
        return y;
    }

    private static Node rebalance(Node n) {
        refresh(n);
        int bf = balanceFactor(n);
        if (bf > 1 && balanceFactor(n.left) >= 0) {
            return rotateRight(n);
        }
        if (bf > 1) {
            n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if (bf < -1 && balanceFactor(n.right) <= 0) {
            return rotateLeft(n);
        }
        if (bf < -1) {
            n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
        return n;
    }

    private static Node insert(Node n, long playerId, int rating) {
        if (n == null) {
            return new Node(playerId, rating);
        }
        int c = cmpKey(rating, playerId, n.rating, n.playerId);
        if (c < 0) {
            n.left = insert(n.left, playerId, rating);
        } else if (c > 0) {
            n.right = insert(n.right, playerId, rating);
        } else {
            throw new IllegalStateException("duplicate key in AVL");
        }
        return rebalance(n);
    }

    private static Node delete(Node n, long playerId, int rating) {
        if (n == null) {
            return null;
        }
        int c = cmpKey(rating, playerId, n.rating, n.playerId);
        if (c < 0) {
            n.left = delete(n.left, playerId, rating);
        } else if (c > 0) {
            n.right = delete(n.right, playerId, rating);
        } else {
            if (n.left == null) {
                return n.right;
            }
            if (n.right == null) {
                return n.left;
            }
            Node succ = minNode(n.right);
            n.right = delete(n.right, succ.playerId, succ.rating);
            succ.left = n.left;
            succ.right = n.right;
            n = succ;
        }
        return rebalance(n);
    }

    private static Node minNode(Node n) {
        while (n.left != null) {
            n = n.left;
        }
        return n;
    }

    private static Node findFirstInRange(Node t, int lo, int hi, long excludeId) {
        if (t == null) {
            return null;
        }
        if (t.rating < lo) {
            return findFirstInRange(t.right, lo, hi, excludeId);
        }
        if (t.rating > hi) {
            return findFirstInRange(t.left, lo, hi, excludeId);
        }
        Node leftHit = findFirstInRange(t.left, lo, hi, excludeId);
        if (leftHit != null) {
            return leftHit;
        }
        if (t.playerId != excludeId) {
            return t;
        }
        return findFirstInRange(t.right, lo, hi, excludeId);
    }

    private static int countLt(Node t, int rating, long id) {
        if (t == null) {
            return 0;
        }
        int c = cmpKey(t.rating, t.playerId, rating, id);
        if (c < 0) {
            return size(t.left) + 1 + countLt(t.right, rating, id);
        }
        return countLt(t.left, rating, id);
    }

    private static int countLeq(Node t, int rating, long id) {
        if (t == null) {
            return 0;
        }
        int c = cmpKey(t.rating, t.playerId, rating, id);
        if (c <= 0) {
            return size(t.left) + 1 + countLeq(t.right, rating, id);
        }
        return countLeq(t.left, rating, id);
    }
}
