import binomial.CSVWriter;
import binomial.analyticalVSexperimental.MTableFailProbPair;
import binomial.analyticalVSexperimental.MTableGenerator;
import binomial.analyticalVSexperimental.RecursiveNumericFailprobabilityCalculator;
import multinomial.util.MCDFCache;
import multinomial.util.MultinomialMTableFailProbPair;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Experiment {
    public static void main(String[] args){
        String filename_mult = "failprobIncreaseWithK_multinomial.csv";
        String filename_binom = "failprobIncreaseWithK_binom.csv";
        Path currentDir = Paths.get(".");
        String path = currentDir.toAbsolutePath().toString();
        String multinomial_path = path+"\\"+filename_mult;
        String binomial_path = path+"\\"+filename_binom;
        CSVWriter writer = new CSVWriter();
        double[] p = {1.0/3.0, 1.0/3.0, 1.0/3.0};
        double p_binom = 0.5;
        double alpha = 0.1;
        double k = 5000;
        MCDFCache cache = new MCDFCache(p);
        int step = 1;

        for(int i=1; i<=k; i+=step){
            MTableGenerator generator = new MTableGenerator(i,p_binom,alpha);
            int[] mt = generator.getMTable();
            RecursiveNumericFailprobabilityCalculator failprobabilityCalculator = new RecursiveNumericFailprobabilityCalculator(i,p_binom,alpha);
            double failprob_binom = failprobabilityCalculator.calculateFailprobability(mt,alpha);
            System.out.println("Binomial MTable: k="+i+" ;p="+p_binom+" ;alpha="+alpha+" ;failprob="+failprob_binom);
            writer.appendStrToFile(binomial_path,""+i+","+failprob_binom+'\n');

            MultinomialMTableFailProbPair gen = new MultinomialMTableFailProbPair(i,p,alpha,cache);
            double failprob_mult = gen.getFailprob();
            System.out.println("Multinomial MTree: k="+i+" ;p=1/3(all) ;alpha="+alpha+" ;failprob="+failprob_mult);
            writer.appendStrToFile(multinomial_path, ""+i+","+failprob_mult+'\n');
            if(i==10){
                step = 3;
            }
            if(i==100){
                step = 10;
            }
            if(i==1000){
                step = 100;
            }
        }
    }
}
