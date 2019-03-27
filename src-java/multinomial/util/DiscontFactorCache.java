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
                System.arraycopy(converted,0,node.parent.data,0,node.parent.data.length);
        if (this.parents.get(converted) == null) {
            this.parents.put(converted, node.parent.weight);
        } else {
            int weight = this.parents.get(converted);
            this.parents.put(converted, node.parent.weight + weight);
        }
        converted[0]++;
        if (this.converted.get(converted) == null) {
            this.converted.put(converted, node.weight);
        } else {
            int weight = this.converted.get(converted);
            this.converted.put(converted, weight + node.weight);
        }
    }

    public double calculateDiscontFactor(ArrayList<int[]> parents, ArrayList<int[]> convertedNodes) {

        int sp = 0;
        int sc = 0;
        ArrayList<Integer> parentsTaken = new ArrayList<>();
        ArrayList<Integer> convertedTaken = new ArrayList<>();
        for (int i = 0; i < parents.size(); i++) {
            if(parentsTaken.contains(i)){
                continue;
            }
            for (int j = 0; j < parents.size(); j++) {
                if (i != j && FairnessTestGraph.isSameSignature(parents.get(i), parents.get(j)) && !parentsTaken.contains(j)) {
                    sp++;
                    parentsTaken.add(j);
                    parentsTaken.add(i);
                    break;
                }
            }
        }

        for (int i = 0; i < convertedNodes.size(); i++) {
            if(convertedTaken.contains(i)){
                continue;
            }
            for (int j = 0; j < convertedNodes.size(); j++) {
                if (i != j && FairnessTestGraph.isSameSignature(convertedNodes.get(i), convertedNodes.get(j)) && !convertedTaken.contains(j)) {
                    sc++;
                    convertedTaken.add(j);
                    convertedTaken.add(i);
                    break;
                }
            }
        }
        System.out.println("dcf: "+ sp +"//"+sc);
        double sameParentSize = sp;
        double sameConvertedSize = sc;
        if(parents.size() == 1){
            sameParentSize = 1.0 / (p.length-1);
        }
        if(sameParentSize==0){
            sameParentSize = 1;
        }
        if(sameConvertedSize == 0){
            sameConvertedSize = 1;
        }
            double multipleParentsFactor = sameParentSize;
            double multipleConvertedFactor = sameConvertedSize;

//            System.out.println(multipleParentsFactor);
        double output = multipleParentsFactor / multipleConvertedFactor;

        if((sameParentSize == sameConvertedSize && sameParentSize>1) ||sameConvertedSize == convertedNodes.size() / (p.length-1)){
            ArrayList<int[]> signaturesSeen = new ArrayList<>();
            parents.stream().forEach(arr ->{
                for(int[] seen : signaturesSeen){
                    if(FairnessTestGraph.isSameSignature(seen,arr)){
                        return;
                    }
                }
                signaturesSeen.add(arr);
            });

            output =  (1.0/signaturesSeen.size()) * output;
        }

        return output;

    }

}
