package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double p = 0.85;
        int k = 6;
        double alpha = 0.05;

        RecursiveTableFailprobabilityCalculator calculator = new RecursiveTableFailprobabilityCalculator(k,p,alpha);
        System.out.println(calculator.calculateFailProbability());
    }
}
