package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.4;
        int k = 10;
        double alpha = 0.05;

        RecursiveTableFailprobabilityCalculator calculator1 = new RecursiveTableFailprobabilityCalculator(k,p,alpha);
        System.out.println("Table-Recursive");
        System.out.println(calculator1.calculateFailprobability());
        RecursiveNumericFailprobabilityCalculator calculator2 = new RecursiveNumericFailprobabilityCalculator(k,p,alpha);
        System.out.println("Numeric-Recursive");
        System.out.println(calculator2.calculateFailprobability());
        RecursiveBlockMatrixFailprobabilityCalculator calculator = new RecursiveBlockMatrixFailprobabilityCalculator(k,p,alpha);
        System.out.println("BlockMatrix-Recursive");
        System.out.println(calculator.calculateFailprobability());

    }
}
