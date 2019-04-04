package multinomial.util;

import binomial.Simulator;
import binomial.analyticalVSexperimental.MTableGenerator;
import binomial.analyticalVSexperimental.RecursiveNumericFailprobabilityCalculator;
import multinomial.MultinomialSimulator;
import umontreal.ssj.probdistmulti.MultinomialDist;

import java.util.HashMap;

public class MCDFCache {

    private int k;
    private double[] p;
    private double alpha;
    private HashMap<int[],Double> mcdfCache;

    public MCDFCache(int k, double[] p, double alpha){
        this.k=k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = new HashMap<>();
    }

    public double mcdf(int[] signature){
        int trials = signature[0];
        if(mcdfCache.get(signature)==null){
            mcdfCache.put(signature, MultinomialDist.cdf(trials,p,signature));
        }
        return mcdfCache.get(signature);
    }

    public static void main(String[] args){
        double[] p = {1.0/3.0,1.0/3.0,1.0/3.0};
        int k = 5;
        MultinomialSimulator simulator = new MultinomialSimulator(10000,k,p,0.1);
        simulator.computeMultinomialMtables();
        System.out.println(simulator.run(k));

        RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(k,1.0/3.0, 0.1);
        //I
        int[] mtableIp1 = {0,0,1,2,3};
        int[] mtableIp2 = {0,0,0,0,0};
        System.out.println("I exp "+simulator.testSingleMultinomialTable(mtableIp1,mtableIp2));
        int[] mtableIp1c = {0,0,0,1,2,3};
        double failprobIp1 = calculator.calculateFailprobability(mtableIp1c,0.1);
        double failprobIp2 = 0;
        double failprobI = (failprobIp1*failprobIp2) + ((1-failprobIp1) * failprobIp2) + ((1-failprobIp2)*failprobIp1);
        System.out.println("I ana "+failprobI);
        //II
        int[] mtableIIp1 = {0,0,1,1,1};
        int[] mtableIIp2 = {0,0,0,1,1};
        System.out.println("II exp "+simulator.testSingleMultinomialTable(mtableIIp1,mtableIIp2));
        int[] mtableIIp1c = {0,0,0,1,1,1};
        int[] mtableIIp2c = {0,0,0,0,1,1};
        double failprobIIp1 = calculator.calculateFailprobability(mtableIIp1c,0.1);
        double failprobIIp2 = calculator.calculateFailprobability(mtableIIp2c,0.1);
        double failprobII = (failprobIIp1*failprobIIp2) + ((1.0-failprobIIp1) * failprobIIp2) + ((1.0-failprobIIp2)*failprobIIp1);
        System.out.println("II ana "+failprobII);
        //III
        int[] mtableIIIp1 = {0,0,1,2,2};
        int[] mtableIIIp2 = {0,0,0,0,1};
        System.out.println("III exp "+simulator.testSingleMultinomialTable(mtableIIIp1,mtableIIIp2));
        int[] mtableIIIp1c = {0,0,0,1,2,2};
        int[] mtableIIIp2c = {0,0,0,0,0,1};
        double failprobIIIp1 = calculator.calculateFailprobability(mtableIIIp1c,0.1);
        double failprobIIIp2 = calculator.calculateFailprobability(mtableIIIp2c, 0.1);
        double failprobIII = (failprobIIIp1*failprobIIIp2) + ((1-failprobIIIp1) * failprobIIIp2) + ((1-failprobIIIp2)*failprobIIIp1);
        System.out.println("III ana "+failprobIII);
        //IV
        int[] mtableIVp2 = {0,0,0,1,2,3};
        double failprobIVp1 = 0;
        double failprobIVp2 = calculator.calculateFailprobability(mtableIVp2, 0.1);
        double failprobIV = (failprobIVp1*failprobIVp2) + ((1-failprobIVp1) * failprobIVp2) + ((1-failprobIVp2)*failprobIVp1);

        //V
        int[] mtableVp1 = {0,0,0,0,0,1};
        int[] mtableVp2 = {0,0,0,1,2,2};
        double failprobVp1 = calculator.calculateFailprobability(mtableVp1, 0.1);
        double failprobVp2 = calculator.calculateFailprobability(mtableVp2,0.1);
        double failprobV = (failprobVp1*failprobVp2) + ((1-failprobVp1) * failprobVp2) + ((1-failprobVp2)*failprobVp1);

        //VI
        int[] mtableVIp1 = {0,0,0,0,1,1};
        int[] mtableVIp2 = {0,0,0,1,1,1};

        double failprobVIp1 = calculator.calculateFailprobability(mtableVIp1, 0.1);
        double failprobVIp2 = calculator.calculateFailprobability(mtableVIp2, 0.1);
        double failprobVI = (failprobVIp1*failprobVIp2) + ((1-failprobVIp1) * failprobVIp2) + ((1-failprobVIp2)*failprobVIp1);

        //System.out.println("I " + failprobI);
        //System.out.println("II " + failprobII);
        //System.out.println("III " + failprobIII);
        //System.out.println("IV " + failprobIV);
        //System.out.println("V " + failprobV);
        //System.out.println("VI " + failprobVI);

        //System.out.println(failprobI*failprobII*failprobIII*failprobIV*failprobV*failprobVI);
    }
}