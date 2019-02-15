package multinomial;

import binomial.Simulator;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;
import java.util.Random;

public class MultinomialSimulator {



    private int runs;
    private int k;
    private double p;
    private double alpha;



    public MultinomialSimulator(int runs, int k, double p, double alpha) {
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



    public static void main(String[] args) throws Exception {
        Double[] pValues = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};

        int k= 1500;
        double alpha = 0.05;
        for(double p : pValues){
            Simulator simulator = new Simulator(10000, k, p, alpha);
            //binomial.CSVWriter writer = new binomial.CSVWriter();
            //System.out.println(writer.getPropotionFromCSVFile(1500,p,0.05));
            System.out.println(simulator.run());
            //System.out.println(writer.mTableIsEqual(k, p, alpha));
        }

    }


}
