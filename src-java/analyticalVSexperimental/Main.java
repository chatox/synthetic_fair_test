package analyticalVSexperimental;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        double[] pValues = {0.8};
        int k=10;
        double[] alphaValues = {0.1};

        for(double p : pValues){
            for(double alpha : alphaValues){
                RecursiveNumericFailprobabilityCalculator failprobabilityCalculator = new RecursiveNumericFailprobabilityCalculator(k,p,alpha);
                MTableFailProbPair adjustedPair = failprobabilityCalculator.adjustAlpha();
                int[] mtable = adjustedPair.getmTable();
                String table = "";
                System.out.println(k+"_"+p+"_"+alpha);
                for(int i : mtable){
                    table+=""+i+", ";
                }
                System.out.println(table);
            }
        }
        System.out.println("rdy");
    }
}
