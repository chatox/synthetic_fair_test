package multinomial.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MeikeMTree implements Serializable {

    private int k;
    private double[] p;
    private double alpha;
    private HashMap<Integer, ArrayList<int[]>> tree;


    public MeikeMTree (int k, double[] p, double alpha, TreeNode<int[]> tree, double[] failprobabilities){
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.tree = tree;
        this.failprobabilities = failprobabilities;
    }
}
