package multinomial;

import binomial.analyticalVSexperimental.MTableGenerator;
import multinomial.util.*;
import umontreal.ssj.probdistmulti.MultinomialDist;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
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
    private HashMap<Integer, Double> failprobOnLevel;


    public MultinomialSimulator(int runs, int k, double[] p, double alpha) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = new MCDFCache(k, p, alpha);
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

    public double testSingleMultinomialTable(int[] p1, int[] p2) {
        int successes = 0;
        for (int i = 0; i < runs; i++) {
//            if (i % 200 == 0) {
//                System.out.println("----" + k + "----" + p[0] + "----------" + alpha + "--------"+(1-(double)successes/runs));
//            }
            ArrayList<Integer> ranking = createRanking(k);
//            boolean test = test(ranking);
//            boolean test = testRankingWithMCDF(ranking);
            boolean test = singleTest(p1, p2, ranking);
            if (test) {
                successes++;
            }
        }

        return 1 - (double) successes / runs;
    }

    public boolean singleTest(int[] p1, int[] p2, ArrayList<Integer> ranking) {
        int p1Seen = 0;
        int p2Seen = 0;
        for (int j = 0; j < ranking.size(); j++) {
            if (ranking.get(j) == 1) {
                p1Seen++;
            }
            if (ranking.get(j) == 2) {
                p2Seen++;
            }
            if (p1[j] > p1Seen || p2[j] > p2Seen) {
                return false;
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

    private boolean containsSignature(HashMap<int[], TreeNode<int[]>> list, int[] arr) {
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
        int currentLevel = 0;
        double failprobSoFar = 0;
        while (openPossibilities.size() > 0) {
            ArrayList<TreeNode<int[]>> intermediatePossibilities = new ArrayList<>();
            ArrayList<TreeNode<int[]>> validPossibilities = new ArrayList<>();
//            HashMap<int[], TreeNode<int[]>> seenSoFar = new HashMap<>();
            ArrayList<int[]> seenSoFar = new ArrayList<>();
            for (TreeNode<int[]> t : openPossibilities) {
                if (!containsSignature(seenSoFar, t.data)) {
//                    seenSoFar.put(t.data, t);
                    validPossibilities.add(t);

                }
//                } else {
//                    seenSoFar.get(t.data).weight += t.weight;
//                }
            }
//            ArrayList<TreeNode<int[]>> failurePossibilities = new ArrayList<>();
            for (TreeNode<int[]> t : validPossibilities) {
                inverseMultinomialCDF(t);
                intermediatePossibilities.addAll(t.children);
//                if (moreProtected) {
//                    failurePossibilities.add(t);
//                }
            }

            currentLevel++;
//            System.out.println(currentLevel);
//            ArrayList<int[]> distinctFailurePossibilities = new ArrayList<>();
//            for(TreeNode<int[]> arr : failurePossibilities){
//                if(!containsSignature(distinctFailurePossibilities,arr.data)){
//                    distinctFailurePossibilities.add(arr.data);
//                }
//            }

//            int finalCurrentLevel = currentLevel;
//            failprobSoFar += distinctFailurePossibilities.stream().mapToDouble(a->{
//                int[] diff = new int[a.length];
//                System.arraycopy(a, 0, diff, 0, a.length);
//                diff[0]=diff[0]-countProtected(a);
//                return MultinomialDist.prob(finalCurrentLevel-1,p,diff)*p[0];
//            }).sum();


            openPossibilities = intermediatePossibilities;
            System.gc();
        }
        this.multinomialMtable = root;
        return root;
    }

    private boolean containsSignature(ArrayList<int[]> list, int[] sig) {
        for (int[] a : list) {
            int found = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i] == sig[i]) {
                    found++;
                } else {
                    break;
                }
            }
            if (found == sig.length) {
                return true;
            }
        }
        return false;
    }

    public static int countProtected(int[] sig) {
        int sum = 0;
        for (int i = 1; i < sig.length; i++) {
            sum += sig[i];
        }
        return sum;
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
            int[] data = new int[minProportions.length];
            int[][] multinomialMtable = new int[trials][minProportions.length];
//            System.arraycopy(currentNode.multinomialMtable,0,multinomialMtable,0,currentNode.multinomialMtable.length);
            System.arraycopy(minProportions, 0, data, 0, minProportions.length);
//            multinomialMtable[trials-1] = data;
            TreeNode<int[]> child = currentNode.addChild(data);
//            child.cdf = mcdf;
//            child.weight = currentNode.weight;
//            child.pmf = child.pmf * p[0];
//            child.multinomialMtable = multinomialMtable;

        } else {
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = mcdfCache.mcdf(temp);
                if (mcdfTemp > alpha) {
                    int[][] multinomialMtable = new int[trials][minProportions.length];
//                    System.arraycopy(currentNode.multinomialMtable,0,multinomialMtable,0,currentNode.multinomialMtable.length);
//                    multinomialMtable[trials-1] = temp;
                    TreeNode<int[]> child = currentNode.addChild(temp);
//                    child.cdf = mcdfTemp;
//                    child.weight = currentNode.weight;
//                    child.pmf = child.pmf * p[i];
//                    child.multinomialMtable = multinomialMtable;
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

    public static double adjustAlpha(int k, double[] p, double alpha,double alphaOld, double tolerance) {
        double aMin = 0;
        double aMax = alpha;
        double aMid = (aMin + aMax) / 2.0;

        MultinomialMTableFailProbPair min = new MultinomialMTableFailProbPair(k, p, aMin);
        MultinomialMTableFailProbPair mid = new MultinomialMTableFailProbPair(k, p, aMid);
        MultinomialMTableFailProbPair max = new MultinomialMTableFailProbPair(k, p, aMax);
        int counter = 0;
        if(Math.abs(max.getFailprob()-alphaOld)<=tolerance){
            return alpha;
        }
        while (true) {
            if (mid.getFailprob() < alphaOld) {
                aMin = aMid;
                min = new MultinomialMTableFailProbPair(k, p, aMin);
            } else if (mid.getFailprob() > alphaOld) {
                aMax = aMid;
                max = new MultinomialMTableFailProbPair(k, p, aMax);
            }
            aMid = (aMin + aMax) / 2.0;
            mid = new MultinomialMTableFailProbPair(k,p,aMid);

            double midDiff = Math.abs(mid.getFailprob()-alphaOld);
            double maxDiff = Math.abs(max.getFailprob()-alphaOld);
            double minDiff = Math.abs(min.getFailprob()-alphaOld);
//            System.out.println("Min:" + min.getFailprob());
//            System.out.println("Mid:" + mid.getFailprob());
//            System.out.println("Max:" + max.getFailprob());

            if(midDiff<= tolerance){
                System.out.println("MID:Failprob: "+ mid.getFailprob()+" ; k: "+k);
                return mid.getAlpha();
            }
            if(minDiff<=tolerance){
                System.out.println("MIN:Failprob: "+ min.getFailprob()+" ; k: "+k);
                return min.getAlpha();
            }
            if(maxDiff<=tolerance){
                System.out.println("MAX:Failprob: "+ max.getFailprob()+" ; k: "+k);
                return max.getAlpha();
            }
            System.out.println(counter++);
        }
    }


    public static void main(String[] args) throws Exception {
        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
        int k = 10;
        int[] end1 = {3, 1, 0};
        int[] start1 = {1, 0, 0};
        int kTarget = 50;
        double alpha = 0.05;
        double alphaOld = 0.05;

        //bottleneck is mtable creation
        //start with binomial adjusted alpha as aMax
        while(k < kTarget){
            alpha = MultinomialSimulator.adjustAlpha(k,p,alpha,alphaOld,0.005);
            k=k+5;
        }
        System.out.println("final alpha: "+alpha);

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
