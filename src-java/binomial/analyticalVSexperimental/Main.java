package binomial.analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double[] pValues = {0.1,0.2,0.3};
        int k=10;
        double[] alphaValues = {0.01,0.05,0.1,0.15};

        Simulator sim = new Simulator(100000,10,0.2,0.15);
        System.out.println(sim.run());

//        RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(10,0.3,0.05);
//        System.out.println(calculator.adjustAlpha().getFailProb());
//        System.out.println(calculator.adjustAlpha().getAlpha());
//
//        for(int i : calculator.adjustAlpha().getmTable()){
//            System.out.println(i);
//        }
//
////        for(double p : pValues){
////            for(double a : alphaValues){
////                String s = ""+k+","+p+","+a+"::::";
////                RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(k,p,a);
////                MTableGenerator generator = new MTableGenerator(k,p,a);
////                for(int i : calculator.adjustAlpha().getmTable()){
////                    s+=""+i+",";
////                }
////                s+=" "+calculator.adjustAlpha().getFailProb();
////                System.out.println(s);
////            }
////        }
    }
}
