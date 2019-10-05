package assignment2;

import java.util.LinkedList;
import java.util.List;

public class NaryTree {
    Node root;
    NaryTree(Node root) {
        this.root = root;
    }

    List<Node> reversedLevelOrderTraversal() {
        // TODO: queue O(n)
        return null;
    }
}

class Node {
    LinkedList<Node> child;
    Triplet value;

    Node(Triplet triplet) {
        this.value = triplet;
    }

    void addChild(Node node) {
        this.child.add(node);
    }

    Triplet getValue() {
        return value;
    }
}

class Triplet {
    int i;
    int j;
    int k;

    Triplet(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }
}
