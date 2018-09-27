import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.3;
        int k = 50;
        double alpha = 0.1;

        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        int[] mtable = generator.getMTable();

        FailprobCalculator calculator = new FailprobCalculator(mtable,p,alpha);
        System.out.println(calculator.calculateFailProbability());

        //Simulator simulator = new Simulator(20000,k,p,alpha);
        //simulator.run();

//        MTableGenerator generator = new MTableGenerator(5,0.9,0.05);
//        int[] mtable = generator.getMTable();
//        for(int i : mtable){
//            System.out.println(i);
//        }
//        MTableMultiTester tableMultiTester = new MTableMultiTester(5,0.9,0.05);
//        System.out.println(tableMultiTester.computeFailureProbability(generator.getMTable()));


         // MAIN EXPERIMENT
//        int[] kValues = {10, 50};
//        double[] pValues = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
//        //double p = 0.5;
//        double[] alphaValues = {0.05, 0.1, 0.15};
//        ArrayList<MTableFailProbPair> analytical = new ArrayList<>();
//        ArrayList<Double> experimental = new ArrayList<>();
//        for(int k : kValues){
//            for(double p : pValues){
//                for(double alpha : alphaValues){
//                    MTableGenerator generator = new MTableGenerator(k,p,alpha);
//                    FailprobCalculator tester = new FailprobCalculator(generator.getMTable(), p, alpha);
//                    double analyticalValue = tester.calculateFailProbability();
//                    System.out.println(analyticalValue);
//                    MTableFailProbPair failProbPair = new MTableFailProbPair(k,p,alpha,analyticalValue,generator.getMTable());
//                    analytical.add(failProbPair);
//                    //Simulator simulator = new Simulator(20000,k,p,alpha);
//                    experimental.add(0d);
//
//                }
//            }
//        }
//        CSVWriter writer = new CSVWriter();
//        writer.writeMTableFailProbPairAndSimulationFailProbPairToCSV(analytical,experimental);



    }
}
