package multinomial.util;

import java.io.Serializable;
import java.util.*;

public class TreeNode<T> implements Iterable<TreeNode<T>>, Serializable {

    public T data;
    public List<TreeNode<T>> parent;
    public List<TreeNode<T>> children;
    public double cdf;
    public int id;
    public static int ID = 0;
    public int weight = 1;

    public static TreeNode<int[]> recreateTreeNode(HashMap<Integer, NodeWriteObject> nodes) {
        TreeNode<int[]> root = null;
        ArrayList<NodeWriteObject> nextLevelChildren = new ArrayList<>();
        ArrayList<TreeNode<int[]>> lastLevelNodes = new ArrayList<>();
        for (NodeWriteObject n : nodes.values()) {
            if (n.level == 0) {
                root = new TreeNode<>(n.data);
                root.cdf = n.cdf;
                root.id = n.id;
                lastLevelNodes.add(root);
                for (int id : n.children) {
                    nextLevelChildren.add(nodes.get(id));
                }
                break;
            }
        }
        while (nextLevelChildren.size() > 0) {
            ArrayList<NodeWriteObject> nextLevel = new ArrayList<>();
            ArrayList<TreeNode<int[]>> lastLevel = new ArrayList<>();
            for (NodeWriteObject n : nextLevelChildren) {
                TreeNode<int[]> parent;
                for (TreeNode<int[]> p : lastLevelNodes) {
                    if (n.parents.contains(p.id)) {
                        parent = p;
                        TreeNode<int[]> child = parent.addChild(n.data);
                        child.cdf = n.cdf;
                        child.id = n.id;
                        lastLevel.add(child);
                        for (int id : n.children) {
                            nextLevel.add(nodes.get(id));
                        }
                        break;
                    }
                }
            }
            nextLevelChildren = nextLevel;
            lastLevelNodes = lastLevel;
        }

        return root;
    }

    public boolean isSplit() {
        return children.size() > 1;
    }

    public boolean isRoot() {
        return parent.size() == 0;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    private List<TreeNode<T>> elementsIndex;

    public TreeNode(T data) {
        this.id = ID;
        ID++;
        this.data = data;
        this.parent = new LinkedList<TreeNode<T>>();
        this.children = new LinkedList<TreeNode<T>>();
        this.elementsIndex = new LinkedList<TreeNode<T>>();
        this.elementsIndex.add(this);
    }

    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        childNode.parent.add(this);
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    public TreeNode<T> addChild(TreeNode<T> child) {
        child.parent.add(this);
        this.children.add(child);
        return child;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.get(0).getLevel() + 1;
    }

    private void registerChildForSearch(TreeNode<T> node) {
        elementsIndex.add(node);
    }

    public TreeNode<T> findTreeNode(Comparable<T> cmp) {
        for (TreeNode<T> element : this.elementsIndex) {
            T elData = element.data;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        TreeNodeIter<T> iter = new TreeNodeIter<T>(this);
        return iter;
    }

    public void disconnect() {
        for (TreeNode<T> parent : this.parent) {
            parent.children.remove(this);
        }
    }
}
