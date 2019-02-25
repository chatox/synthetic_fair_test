package multinomial;

import multinomial.util.*;
import org.abego.treelayout.TreeForTreeLayout;
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import umontreal.ssj.probdistmulti.MultinomialDist;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultinomialSimulator {


    private int runs;
    private int k;
    private double[] p;
    private double alpha;
    private TreeNode<int[]> multinomialMtable;


    public MultinomialSimulator(int runs, int k, double[] p, double alpha) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
    }

    private ArrayList<Integer> createRanking() {
        ArrayList<Integer> ranking = new ArrayList<>();
        Random random = new Random();
        double[] cumulativeProportions = new double[p.length];
        cumulativeProportions[0] = p[0];
        for (int i = 1; i < p.length; i++) {
            cumulativeProportions[i] = p[i] + cumulativeProportions[i - 1];
        }

        for (int i = 0; i < k; i++) {
            double r = random.nextDouble();
            for (int j = 0; j < cumulativeProportions.length; j++) {
                if (r <= cumulativeProportions[j]) {
                    ranking.add(j);
                    break;
                }
            }
        }
        return ranking;
    }

    public boolean test(ArrayList<Integer> ranking) {
        ArrayList<TreeNode> possibleWayDown = new ArrayList<>(multinomialMtable.children);
        int[] seenSoFar = new int[p.length];
        for (int i = 0; i < ranking.size(); i++) {
            seenSoFar[ranking.get(i)]++;
            ArrayList<TreeNode> nextStepDown = new ArrayList<>();
            for (TreeNode<int[]> t : possibleWayDown) {
                if (enoughProtected(t.data, seenSoFar)) {
                    nextStepDown.addAll(t.children);
                }
            }
            possibleWayDown = nextStepDown;
            if (nextStepDown.size() == 0 && i < ranking.size() - 1) {
                return false;
            }
        }
        return true;


    }

    private boolean enoughProtected(int[] data, int[] seenSoFar) {
        for (int i = 1; i < seenSoFar.length; i++) {
            if (seenSoFar[i] < data[i]) {
                return false;
            }
        }
        return true;
    }

    public double run() {
        multinomialMtable = this.computeMultinomialMtables();
        int successes = 0;
        for (int i = 0; i < runs; i++) {
            ArrayList<Integer> ranking = createRanking();
            boolean test = test(ranking);
            if (test) {
                successes++;
            }
        }
        System.out.println("----" + k + "----" + p + "----------" + alpha + "--------");
        System.out.println(1 - (double) successes / runs);

        return 1 - (double) successes / runs;
    }


    public TreeNode<int[]> computeMultinomialMtables() {
        int[] currentProtectedCount = new int[this.p.length];
        TreeNode<int[]> root = new TreeNode<>(currentProtectedCount);
        ArrayList<TreeNode> openPossibilities = new ArrayList<>();
        openPossibilities.add(root);
        while (openPossibilities.size() > 0) {
            ArrayList<TreeNode> intermediatePossibilities = new ArrayList<>();
            for (TreeNode<int[]> t : openPossibilities) {
                inverseMultinomialCDF(t);
                intermediatePossibilities.addAll(t.children);
            }
            openPossibilities = intermediatePossibilities;
        }
        return root;
    }


    public void inverseMultinomialCDF(TreeNode<int[]> currentNode){
        int trials = currentNode.getLevel()+1;
        if(trials>this.k){
            return;
        }
        int[] minProportions = new int[currentNode.data.length];
        System.arraycopy(currentNode.data, 0, minProportions, 0, currentNode.data.length);
        minProportions[0]++;
        double mcdf = MultinomialDist.cdf(trials,this.p,minProportions);
        if(mcdf > alpha){
            TreeNode<int[]> child = currentNode.addChild(minProportions);
            child.cdf = mcdf;
        }else{
            for(int i=1; i<this.p.length; i++){
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = MultinomialDist.cdf(trials,this.p,temp);
                if(mcdfTemp>alpha){
                    TreeNode<int[]> child =currentNode.addChild(temp);
                    child.cdf = mcdfTemp;
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
//        double[] p2 = {0.6,0.15,0.15,0.1};
////        int[] node1 = {5,0,1};
////        int[] node2 = {1,1,0};
////        int[] node3 = {0,0,0,1};
////        int[] node4 = {1,1,1};
////        //int[]= {k, 0, 0 }
////        //cdf wert in node speichern
////        System.out.println(MultinomialDist.cdf(5, p, node1)); // x oder weniger
        MultinomialSimulator simulator = new MultinomialSimulator(10000,10,p,0.01);
        System.out.println(simulator.run());

        String treeName = "test";
        TreeForTreeLayout<NodeDisplayWrapper> tree = MultinomialMtableTreeVisualizer.convertTree(simulator.multinomialMtable);

        // setup the tree layout configuration
        double gapBetweenLevels = 50;
        double gapBetweenNodes = 10;
        DefaultConfiguration<NodeDisplayWrapper> configuration = new DefaultConfiguration<>(
                gapBetweenLevels, gapBetweenNodes);

        // create the NodeExtentProvider for TextInBox nodes
        NodeDisplayWrapperNodeExtentProvider nodeExtentProvider = new NodeDisplayWrapperNodeExtentProvider();

        // create the layout
        TreeLayout<NodeDisplayWrapper> treeLayout = new TreeLayout<>(tree,
                nodeExtentProvider, configuration);

        // Create a panel that draws the nodes and edges and show the panel
        NodeDisplayWrapperTreePane panel = new NodeDisplayWrapperTreePane(treeLayout);
        showInDialog(panel);

    }

    private static void showInDialog(JComponent panel) {
        JDialog dialog = new JDialog();
        Container contentPane = dialog.getContentPane();
        ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));
        contentPane.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        panel.paint(g);
        try {
            ImageIO.write(image, "png", new File("C:\\Users\\Tom\\Desktop\\CIT\\image.png"));
        } catch (IOException ex) {
            Logger.getLogger(JComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
