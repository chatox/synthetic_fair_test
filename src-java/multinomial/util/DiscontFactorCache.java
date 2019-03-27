package multinomial.util;

import java.util.ArrayList;
import java.util.HashMap;

public class DiscontFactorCache {

    HashMap<int[], Integer> converted;
    HashMap<int[], Integer> parents;
    ArrayList<TreeNode<int[]>> nodesThisLevel;
    double[] p;

    public DiscontFactorCache(double[] p) {
        this.nodesThisLevel = new ArrayList<>();
        this.converted = new HashMap<>();
        this.parents = new HashMap<>();
        this.p = p;
    }

    public void push(TreeNode<int[]> node) {
        if(node.isRoot()){
            return;
        }
        int[] converted = new int[node.data.length];
                System.arraycopy(converted,0,node.parent.get(0).data,0,node.parent.get(0).data.length);
        if (this.parents.get(converted) == null) {
            this.parents.put(converted, node.parent.get(0).weight);
        } else {
            int weight = this.parents.get(converted);
            this.parents.put(converted, node.parent.get(0).weight + weight);
        }
        converted[0]++;
        if (this.converted.get(converted) == null) {
            this.converted.put(converted, node.weight);
        } else {
            int weight = this.converted.get(converted);
            this.converted.put(converted, weight + node.weight);
        }
    }

    public double calculateDiscontFactor() {
        double multipleParents = 0;
        for (Integer i : parents.values()) {
            if (i > 1) {
                multipleParents += i;
            }
        }
        multipleParents = multipleParents / (p.length - 1);

        double multipleConverted = 0;
        for(Integer j : converted.values()){
            if(j > 1){
                multipleConverted += j;
            }
        }
        multipleConverted = multipleConverted / (p.length - 1);
        if(multipleConverted == 0 || multipleParents == 0){
            return 1;
        }
        System.out.println(multipleParents / multipleConverted);
        return multipleParents / multipleConverted ;
    }

}
