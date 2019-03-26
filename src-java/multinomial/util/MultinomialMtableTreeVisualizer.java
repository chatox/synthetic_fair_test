package multinomial.util;

import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

import java.util.ArrayList;

public class MultinomialMtableTreeVisualizer {

    static final int width = 70;
    static final int height = 50;

    public static TreeForTreeLayout<NodeDisplayWrapper> convertTree(TreeNode<int[]> root, int k){


        ArrayList<TreeNode<int[]>> children = new ArrayList<>();
        NodeDisplayWrapper rootDisplay = new NodeDisplayWrapper(buildNodeString(0.0,0,root.data),width,height, root);

        DefaultTreeForTreeLayout<NodeDisplayWrapper> tree = new DefaultTreeForTreeLayout<>(rootDisplay);

        goDownDisplay(tree,rootDisplay);

        return tree;
    }

    private static void goDownDisplay(DefaultTreeForTreeLayout<NodeDisplayWrapper> tree, NodeDisplayWrapper root){
        if(root.node.children.size()>0){
            for(TreeNode<int[]> n : root.node.children){
                NodeDisplayWrapper child = new NodeDisplayWrapper(buildNodeString(n.cdf,n.getLevel(),n.data),width,height, n);
                tree.addChild(root,child);
                goDownDisplay(tree,child);
            }
        }
    }

    private static String buildNodeString(double cdf, int level, int[] minprop){
        StringBuilder sb = new StringBuilder();
        sb.append(""+level);
        sb.append('\n');
        sb.append("[");
        for(int i : minprop){
            sb.append(""+i+",");
        }
        sb.append("]");
        sb.append('\n');

        sb.append("mcdf: "+((double)Math.round(cdf * 1000d) / 1000d));

        return sb.toString();
    }

}
