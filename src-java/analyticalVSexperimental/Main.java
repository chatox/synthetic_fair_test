package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.85;
        int k = 6;
        double alpha = 0.05;

//        RecursiveTableFailprobabilityCalculator calculator1 = new RecursiveTableFailprobabilityCalculator(k,p,alpha);
//        System.out.println("Table-Recursive");
//        System.out.println(calculator1.calculateFailprobability());
        RecursiveNumericFailprobabilityCalculator calculator2 = new RecursiveNumericFailprobabilityCalculator(k,p,alpha);
        System.out.println("Numeric-Recursive");
        System.out.println(calculator2.calculateFailprobability());
//        RecursiveBlockMatrixFailprobabilityCalculator calculator = new RecursiveBlockMatrixFailprobabilityCalculator(k,p,alpha);
//        System.out.println("BlockMatrix-Recursive");
//        System.out.println(calculator.calculateFailprobability());
        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        MTableMultiTester mTableMultiTester = new MTableMultiTester(k,p,alpha);
        System.out.println(mTableMultiTester.computeFailureProbability(generator.getMTable()));



    }
}
