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
    private MCDFCache mcdfCache;
    private HashMap<Integer,Double> failprobOnLevel;


    public MultinomialSimulator(int runs, int k, double[] p, double alpha) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = new MCDFCache(k,p,alpha);
        this.failprobOnLevel = new HashMap<>();
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
            if (mcdfCache.mcdf(seenSoFar) <= alpha) {
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

        return 1 - (double) successes / runs;
    }

    private boolean containsSignature(HashMap<int[],TreeNode<int[]>> list, int[] arr) {
        for (int[] sig : list.keySet()) {
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
        openPossibilities.add(root);
        double currentTime = System.nanoTime();
        double failprob = 0;
        double succProb = 1-failprob;
        failprobOnLevel.put(0,failprob);
        int currentLevel = 0;
        while (openPossibilities.size() > 0) {
            ArrayList<TreeNode<int[]>> intermediatePossibilities = new ArrayList<>();
            ArrayList<TreeNode> validPossibilities = new ArrayList<>();
            HashMap<int[],TreeNode<int[]>> seenSoFar = new HashMap<>();
            for (TreeNode<int[]> t : openPossibilities) {
                if (seenSoFar.get(t.data)==null) {
                    seenSoFar.put(t.data,t);
                    validPossibilities.add(t);
                }else{
                    seenSoFar.get(t.data).weight +=t.weight-1;
                }
            }
            double failProbThisLevel = 0;
            DiscontFactorCache dcfCache = new DiscontFactorCache(this.p);

            for (TreeNode<int[]> t : validPossibilities) {
                if(currentLevel>2){
                    dcfCache.push(t);
                    int[] converted = t.parent.get(0).data;
                    converted[0]++;
                    failProbThisLevel += mcdfCache.mcdf(converted)*t.weight*succProb;
                }
                inverseMultinomialCDF(t);
                intermediatePossibilities.addAll(t.children);
            }
            if(currentLevel>2){
                double discontFactor = dcfCache.calculateDiscontFactor();
                failProbThisLevel = failProbThisLevel*discontFactor;
            }
            failprobOnLevel.put(currentLevel,failProbThisLevel);
            failprob = failProbThisLevel;
            succProb = 1-failprob;
            openPossibilities = intermediatePossibilities;
            System.out.println("lvl: "+currentLevel +" --- "+failprob);
            //System.out.println((System.nanoTime() - currentTime) / (1000000000) + ";" + currentLevel + ";" + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (double) (1024 * 1024));
            currentLevel ++;
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
        double mcdf = mcdfCache.mcdf(minProportions);
        if (mcdf > alpha) {
            TreeNode<int[]> child = currentNode.addChild(minProportions);
            child.cdf = mcdf;
            child.weight = currentNode.weight;
        } else {
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = mcdfCache.mcdf(temp);
                if (mcdfTemp > alpha) {
                    TreeNode<int[]> child = currentNode.addChild(temp);
                    child.cdf = mcdfTemp;
                    child.weight = currentNode.weight;
                }
            }
        }
    }

    public void readNodes(String path) throws IOException, ClassNotFoundException {
        File directory = new File(path);
        String filePrefix = "node_";
        int numberOfNodes = directory.list().length;

        HashMap<Integer, NodeWriteObject> nodes = new HashMap<>();
        for (int i = 0; i < numberOfNodes; i++) {
            FileInputStream fi = new FileInputStream(directory + "\\" + filePrefix + i);
            ObjectInputStream oi = new ObjectInputStream(fi);
            NodeWriteObject node = (NodeWriteObject) oi.readObject();
            nodes.put(node.id, node);
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
        ////////////////////////////////////////////////////////////////////////////////////
        //level 1, 2 failprob = 0;

        double failprobOnLevel2 = 0;
        double succProbOnLevel2 = 1-failprobOnLevel2;

        //NodesOnLevel 3

        int[] n1 = {3,1,0};
        int[] n2 = {3,0,1};
        //Parent from level 2
        int[] parent = {2,0,0};
        //wird zu
        int[] n1Converted = {3,0,0};
        int[] n2Converted = {3,0,0};
        // 1 gleiches Paar = 1/1
        double failprobOnLevel3 = 1*( 0.5*succProbOnLevel2* MultinomialDist.cdf(3,p,n1Converted) + 0.5*succProbOnLevel2* MultinomialDist.cdf(3,p,n2Converted));
        double succProbOnLevel3 = 1-failprobOnLevel3;
        System.out.println("Level 3 " + failprobOnLevel3);
        //NodesOnLevel 4
        int[] n3 = {4,2,0};
        int[] n4 = {4,1,1};
        int[] n5 = {4,1,1};
        int[] n6 = {4,0,2};
        //Parents from level 3
        int[] p1 = {3,1,0};
        int[] p2 = {3,0,1};
        //wird zu
        int[] n3Converted = {4,1,0};
        int[] n4Converted = {4,1,0};
        int[] n5Converted = {4,0,1};
        int[] n6Converted = {4,0,1};
        //2 gleiche Paare = 1/2
        double failprobOnLevel4 = 0.5*succProbOnLevel3*MultinomialDist.cdf(4,p,n3Converted)
                + 0.5*succProbOnLevel3 * MultinomialDist.cdf(4,p,n4Converted)
                + 0.5*succProbOnLevel3 * MultinomialDist.cdf(4,p,n5Converted)
                + 0.5*succProbOnLevel3 * MultinomialDist.cdf(4,p,n6Converted);
        double succProbOnLevel4 = 1-failprobOnLevel4;
        System.out.println("Level 4 " + failprobOnLevel4);
        //NodesOnLevel 5
        int[] n7 = {5,3,0};
        int[] n8 = {5,2,1};
        int[] n9 = {5,1,1};
        int[] n10 = {5,1,1};
        int[] n11 = {5,1,2};
        int[] n12 = {5,0,3};
        //Parents from level 4
        int[] p3 = {4,2,0};
        int[] p4 = {4,1,1};
        int[] p5 = {4,1,1};
        int[] p6 = {4,0,2};
        //wird zu
        int[] n7Converted = {5,2,0};
        int[] n8Converted = {5,2,0};
        int[] n9Converted = {5,1,1};
        int[] n10Converted = {5,1,1};
        int[] n11Converted = {5,0,2};
        int[] n12Converted = {5,0,2};
        //Ein gleiches Parent Paar / drei gleiche Paare Converted = 1/3
        double failProbOnLevel5 = (1.0/3.0)*(succProbOnLevel4*MultinomialDist.cdf(5,p,n7Converted)
                + succProbOnLevel4*MultinomialDist.cdf(5,p,n8Converted)
                + succProbOnLevel4*MultinomialDist.cdf(5,p,n9Converted)
                + succProbOnLevel4*MultinomialDist.cdf(5,p,n10Converted)
                + succProbOnLevel4*MultinomialDist.cdf(5,p,n11Converted)
                + succProbOnLevel4*MultinomialDist.cdf(5,p,n12Converted));
        double succProbOnLevel5 = 1-failProbOnLevel5;
        System.out.println("Level 5 " + failProbOnLevel5);
        //NodesOnLevel 6
        int[] n13 = {6,3,1};
        int[] n14 = {6,2,1};
        int[] n15 = {6,2,1};
        int[] n16 = {6,1,2};
        int[] n17 = {6,2,1};
        int[] n18 = {6,1,2};
        int[] n19 = {6,1,2};
        int[] n20 = {6,1,3};
        //Parents from level 5
        int[] p7 = {5,3,0};
        int[] p8 = {5,2,1};
        int[] p9 = {5,1,1};
        int[] p10 = {5,1,1};
        int[] p11 = {5,1,2};
        int[] p12 = {5,0,3};
        //wird zu
        int[] n13Converted = {6,3,0};
        int[] n14Converted = {6,2,1};
        int[] n15Converted = {6,1,1};
        int[] n16Converted = {6,1,1};
        int[] n17Converted = {6,1,1};
        int[] n18Converted = {6,1,1};
        int[] n19Converted = {6,1,2};
        int[] n20Converted = {6,0,3};
        //Ein gleiches Parent Paar /zwei gleiche Paare in Converted = 1/2
        double failProbOnLevel6 =0.5*(succProbOnLevel5*MultinomialDist.cdf(6,p,n13Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n14Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n15Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n16Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n17Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n18Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n19Converted)
                + succProbOnLevel5*MultinomialDist.cdf(6,p,n20Converted));
        double succProbOnLevel6 = 1-failProbOnLevel6;
        System.out.println("Level 6 " + failProbOnLevel6);
        //NodesOnLevel 7
        int[] n21 = {7,3,1};
        int[] n22 = {7,3,1};
        int[] n23 = {7,2,2};
        int[] n24 = {7,3,1};
        int[] n25 = {7,2,2};
        int[] n26 = {7,2,2};
        int[] n27 = {7,1,3};
        int[] n28 = {7,3,1};
        int[] n29 = {7,2,2};
        int[] n30 = {7,2,2};
        int[] n31 = {7,1,3};
        int[] n32 = {7,2,2};
        int[] n33 = {7,1,3};
        int[] n34 = {7,1,3};
        //Parents from level 6
        int[] p13 = {6,3,1};
        int[] p14 = {6,2,1};
        int[] p15 = {6,2,1};
        int[] p16 = {6,1,2};
        int[] p17 = {6,2,1};
        int[] p18 = {6,1,2};
        int[] p19 = {6,1,2};
        int[] p20 = {6,1,3};
        //wird zu
        int[] n21Converted = {7,3,1};

        int[] n22Converted = {7,2,1};
        int[] n23Converted = {7,2,1};

        int[] n24Converted = {7,2,1};
        int[] n25Converted = {7,2,1};

        int[] n26Converted = {7,1,2};
        int[] n27Converted = {7,1,2};

        int[] n28Converted = {7,2,1};
        int[] n29Converted = {7,2,1};

        int[] n30Converted = {7,1,2};
        int[] n31Converted = {7,1,2};

        int[] n32Converted = {7,1,2};
        int[] n33Converted = {7,1,2};

        int[] n34Converted = {7,1,3};

        //Zwei gleiche Parent Paar /6 gleiche Paare Converted = 2/6
        double failProbOnLevel7 = (2.0/6.0) * (succProbOnLevel6*MultinomialDist.cdf(7,p,n21Converted) + succProbOnLevel6*MultinomialDist.cdf(7,p,n22Converted)
                + succProbOnLevel6*MultinomialDist.cdf(7,p,n23Converted)
                + succProbOnLevel6*MultinomialDist.cdf(7,p,n24Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n25Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n26Converted)
                + succProbOnLevel6*MultinomialDist.cdf(7,p,n27Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n28Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n29Converted)
                + succProbOnLevel6*MultinomialDist.cdf(7,p,n30Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n31Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n32Converted)
                + succProbOnLevel6*MultinomialDist.cdf(7,p,n33Converted)+ succProbOnLevel6*MultinomialDist.cdf(7,p,n34Converted));
        System.out.println("Level 7 "+failProbOnLevel7);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        MultinomialSimulator simulator = new MultinomialSimulator(10000,7,p,0.1);
        simulator.computeMultinomialMtables();
        //save and mark nodes which are counted x times like weights

//        simulator.readNodes("C:\\Users\\Tom\\Desktop\\CIT\\mtable500\\");
//        simulator.computeMultinomialMtables();
//        System.out.println(simulator.run());
//        simulator.writeNodesToFile("C:\\Users\\Tom\\Desktop\\CIT\\mtable500\\");
//        for(int i=1; i<=k; i++){
//            System.out.println(i+";"+simulator.run(i));
//        }
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
