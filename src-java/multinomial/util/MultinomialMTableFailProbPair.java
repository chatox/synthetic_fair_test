package multinomial.util;

import multinomial.MultinomialSimulator;

public class MultinomialMTableFailProbPair {

    private double failprob;
    private double alpha;
    private double[] p;
    private int k;
    private TreeNode<int[]> mtable;
    private final int runs = 10000;


    public MultinomialMTableFailProbPair(int k, double[] p, double alpha) {
        this.alpha = alpha;
        this.p = p;
        this.k = k;
        MultinomialSimulator simulator = new MultinomialSimulator(runs,k,p,alpha);
        this.mtable = simulator.computeMultinomialMtables();
        this.failprob = simulator.run(k);
    }

    public double getFailprob() {
        return failprob;
    }

    public double getAlpha() {
        return alpha;
    }

    public double[] getP() {
        return p;
    }

    public int getK() {
        return k;
    }
}
