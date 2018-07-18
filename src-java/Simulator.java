import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.distribution.BinomialDistribution;

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
            if (r < this.p) {
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
            for (int i = 0; i < mtable.length - 1; i++) {
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
        int[] table = new int[this.k + 1];
        for (int i = 1; i < this.k + 1; i++) {
            table[i] = m(i);
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
                System.out.println("p=" + p + ", alpha=" + alpha + ", k=" + k + " -- completed " + i + "/" + runs + " trials -- Fail prob.=" + ((double) (i - successes) / i));
            }
        }
        System.out.println("----"+k+"----"+p+"----------"+alpha+"--------");
        System.out.println(1 - (double) successes / runs);

        return 1 - (double) successes / runs;
    }


    public static void main(String[] args) throws FileNotFoundException {
        Double[] pValues = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};

        PrintWriter pw = new PrintWriter(new File("C:\\Users\\Tom\\Desktop\\CIT\\Work\\test.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("k");
        sb.append(';');
        sb.append("p");
        sb.append(';');
        sb.append("alpha");
        sb.append(';');
        sb.append("failProb");
        sb.append(';');
        sb.append('\n');


        for(double p : pValues){

            Simulator sim = new Simulator(10000, 1500, p, 0.01);
            sb.append(1500);
            sb.append(';');
            sb.append(p);
            sb.append(';');
            sb.append(0.01);
            sb.append(';');
            sb.append(sim.run());
            sb.append(';');
            sb.append('\n');
        }

        pw.write(sb.toString());
        pw.close();

    }


}
