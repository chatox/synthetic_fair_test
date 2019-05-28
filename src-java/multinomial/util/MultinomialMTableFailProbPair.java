package multinomial.util;

import multinomial.MultinomialSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MultinomialMTableFailProbPair {

    private double failprob;
    private double alpha;
    private double[] p;
    private int k;
    private final int runs = 10000;


    public MultinomialMTableFailProbPair(int k, double[] p, double alpha, MCDFCache mcdfCache) {
        this.alpha = alpha;
        this.p = p;
        this.k = k;
//        MultinomialSimulator simulator = new MultinomialSimulator(runs,k,p,alpha,mcdfCache);
//        this.failprob = simulator.run(k);
        try {
            MultinomialMTableGenerator generator = new MultinomialMTableGenerator(k,p,alpha,mcdfCache);
            HashMap<Integer, ArrayList<int[]>> mtable = generator.getMtable();
            ArrayList<int[]> mirrors = generator.getMirrors();
            this.failprob = new MultinomialFailprobCalculator(k,p,alpha,mtable,mirrors).getFailprob();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
