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
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultinomialSimulator {


    private int runs;
    private int k;
    private double[] p;
    private double alpha;
    private TreeNode<int[]> multinomialMtable;
    HashMap<int[], TreeNode<int[]>> obtainedPossibilities = new HashMap<>();


    public MultinomialSimulator(int runs, int k, double[] p, double alpha) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
//        this.multinomialMtable = computeMultinomialMtables();
//        completeMtable();
    }

    private ArrayList<Integer> createRanking(int k) {
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

    public boolean testRankingWithMCDF(ArrayList<Integer> ranking) {
        int[] seenSoFar = new int[p.length];
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i) != 0) {
                seenSoFar[ranking.get(i)]++;
            }
            seenSoFar[0] = i + 1;
            if (MultinomialDist.cdf(i + 1, p, seenSoFar) <= alpha) {
//                System.out.println("["+seenSoFar[0]+","+seenSoFar[1]+","+seenSoFar[2]+"]");
                return false;
            }
        }
        return true;
    }

    public boolean test(ArrayList<Integer> ranking) {
        ArrayList<TreeNode> possibleWayDown = new ArrayList<>(multinomialMtable.children);
        int[] seenSoFar = new int[p.length];
        for (int i = 0; i < ranking.size(); i++) {
            seenSoFar[ranking.get(i)]++;

            ArrayList<TreeNode> nextStepDown = new ArrayList<>();
            int enoughProtectedCount = 0;
            for (TreeNode<int[]> t : possibleWayDown) {
                if (enoughProtected(t.data, seenSoFar)) {
                    nextStepDown.addAll(t.children);
                    enoughProtectedCount++;
                }
            }
            possibleWayDown = nextStepDown;
            if (possibleWayDown.size() == 0) {
                if (i < ranking.size() - 1 || enoughProtectedCount == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean testWithLazyMtable(ArrayList<Integer> ranking) {
        ArrayList<TreeNode> possibleWayDown = new ArrayList<>(multinomialMtable.children);
        int[] seenSoFar = new int[p.length];
        for (int i = 0; i < ranking.size(); i++) {
            seenSoFar[ranking.get(i)]++;
            ArrayList<TreeNode> nextStepDown = new ArrayList<>();
            int enoughProtectedCount = 0;
            for (TreeNode<int[]> t : possibleWayDown) {
                if (enoughProtected(t.data, seenSoFar)) {
//                    nextStepDown.addAll(t.children);
                    enoughProtectedCount++;
                }
                nextStepDown.addAll(t.children);
            }
            possibleWayDown = nextStepDown;
            if (enoughProtectedCount == 0) {
                return false;

            }
        }
        return true;
    }

    private String arrayToString(int[] arr) {
        String s = "[";
        for (int i : arr) {
            s += arr + " ";
        }
        s += "]";
        return s;
    }

    private boolean enoughProtected(int[] data, int[] seenSoFar) {
        for (int i = 1; i < seenSoFar.length; i++) {
            if (seenSoFar[i] < data[i]) {
                return false;
            }
        }
        return true;
    }

//    private void completeMtable() {
//        ArrayList<TreeNode<int[]>> nextLevel = new ArrayList<>();
//        nextLevel.add(this.multinomialMtable);
//        while (true) {
//            ArrayList<TreeNode<int[]>> children = new ArrayList<>();
//            for (TreeNode<int[]> t : nextLevel) {
//                children.addAll(t.children);
//            }
//            //make additional edges
//            for (TreeNode<int[]> c : children) {
//                for (TreeNode<int[]> t : children) {
//                    if (c.id != t.id && FairnessTestGraph.isSameSignature(c.data, t.data)) {
//                        if (c.children.size() == 0) {
//                            for (TreeNode<int[]> p : c.parent) {
//                                p.addChild(t);
//                            }
//                        } else {
//                            for (TreeNode<int[]> p : t.parent) {
//                                p.addChild(c);
//                            }
//                        }
//                    }
//                }
//            }
//            ///////////////
//
//            nextLevel = children;
//            if (nextLevel.size() == 0) {
//                return;
//            }
//        }
//    }

    public double run(int k) {
        int successes = 0;
        for (int i = 0; i < runs; i++) {
//            if (i % 200 == 0) {
//                System.out.println("----" + k + "----" + p[0] + "----------" + alpha + "--------"+(1-(double)successes/runs));
//            }
            ArrayList<Integer> ranking = createRanking(k);
//            boolean test = test(ranking);
//            boolean test = testRankingWithMCDF(ranking);
            boolean test = testWithLazyMtable(ranking);
            if (test) {
                successes++;
            }
        }

//        System.out.println(1 - (double) successes / runs);

        return 1 - (double) successes / runs;
    }

    private boolean containsSignature(ArrayList<int[]> list, int[] arr) {
        for (int[] sig : list) {
            int counter = 0;
            for (int i = 0; i < sig.length; i++) {
                if (sig[i] == arr[i]) {
                    counter++;
                }
            }
            if (counter == sig.length) {
                return true;
            }
        }
        return false;
    }

    public TreeNode<int[]> computeMultinomialMtables() {
        int[] currentProtectedCount = new int[this.p.length];
        TreeNode<int[]> root = new TreeNode<>(currentProtectedCount);
        ArrayList<TreeNode<int[]>> openPossibilities = new ArrayList<>();
        double currentTime = System.nanoTime();
        openPossibilities.add(root);
        while (openPossibilities.size() > 0) {
            ArrayList<TreeNode<int[]>> intermediatePossibilities = new ArrayList<>();
            ArrayList<TreeNode> validPossibilities = new ArrayList<>();
            ArrayList<int[]> seenSoFar = new ArrayList<>();
            for (TreeNode<int[]> t : openPossibilities) {
                if (!containsSignature(seenSoFar, t.data)) {
                    seenSoFar.add(t.data);
                    validPossibilities.add(t);
                }
            }
//            this.obtainedPossibilities = new HashMap<>();
//            System.gc();
            for (TreeNode<int[]> t : validPossibilities) {
                inverseMultinomialCDF(t);
                intermediatePossibilities.addAll(t.children);
            }


            System.out.println((System.nanoTime() - currentTime) / (1000000000) + ";" + openPossibilities.get(0).getLevel() + ";" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) (1024 * 1024));
            openPossibilities = intermediatePossibilities;
            currentTime = System.nanoTime();
            System.gc();
        }
        this.multinomialMtable = root;
        return root;
    }

    public void inverseMultinomialCDF(TreeNode<int[]> currentNode) {
        int trials = currentNode.getLevel() + 1;
        if (trials > this.k) {
            return;
        }
        int[] minProportions = new int[currentNode.data.length];
        System.arraycopy(currentNode.data, 0, minProportions, 0, currentNode.data.length);
        minProportions[0]++;
        double mcdf = MultinomialDist.cdf(trials, this.p, minProportions);
        if (mcdf > alpha) {
            TreeNode<int[]> child = currentNode.addChild(minProportions);
            child.cdf = mcdf;
        } else {
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = MultinomialDist.cdf(trials, this.p, temp);
                if (mcdfTemp > alpha) {
//                    if(obtainedPossibilities.containsKey(temp)){
//                        TreeNode<int[]> child = obtainedPossibilities.get(temp);
//                        currentNode.addChild(child);
//                    }else{
                    TreeNode<int[]> child = currentNode.addChild(temp);
//                        obtainedPossibilities.put(temp,child);
                    child.cdf = mcdfTemp;
//                    }

                }
            }
        }
    }

    public void readNodes(String path) throws IOException, ClassNotFoundException {
        File directory = new File(path);
        String filePrefix = "node_";
        int numberOfNodes = directory.list().length;

        HashMap<Integer,NodeWriteObject> nodes = new HashMap<>();
        for (int i = 0; i < numberOfNodes; i++) {
            FileInputStream fi = new FileInputStream(directory+"\\"+filePrefix+i);
            ObjectInputStream oi = new ObjectInputStream(fi);
            NodeWriteObject node = (NodeWriteObject) oi.readObject();
            nodes.put(node.id,node);
            oi.close();
            fi.close();
        }

        this.multinomialMtable = TreeNode.recreateTreeNode(nodes);


    }

    public void writeNodesToFile(String path) throws Exception {
        ArrayList<TreeNode<int[]>> nodesOnCurrentLevel = new ArrayList<>();
        nodesOnCurrentLevel.add(this.multinomialMtable);
        while (nodesOnCurrentLevel.size() > 0) {
            ArrayList<TreeNode<int[]>> nextLevel = new ArrayList<>();
            for (TreeNode<int[]> t : nodesOnCurrentLevel) {
                NodeWriteObject node = new NodeWriteObject(t);
                FileOutputStream f = new FileOutputStream(new File(path + "node_" + node.id));
                ObjectOutputStream o = new ObjectOutputStream(f);

                // Write objects to file
                o.writeObject(node);

                o.close();
                f.close();
                nextLevel.addAll(t.children);
            }
            nodesOnCurrentLevel = nextLevel;
        }
    }


    public static void main(String[] args) throws Exception {
        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
        int k = 500;
        MultinomialSimulator simulator = new MultinomialSimulator(10000, 500, p, 0.1);
        simulator.readNodes("C:\\Users\\Tom\\Desktop\\CIT\\mtable500\\");
//        simulator.computeMultinomialMtables();
//        System.out.println(simulator.run());
//        simulator.writeNodesToFile("C:\\Users\\Tom\\Desktop\\CIT\\mtable500\\");
        for(int i=1; i<=k; i++){
            System.out.println(i+";"+simulator.run(i));
        }
//            String treeName = "test";
//            MultinomialSimulator simulator = new MultinomialSimulator(10000, 10, p, 0.1);
//            simulator.run();
//            TreeForTreeLayout<NodeDisplayWrapper> tree = MultinomialMtableTreeVisualizer.convertTree(simulator.multinomialMtable, 10);
//
//            // setup the tree layout configuration
//            double gapBetweenLevels = 50;
//            double gapBetweenNodes = 10;
//            DefaultConfiguration<NodeDisplayWrapper> configuration = new DefaultConfiguration<>(
//                    gapBetweenLevels, gapBetweenNodes);
//
//            // create the NodeExtentProvider for TextInBox nodes
//            NodeDisplayWrapperNodeExtentProvider nodeExtentProvider = new NodeDisplayWrapperNodeExtentProvider();
//
//            // create the layout
//            TreeLayout<NodeDisplayWrapper> treeLayout = new TreeLayout<>(tree,
//                    nodeExtentProvider, configuration);
//
//            // Create a panel that draws the nodes and edges and show the panel
//            NodeDisplayWrapperTreePane panel = new NodeDisplayWrapperTreePane(treeLayout);
//            showInDialog(panel);


//            oi.close();
//            fi.close();

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
