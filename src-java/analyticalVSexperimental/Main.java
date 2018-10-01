package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.85;
        int k = 6;
        double alpha = 0.05;

//        RecursiveTableFailprobabilityCalculator calculator1 = new RecursiveTableFailprobabilityCalculator(k,p,alpha);
//        System.out.println("Table-Recursive");
//        System.out.println(calculator1.calculateFailProbability());
//        RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(k,p,alpha);
//        System.out.println("Numeric-Recursive");
//        System.out.println(calculator.calculateFailProbability());

         RecursiveBlockMatrixFailprobabilityCalculator calculator = new RecursiveBlockMatrixFailprobabilityCalculator(k,p,alpha);
         calculator.calculateFailProbability();

    }
}
