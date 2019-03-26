package multinomial.util;

import java.io.Serializable;

public class MTree implements Serializable {

    private int k;
    private double[] p;
    private double alpha;
    private TreeNode<int[]> tree;
    private double[] failprobabilities;

    public MTree (int k, double[] p, double alpha, TreeNode<int[]> tree, double[] failprobabilities){
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.tree = tree;
        this.failprobabilities = failprobabilities;
    }
}
