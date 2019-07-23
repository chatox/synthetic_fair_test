package multinomial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MultinomialFailprobCalculator {

    private int k;
    private double[] p;
    private double alpha;
    private HashMap<Integer, ArrayList<int[]>> mtable;
    private HashMap<ArrayList<Integer>, Boolean> testCache = new HashMap<>();
    private double failprob;
    ArrayList<int[]> mirrors;
    public static final int runs = 10000;

    public MultinomialFailprobCalculator(int k, double[] p, double alpha, HashMap<Integer, ArrayList<int[]>> mtable, ArrayList<int[]> mirrors) {
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.mtable = mtable;
        this.mirrors = mirrors;
        this.failprob = run();
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

    private double run() {
        int successes = 0;
        for (int i = 0; i < runs; i++) {
            ArrayList<Integer> ranking = createRanking(k);
            boolean test = testWithLazyMtable(ranking);
            if (test) {
                successes++;
            }
        }

        return 1 - (double) successes / runs;
    }

    private boolean testWithLazyMtable(ArrayList<Integer> ranking) {
        if (testCache.get(ranking) != null) {
            return testCache.get(ranking);
        }
        int[] seenSoFar = new int[p.length];
        for (int i = 0; i < ranking.size(); i++) {
            seenSoFar[ranking.get(i)]++;
            ArrayList<int[]> possibleWayDown = mtable.get(i+1);
            int enoughProtectedCount = 0;
            for (int[] t : possibleWayDown) {
                if (enoughProtected(t, seenSoFar)) {
                    enoughProtectedCount++;
                    break;
                }
            }
            if (enoughProtectedCount == 0) {
                testCache.put(ranking, false);
                return false;

            }
        }
        testCache.put(ranking, true);
        return true;
    }

    private boolean enoughProtected(int[] data, int[] seenSoFar) {
        if (mirrors != null && mirrors.size() > 0 && mirrors.contains(data)) {
            boolean mirror1 = true;

            for (int i = 1; i < seenSoFar.length; i++) {
                if (seenSoFar[seenSoFar.length - i] < data[i]) {
                    mirror1 = false;
                }
            }
            for (int i = 1; i < seenSoFar.length; i++) {
                if (seenSoFar[i] < data[i]) {
                    return mirror1;
                }
            }
            return true;
        }
        for (int i = 1; i < seenSoFar.length; i++) {
            if (seenSoFar[i] < data[i]) {
                return false;
            }
        }
        return true;
    }

    public double getFailprob() {
        return failprob;
    }

    public double getAlpha() {
        return alpha;
    }
}
