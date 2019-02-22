package multinomial;

import binomial.Simulator;
import multinomial.util.TreeNode;
import org.apache.commons.math3.distribution.BinomialDistribution;
import umontreal.ssj.probdistmulti.MultinomialDist;

import java.util.ArrayList;
import java.util.Random;

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
        int[] currentProtectedCount = new int[p.length];
        TreeNode<int[]> root = new TreeNode<>(currentProtectedCount);
        ArrayList<TreeNode> openPossibilities = new ArrayList<>();
        openPossibilities.add(root);
        int position = 1;
        while (openPossibilities.size() > 0 && position <= k) {
            ArrayList<TreeNode> intermediatePossibilities = new ArrayList<>();
            for (TreeNode<int[]> t : openPossibilities) {
                int[] dist = t.data;
                if (fairRepresentationCondition(position, dist)) {
                    TreeNode<int[]> tnode = t.addChild(dist);
                    intermediatePossibilities.add(tnode);

                } else {
                    for (int i = 0; i < dist.length; i++) {
                        int[] temp = new int[dist.length];
                        for (int c = 0; c < dist.length; c++) {
                            temp[c] = dist[c];
                        }
                        temp[i]++;
                        if (fairRepresentationCondition(position, temp)) {
                            TreeNode<int[]> tnode = t.addChild(temp);
//                        tnode.signature = i;
                            intermediatePossibilities.add(tnode);
                        }
                    }
                }
            }
            openPossibilities = intermediatePossibilities;
            position++;
            System.out.println(position);
        }
        return root;
    }

    public boolean fairRepresentationCondition(int k, int[] x) {
        if (MultinomialDist.cdf(k, p, x) > alpha) {
            System.out.println("FRC: "+k+", "+MultinomialDist.cdf(k,p,x));
            return true;
        }
        return false;

    }


    public static void main(String[] args) throws Exception {
//        double[] p = {1.0 / 3.0, 1.0 / 3.0, 1.0 / 3.0};
        double[] p = {0.6,0.15,0.15,0.1};
        int[] x = {3,1,0,0};
        System.out.println(MultinomialDist.cdf(2,p,x));
//        MultinomialSimulator simulator = new MultinomialSimulator(10000, 30, p, 0.15);
//        simulator.run();

    }


}
