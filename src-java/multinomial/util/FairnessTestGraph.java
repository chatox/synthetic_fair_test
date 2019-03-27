package multinomial.util;

import java.util.ArrayList;

public class FairnessTestGraph {

    public TreeNode<int[]> multinomialMtable;
    private int k;


    public FairnessTestGraph(TreeNode<int[]> multinomialMtable, int k){
        this.multinomialMtable = multinomialMtable;
        this.k = k;
        createTestGraph();
    }

    public static boolean isSameSignature(int[] a, int[] b){
        for(int i=0; i<a.length; i++){
            if(a[i]!=b[i]){
                return false;
            }
        }
        return true;
    }

    private void createTestGraph(){
        TreeNode<int[]> root = multinomialMtable;
        ArrayList<TreeNode<int[]>> currentLevelChildren = new ArrayList<>(root.children);
        while(currentLevelChildren.size()>0){
            for(TreeNode<int[]> t : currentLevelChildren){

                if(t.children.size()==0 && t.getLevel()<k){
                    for(TreeNode<int[]> n : currentLevelChildren){
                        if(isSameSignature(t.data,n.data) && t.id != n.id){
                            t.parent.addChild(n);
                            t.disconnect();
                        }
                    }
                    currentLevelChildren.remove(t);
                }
            }
        }
    }
}
