

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author Sabrina Xia
 */
public class BSTStringSet implements SortedStringSet, Iterable<String> {
    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        if (_root == null) {
            _root = new Node(s);
        } else {
            Node node = _root;
            while (true) {
                int comp = s.compareTo(node.s);
                if (comp > 0) {
                    if (node.right != null) {
                        node = node.right;
                    } else {
                        node.right = new Node(s);
                        break;
                    }
                } else {
                    if (node.left != null) {
                        node = node.left;
                    } else {
                        node.left = new Node(s);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(String s) {
        for (Node n = _root; n != null;) {
            if (s.equals(n.s)) {
                return true;
            } else if (s.compareTo(n.s) > 0) {
                n = n.right;
            } else {
                n = n.left;
            }
        }
        return false;
    }

    @Override
    public List<String> asList() {
        BSTIterator i = new BSTIterator(_root);
        ArrayList<String> result = new ArrayList<String>();
        while (i.hasNext()) {
            result.add(i.next());
        }
        return result;
    }


    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }
    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    // FIXME: UNCOMMENT THE NEXT LINE FOR PART B
    @Override
    public Iterator<String> iterator(String low, String high) {
        return new BoundedBSTIterator(_root, low, high);
    }



    private static class BoundedBSTIterator implements Iterator<String> {
        private Stack<Node> _toDo = new Stack<>();
        private String _low;
        private String _high;

        BoundedBSTIterator(Node n, String low, String high) {
            _low = low;
            _high = high;
            addTree(n);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node node = _toDo.pop();
            if (_high.compareTo(node.s) > 0) {
                addTree(node.right);
            }
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void addTree(Node node) {
            while (node != null) {
                int leftcomp = _low.compareTo(node.s);
                if (leftcomp < 0) {
                    _toDo.push(node);
                    node = node.left;
                } else {
                    break;
                }
            }
        }

    }

    /** Root node of the tree. */
    private Node _root;
}
