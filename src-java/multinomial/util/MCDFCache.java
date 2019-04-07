package multinomial.util;

import binomial.Simulator;
import binomial.analyticalVSexperimental.MTableGenerator;
import binomial.analyticalVSexperimental.RecursiveNumericFailprobabilityCalculator;
import multinomial.MultinomialSimulator;
import umontreal.ssj.probdistmulti.MultinomialDist;

import java.util.ArrayList;
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
        System.out.println("Level 5");
        //I
        int[] mtableIp1 = {0,0,1,2,3};
        int[] mtableIp2 = {0,0,0,0,0};
        double failprobIexp = simulator.testSingleMultinomialTable(mtableIp1,mtableIp2);
        System.out.println("I exp "+simulator.testSingleMultinomialTable(mtableIp1,mtableIp2));
        int[] mtableIp1c = {0,0,0,1,2,3};
        double failprobIp1 = calculator.calculateFailprobability(mtableIp1c,0.1);
        double failprobIp2 = 0;
        double failprobI = (failprobIp1*failprobIp2) + ((1-failprobIp1) * failprobIp2) + ((1-failprobIp2)*failprobIp1);
        System.out.println("I ana "+failprobI);
        //II
        int[] mtableIIp1 = {0,0,1,1,1};
        int[] mtableIIp2 = {0,0,0,1,1};
        double failprobIIexp = simulator.testSingleMultinomialTable(mtableIIp1,mtableIIp2);
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
        double failprobIIIexp = simulator.testSingleMultinomialTable(mtableIIIp1,mtableIIIp2);
        System.out.println("III exp "+simulator.testSingleMultinomialTable(mtableIIIp1,mtableIIIp2));
        int[] mtableIIIp1c = {0,0,0,1,2,2};
        int[] mtableIIIp2c = {0,0,0,0,0,1};
        double failprobIIIp1 = calculator.calculateFailprobability(mtableIIIp1c,0.1);
        double failprobIIIp2 = calculator.calculateFailprobability(mtableIIIp2c, 0.1);
        double failprobIII = (failprobIIIp1*failprobIIIp2) + ((1-failprobIIIp1) * failprobIIIp2) + ((1-failprobIIIp2)*failprobIIIp1);
        System.out.println("III ana "+failprobIII);
        //IV
        int[] mtableIVp2 = {0,0,1,2,3};
        int[] mtableIVp1 = {0,0,0,0,0};
        double failprobIVexp = simulator.testSingleMultinomialTable(mtableIVp1,mtableIVp2);
        System.out.println("IV exp "+simulator.testSingleMultinomialTable(mtableIVp1,mtableIVp2));
        int[] mtableIVp2c = {0,0,0,1,2,3};
        double failprobIVp1 = 0;
        double failprobIVp2 = calculator.calculateFailprobability(mtableIVp2c, 0.1);
        double failprobIV = (failprobIVp1*failprobIVp2) + ((1-failprobIVp1) * failprobIVp2) + ((1-failprobIVp2)*failprobIVp1);
        System.out.println("IV ana "+failprobIV);

        //V
        int[] mtableVp1 = {0,0,0,0,1};
        int[] mtableVp2 = {0,0,1,2,2};
        double failprobVexp = simulator.testSingleMultinomialTable(mtableVp1,mtableVp2);
        System.out.println("V exp "+simulator.testSingleMultinomialTable(mtableVp1,mtableVp2));
        int[] mtableVp1c = {0,0,0,0,0,1};
        int[] mtableVp2c = {0,0,0,1,2,2};
        double failprobVp1 = calculator.calculateFailprobability(mtableVp1c, 0.1);
        double failprobVp2 = calculator.calculateFailprobability(mtableVp2c,0.1);
        double failprobV = (failprobVp1*failprobVp2) + ((1-failprobVp1) * failprobVp2) + ((1-failprobVp2)*failprobVp1);
        System.out.println("V ana "+failprobV);
        //VI
        int[] mtableVIp1 = {0,0,0,1,1};
        int[] mtableVIp2 = {0,0,1,1,1};
        double failprobVIexp = simulator.testSingleMultinomialTable(mtableVIp1,mtableVIp2);
        System.out.println("VI exp "+simulator.testSingleMultinomialTable(mtableVIp1,mtableVIp2));
        int[] mtableVIp1c = {0,0,0,0,1,1};
        int[] mtableVIp2c = {0,0,0,1,1,1};
        double failprobVIp1 = calculator.calculateFailprobability(mtableVIp1c, 0.1);
        double failprobVIp2 = calculator.calculateFailprobability(mtableVIp2c, 0.1);
        double failprobVI = (failprobVIp1*failprobVIp2) + ((1-failprobVIp1) * failprobVIp2) + ((1-failprobVIp2)*failprobVIp1);
        System.out.println("VI ana "+failprobVI);
        System.out.println("-------------------");
//        System.out.println(failprobVIp1);
//        System.out.println(failprobVIp2);
//        System.out.println(
//                ((failprobVIp1*failprobVIp2)/failprobVIp2)
//                *((failprobVIp1*failprobVIp2)/failprobVIp1)
//                +(((1-failprobVIp1)*failprobVIp2)/failprobVIp2)
//                *(((1-failprobVIp1)*failprobVIp2)/(1-failprobVIp1))
//                +((failprobVIp1*(1-failprobVIp2))/(1-failprobVIp2))
//                *((failprobVIp1*(1-failprobVIp2))/failprobVIp1));


//        System.out.println("I " + (failprobI-failprobIexp));
//        System.out.println("II " + (failprobII-failprobIIexp));
//        System.out.println("III " + (failprobIII-failprobIIIexp));
//        System.out.println("IV " + (failprobIV-failprobIVexp));
//        System.out.println("V " + (failprobV-failprobVexp));
//        System.out.println("VI " + (failprobVI-failprobVIexp));
//        ArrayList<Double> experimental = new ArrayList<>();
//        experimental.add(1-failprobIexp);
//        experimental.add(1-failprobIIexp);
//        experimental.add(1-failprobIIIexp);
//        experimental.add(1-failprobIVexp);
//        experimental.add(1-failprobVexp);
//        experimental.add(1-failprobVIexp);

        System.out.println("Level 4");
        k = 4;
        simulator = new MultinomialSimulator(10000,k,p,0.1);
        simulator.computeMultinomialMtables();
        System.out.println(simulator.run(k));
        calculator = new RecursiveNumericFailprobabilityCalculator(k,1.0/3.0, 0.1);
        //A
        int[] mtable4Ip1 =      {0,0,1,2};
        int[] mtable4Ip2 =      {0,0,0,0};
        double failprobAExp = simulator.testSingleMultinomialTable(mtable4Ip1,mtable4Ip2);
        System.out.println("path1="+failprobAExp);
        int[] mtable4Ip1c =      {0,0,0,1,2};
        int[] mtable4Ip2c =      {0,0,0,0,0};
        double failprobAp1 = calculator.calculateFailprobability(mtable4Ip1c,0.1);
        System.out.println("A P1 "+failprobAp1);
        double failprobAp2 = calculator.calculateFailprobability(mtable4Ip2c, 0.1);
        double failprobAAna = (failprobAp1 * failprobAp2) + ((1-failprobAp1)*failprobAp2) + (failprobAp1 * (1-failprobAp2));
        //B
        int[] mtable4IVp1 =     {0,0,0,0};
        int[] mtable4IVp2 =     {0,0,1,2};
        double failprobBExp = simulator.testSingleMultinomialTable(mtable4IVp1,mtable4IVp2);
        System.out.println("path4="+failprobBExp);
        int[] mtable4IVp1c =     {0,0,0,0,0};
        int[] mtable4IVp2c =     {0,0,0,1,2};
        double failprobBp1 = calculator.calculateFailprobability(mtable4IVp1c,0.1);
        double failprobBp2 = calculator.calculateFailprobability(mtable4IVp2c, 0.1);
        double failprobBAna = (failprobBp1 * failprobBp2) + ((1-failprobBp1)*failprobBp2) + (failprobBp1 * (1-failprobBp2));
        //C
        int[] mtable4IIp1 =     {0,0,1,1};
        int[] mtable4IIp2 =     {0,0,0,1};
        double failprobCExp = simulator.testSingleMultinomialTable(mtable4IIp1,mtable4IIp2);
        System.out.println("path2="+failprobCExp);
        int[] mtable4IIp1c =     {0,0,0,1,1};
        int[] mtable4IIp2c =     {0,0,0,0,1};
        double failprobCp1 = calculator.calculateFailprobability(mtable4IIp1c,0.1);
        double failprobCp2 = calculator.calculateFailprobability(mtable4IIp2c, 0.1);
        System.out.println("Analytical Path 2: P1="+failprobCp1 + "/// P2="+failprobCp2);
        double failprobCAna = (failprobCp1 * failprobCp2) + ((1-failprobCp1)*failprobCp2) + (failprobCp1 * (1-failprobCp2));
        //D
        int[] mtable4VIp1 =     {0,0,0,1};
        int[] mtable4VIp2 =     {0,0,1,1};
        double failprobDExp = simulator.testSingleMultinomialTable(mtable4VIp1,mtable4VIp2);
        System.out.println("path3="+failprobDExp);
        int[] mtable4VIp1c =     {0,0,0,0,1};
        int[] mtable4VIp2c =     {0,0,0,1,1};
        double failprobDp1 = calculator.calculateFailprobability(mtable4VIp1c,0.1);
        double failprobDp2 = calculator.calculateFailprobability(mtable4VIp2c, 0.1);
        double failprobDAna = (failprobDp1 * failprobDp2) + ((1-failprobDp1)*failprobDp2) + (failprobDp1 * (1-failprobDp2));
        System.out.println(failprobAExp);
        System.out.println(failprobBExp);
        System.out.println(failprobCExp);
        System.out.println(failprobDExp);
        System.out.println("--------------------------");
        System.out.println("Level 6");
        k = 6;
        simulator = new MultinomialSimulator(10000,k,p,0.1);
        simulator.computeMultinomialMtables();
        System.out.println(simulator.run(k));
        calculator = new RecursiveNumericFailprobabilityCalculator(k,1.0/3.0, 0.1);


        int[] m1p1 = {0,0,1,2,3,3};
        int[] m1p2 = {0,0,0,0,0,1};
        double failprob1Exp = simulator.testSingleMultinomialTable(m1p1,m1p2);
        int[] m1p1c = {0,0,0,1,2,3,3};
        int[] m1p2c = {0,0,0,0,0,0,1};
        double failprob1p1 = calculator.calculateFailprobability(m1p1c,0.1);
        double failprob1p2 = calculator.calculateFailprobability(m1p2c, 0.1);
        double failprob1Ana = (failprob1p1 * failprob1p2) + ((1-failprob1p1)*failprob1p2) + (failprob1p1 * (1-failprob1p2));

        int[] m2p1 = {0,0,1,2,2,2};
        int[] m2p2 = {0,0,0,0,1,1};
        double failprob2Exp = simulator.testSingleMultinomialTable(m2p1,m2p2);
        int[] m2p1c = {0,0,0,1,2,2,2};
        int[] m2p2c = {0,0,0,0,0,1,1};
        double failprob2p1 = calculator.calculateFailprobability(m2p1c,0.1);
        double failprob2p2 = calculator.calculateFailprobability(m2p2c, 0.1);
        double failprob2Ana = (failprob2p1 * failprob2p2) + ((1-failprob2p1)*failprob2p2) + (failprob2p1 * (1-failprob2p2));


        int[] m3p1 = {0,0,1,1,1,2};
        int[] m3p2 = {0,0,0,1,1,1};
        double failprob3Exp = simulator.testSingleMultinomialTable(m3p1,m3p2);
        int[] m3p1c = {0,0,0,1,1,1,2};
        int[] m3p2c = {0,0,0,0,1,1,1};
        double failprob3p1 = calculator.calculateFailprobability(m3p1c,0.1);
        double failprob3p2 = calculator.calculateFailprobability(m3p2c, 0.1);
        double failprob3Ana = (failprob3p1 * failprob3p2) + ((1-failprob3p1)*failprob3p2) + (failprob3p1 * (1-failprob3p2));

        int[] m4p1 = {0,0,1,1,1,1};
        int[] m4p2 = {0,0,0,1,1,2};
        double failprob4Exp = simulator.testSingleMultinomialTable(m4p1,m4p2);
        int[] m4p1c = {0,0,0,1,1,1,1};
        int[] m4p2c = {0,0,0,0,1,1,2};
        double failprob4p1 = calculator.calculateFailprobability(m4p1c,0.1);
        double failprob4p2 = calculator.calculateFailprobability(m4p2c, 0.1);
        double failprob4Ana = (failprob4p1 * failprob4p2) + ((1-failprob4p1)*failprob4p2) + (failprob4p1 * (1-failprob4p2));

        int[] m5p1 = {0,0,0,1,1,2};
        int[] m5p2 = {0,0,1,1,1,1};
        double failprob5Exp = simulator.testSingleMultinomialTable(m5p1,m5p2);
        int[] m5p1c = {0,0,0,0,1,1,2};
        int[] m5p2c = {0,0,0,1,1,1,1};
        double failprob5p1 = calculator.calculateFailprobability(m5p1c,0.1);
        double failprob5p2 = calculator.calculateFailprobability(m5p2c, 0.1);
        double failprob5Ana = (failprob5p1 * failprob5p2) + ((1-failprob5p1)*failprob5p2) + (failprob5p1 * (1-failprob5p2));

        int[] m6p1 = {0,0,0,1,1,1};
        int[] m6p2 = {0,0,1,1,1,2};
        double failprob6Exp = simulator.testSingleMultinomialTable(m6p1,m6p2);
        int[] m6p1c = {0,0,0,0,1,1,1};
        int[] m6p2c = {0,0,0,1,1,1,2};
        double failprob6p1 = calculator.calculateFailprobability(m6p1c,0.1);
        double failprob6p2 = calculator.calculateFailprobability(m6p2c, 0.1);
        double failprob6Ana = (failprob6p1 * failprob6p2) + ((1-failprob6p1)*failprob6p2) + (failprob6p1 * (1-failprob6p2));

        int[] m7p1 = {0,0,0,0,0,1};
        int[] m7p2 = {0,0,1,2,3,3};
        double failprob7Exp = simulator.testSingleMultinomialTable(m7p1,m7p2);
        int[] m7p1c = {0,0,0,0,0,0,1};
        int[] m7p2c = {0,0,0,1,2,3,3};
        double failprob7p1 = calculator.calculateFailprobability(m7p1c,0.1);
        double failprob7p2 = calculator.calculateFailprobability(m7p2c, 0.1);
        double failprob7Ana = (failprob7p1 * failprob7p2) + ((1-failprob7p1)*failprob7p2) + (failprob7p1 * (1-failprob7p2));

        int[] m8p1 = {0,0,0,0,1,1};
        int[] m8p2 = {0,0,1,2,2,2};
        double failprob8Exp = simulator.testSingleMultinomialTable(m8p1,m8p2);
        int[] m8p1c = {0,0,0,0,0,1,1};
        int[] m8p2c = {0,0,0,1,2,2,2};
        double failprob8p1 = calculator.calculateFailprobability(m8p1c,0.1);
        double failprob8p2 = calculator.calculateFailprobability(m8p2c, 0.1);
        double failprob8Ana = (failprob8p1 * failprob8p2) + ((1-failprob8p1)*failprob8p2) + (failprob8p1 * (1-failprob8p2));
        System.out.println(failprob1Exp);
        System.out.println(failprob2Exp);
        System.out.println(failprob3Exp);
        System.out.println(failprob4Exp);
        System.out.println(failprob5Exp);
        System.out.println(failprob6Exp);
        System.out.println(failprob7Exp);
        System.out.println(failprob8Exp);
        System.out.println("-------Offsets---------");
        System.out.println(failprob1Exp-failprob1Ana);
        System.out.println(failprob2Exp-failprob2Ana);
        System.out.println(failprob3Exp-failprob3Ana);
        System.out.println(failprob4Exp-failprob4Ana);
        System.out.println(failprob5Exp-failprob5Ana);
        System.out.println(failprob6Exp-failprob6Ana);
        System.out.println(failprob7Exp-failprob7Ana);
        System.out.println(failprob8Exp-failprob8Ana);
        double level6Average = ((failprob1Exp-failprob1Ana)+(failprob2Exp-failprob2Ana)+(failprob3Exp-failprob3Ana)+(failprob4Exp-failprob4Ana)+(failprob5Exp-failprob5Ana)
                +(failprob6Exp-failprob6Ana)+(failprob7Exp-failprob7Ana)+(failprob8Exp-failprob8Ana))/8d;
        double level4Average = ((failprobCExp-failprobCAna)+ (failprobDExp-failprobDAna))/2d;
        double level5Average = ((failprobIIexp-failprobII)+(failprobIIIexp-failprobIII)+(failprobVexp-failprobV)+(failprobVIexp-failprobVI))/4d;
        System.out.println(failprobAExp-failprobAAna);
        System.out.println(failprobBExp-failprobBAna);
        System.out.println(failprobCExp-failprobCAna);
        System.out.println(failprobDExp-failprobDAna);

        double failprobk4 = failprobAExp + failprobBExp + failprobCExp + failprobDExp - failprobAExp*failprobBExp - failprobAExp * failprobCExp - failprobAExp * failprobDExp - failprobBExp*failprobCExp
                -failprobBExp * failprobDExp - failprobCExp * failprobDExp;
        System.out.println("Formula k4 "+failprobk4);


//        System.out.println(failprobIexp-failprobI);
        System.out.println(failprobIIexp-failprobII);
        System.out.println(failprobIIIexp-failprobIII);
//        System.out.println(failprobIVexp-failprobIV);
        System.out.println(failprobVexp-failprobV);
        System.out.println(failprobVIexp-failprobVI);
        System.out.println("Average Offsett");
        System.out.println("Level4: " +level4Average);
        System.out.println("Level5: "+level5Average);
        System.out.println("Level6: "+level6Average);


        //AnBnCnDnEnF = 0
        //AnBnCnDnEnF =

        //Probability to pass at least 1


        //System.out.println();
    }
}