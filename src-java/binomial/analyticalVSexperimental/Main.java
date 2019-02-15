package binomial.analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double[] pValues = {0.8};
        int k=10;
        double[] alphaValues = {0.1};

        RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(600,0.85,0.15);
        System.out.println(calculator.adjustAlpha().getFailProb());
    }
}
