package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.85;
        int k = 6;
        double alpha = 0.05;

        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        int[] mtable = generator.getMTable();

        RecursiveTableFailprobabilityCalculator calculator = new RecursiveTableFailprobabilityCalculator(mtable,p,alpha);
        System.out.println(calculator.calculateFailProbability());
    }
}
