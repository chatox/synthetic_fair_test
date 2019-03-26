package multinomial.util;

import java.io.Serializable;
import java.util.ArrayList;

public class NodeWriteObject implements Serializable {

    public int[] data;
    public ArrayList<Integer> parents;
    public ArrayList<Integer> children;
    public double cdf;
    public int id;
    public int level;

    public NodeWriteObject(TreeNode<int[]> node){
        this.data = node.data;
        this.parents = new ArrayList<>();
        for(TreeNode<int[]> p : node.parent){
            this.parents.add(p.id);
        }
        this.children = new ArrayList<>();
        for(TreeNode<int[]> c : node.children){
            this.children.add(c.id);
        }
        this.cdf = node.cdf;
        this.id = node.id;
        this.level = node.getLevel();
    }
}
