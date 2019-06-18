package multinomial;

import binomial.CSVWriter;
import binomial.analyticalVSexperimental.MTableGenerator;
import multinomial.util.*;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import umontreal.ssj.probdistmulti.MultinomialDist;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultinomialSimulator {


    private int runs;
    private int k;
    private double[] p;
    private double alpha;
    private TreeNode<int[]> multinomialMtable;
    private MCDFCache mcdfCache;


    public MultinomialSimulator(int runs, int k, double[] p, double alpha, MCDFCache mcdfCache) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = mcdfCache;

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

    public TreeNode<int[]> computeMultinomialMTree() {
        int[] currentProtectedCount = new int[this.p.length];
        TreeNode<int[]> root = new TreeNode<>(currentProtectedCount);
        ArrayList<TreeNode<int[]>> openPossibilities = new ArrayList<>();
        openPossibilities.add(root);
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
            System.arraycopy(minProportions, 0, data, 0, minProportions.length);
            TreeNode<int[]> child = currentNode.addChild(data);

        } else {
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = mcdfCache.mcdf(temp);
                if (mcdfTemp > alpha) {
                    TreeNode<int[]> child = currentNode.addChild(temp);
                }
            }
        }
    }

    public static MultinomialMTableFailProbPair adjustAlpha(int k, double[] p, double alpha, double alphaOld, double tolerance, MCDFCache mcdfCache) {

        double aMin = 0;
        double aMax = alpha;
        double aMid = (aMin + aMax) / 2.0;

        MultinomialMTableFailProbPair max = new MultinomialMTableFailProbPair(k, p, aMax, mcdfCache);
        if (max.getFailprob() == 0) {
            return max;
        }
        MultinomialMTableFailProbPair min = new MultinomialMTableFailProbPair(k, p, aMin, mcdfCache);
        MultinomialMTableFailProbPair mid = new MultinomialMTableFailProbPair(k, p, aMid, mcdfCache);

        if (Math.abs(max.getFailprob() - alphaOld) <= tolerance) {
            return max;
        }
        while (true) {
            boolean trigger = false;
            char side = '0';
            if (mid.getFailprob() < alphaOld) {
                aMin = aMid;
                trigger = true;
                side = 'l';
            } else if (mid.getFailprob() > alphaOld) {
                aMax = aMid;
                trigger = true;
                side = 'r';
            }
            if (trigger && side == 'l') {
                min = new MultinomialMTableFailProbPair(k, p, aMin, mcdfCache);
                aMid = (aMin + aMax) / 2.0;
                mid = new MultinomialMTableFailProbPair(k, p, aMid, mcdfCache);
            } else if (trigger && side == 'r') {
                max = new MultinomialMTableFailProbPair(k, p, aMax, mcdfCache);
                aMid = (aMin + aMax) / 2.0;
                mid = new MultinomialMTableFailProbPair(k, p, aMid, mcdfCache);
            }

            double midDiff = Math.abs(mid.getFailprob() - alphaOld);
            double maxDiff = Math.abs(max.getFailprob() - alphaOld);
            double minDiff = Math.abs(min.getFailprob() - alphaOld);

            if (midDiff <= tolerance) {
//                System.out.println("MID:Failprob: " + mid.getFailprob() + " ; k: " + k);
                return mid;
            }
            if (minDiff <= tolerance) {
//                System.out.println("MIN:Failprob: " + min.getFailprob() + " ; k: " + k);
                return min;
            }
            if (maxDiff <= tolerance) {
//                System.out.println("MAX:Failprob: " + max.getFailprob() + " ; k: " + k);
                return max;
            }
//            System.out.println("midDiff: " + midDiff + "--" + counter++);
        }
    }

    private static int calculateStepSize(int k) {
        if (k <= 10) {
            return 0;
        } else {
            return k / 10;
        }
    }

    public static MultinomialMTableFailProbPair binarySearchAlphaAdjustment(int kTarget, double[] p, double alpha) {
        double originalAlpha = alpha;
        MCDFCache mcdfCache = new MCDFCache(p);
        int steps = calculateStepSize(kTarget);
        MultinomialMTableFailProbPair pair = null;
        int k = 10;
        if (steps == 0) {
            k = kTarget;
        }
        steps = 10;
        while (k <= kTarget) {
            pair = MultinomialSimulator.adjustAlpha(k, p, alpha, originalAlpha, 0.005, mcdfCache);
            alpha = pair.getAlpha();
            k += steps;
        }
        pair = MultinomialSimulator.adjustAlpha(kTarget, p, alpha, originalAlpha, 0.005, mcdfCache);

        return pair;
    }

    public static MultinomialMTableFailProbPair regressionAlphaAdjustment(int kTarget, double[] p, double alpha, int trainingIterations) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        double originalAlpha = alpha;
        MCDFCache mcdfCache = new MCDFCache(p);
        int steps = calculateStepSize(kTarget);
        MultinomialMTableFailProbPair pair = null;
        int k = 10;
        if (steps == 0) {
            k = kTarget;
        }
        steps = 10;
        for (int i = 0; i < trainingIterations; i++) {
            pair = MultinomialSimulator.adjustAlpha(k, p, alpha, originalAlpha, 0.005, mcdfCache);
            alpha = pair.getAlpha();
            obs.add(k, alpha);
            if (k + steps <= kTarget) {
                k += steps;
            } else {
                break;
            }
        }
        if (k < kTarget) {
            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
            final double[] coeff = fitter.fit(obs.toList());
            double alphaPredict = coeff[0] + coeff[1] * kTarget + coeff[2] * (kTarget * kTarget);
            pair = MultinomialSimulator.adjustAlpha(kTarget, p, alphaPredict, originalAlpha, 0.005, mcdfCache);
        }

        return pair;

    }


    public static void main(String[] args) throws Exception {
        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
        int[] ks = {20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250};
        double alpha = 0.1;
        MultinomialMTableFailProbPair pair = null;
        CSVWriter writer = new CSVWriter();
        StringBuilder sb = new StringBuilder();
        sb.append("k,adjustedAlpha,time" + '\n');
        for (int k : ks) {
            System.out.println(k);
            long start = System.nanoTime();
            pair = MultinomialSimulator.regressionAlphaAdjustment(k,p,alpha,6);
            double end = (System.nanoTime() - start)/1000000000.0;
            sb.append(k + ","+pair.getAlpha()+","+end+ '\n');
        }
        writer.writePlotToCSV(sb.toString(), "regression_030303_01");
        System.out.println("fin");

//        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\Tom\\Desktop\\data_030303_01.csv"));
//        String line = reader.readLine();
//        line = reader.readLine();
//        final WeightedObservedPoints obs = new WeightedObservedPoints();
//        while (line != null) {
//            int k = Integer.parseInt(line.split(",")[0]);
//            double a = Double.parseDouble(line.split(",")[1]);
//            obs.add(k, a);
//            line = reader.readLine();
//        }
//        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
//        final double[] coeff = fitter.fit(obs.toList());
//        System.out.println("coef=" + Arrays.toString(coeff));
//        System.out.println(coeff[0] + coeff[1] * 100 + coeff[2] * (100 * 100));
//        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
//        System.out.println(MultinomialSimulator.binarySearchAlphaAdjustment(100, p, 0.1).getAlpha());
//        System.out.println(""+regression.predict(120));
    }


}
