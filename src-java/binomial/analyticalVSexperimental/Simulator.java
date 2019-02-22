package binomial.analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;


public class Simulator {


    private int runs;
    private int k;
    private double p;
    private double alpha;



    public Simulator(int runs, int k, double p, double alpha) {
        this.runs = runs;
        this.k = k;
        this.p = p;
        this.alpha = alpha;
    }

    private ArrayList<Boolean> createRanking() {
        ArrayList<Boolean> ranking = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < k; i++) {
            double r = random.nextDouble();
            if (r <= this.p) {
                ranking.add(true);      //true equals protected
            } else {
                ranking.add(false);    //false equals unprotected
            }
        }
        return ranking;
    }

    public boolean test(ArrayList<Boolean> ranking) {
        int numProtected = 0;
        int[] protCandCount = new int[k];
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i)) {
                numProtected++;
                protCandCount[i] = numProtected;
            } else {
                protCandCount[i] = numProtected;
            }

        }

        int[] mtable = computeMTable();
        if (numProtected < mtable[mtable.length - 1]) {
            return false;
        } else {
            int protectedFound = 0;
            for (int i = 0; i < mtable.length; i++) {
                if (ranking.get(i)) {
                    protectedFound++;
                }
                if (mtable[i] > protectedFound) {
                    return false;
                }
            }
            return true;
        }

    }

    private int[] computeMTable() {
        int[] table = new int[this.k];
        for (int i = 0; i < this.k; i++) {
            table[i] = m(i+1);
        }
        return table;
    }

    private Integer m(int k) {

        BinomialDistribution dist = new BinomialDistribution(k, p);

        return dist.inverseCumulativeProbability(alpha);

    }


    public double run() {
        int successes = 0;
        for (int i = 0; i < runs; i++) {
            ArrayList<Boolean> ranking = createRanking();
            boolean test = test(ranking);
            if (test) {
                successes++;
            }
            if ((i % 200) == 0) {
//                System.out.println("p=" + p + ", alpha=" + alpha + ", k=" + k + " -- completed " + i + "/" + runs + " trials -- Fail prob.=" + ((double) (i - successes) / i));
            }
        }
//        System.out.println("----"+k+"----"+p+"----------"+alpha+"--------");
//        System.out.println(1 - (double) successes / runs);

        return 1 - (double) successes / runs;
    }


    public static void main(String[] args) throws Exception {
//        ArrayList<Double> results = new ArrayList<>();
        Double[] pValues = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6 ,0.7,0.8,0.9};
        int[] kValues= {10, 20, 50, 100, 200};
        double[] alphaValues = {0.01,0.05, 0.1, 0.15};
        PrintWriter pw = new PrintWriter("C:\\Users\\Tom\\Desktop\\results.csv");
        pw.write("k,p,alpha,failprob"+'\n');
        for(int k : kValues){
            for(double p : pValues){
                for(double alpha : alphaValues){
                    Simulator simulator = new Simulator(10000, k, p, alpha);
                    double result = simulator.run();
                    System.out.println(result);
                    pw.write(""+k+","+p+","+alpha+","+result+'\n');
//                    results.add(result);
                }
            }

        }

    }


}
