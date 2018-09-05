import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        int k = 500;
        //double[] pValues = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
        double p = 0.9;
        double alpha = 0.1;
        Simulator sim = new Simulator(10000, k,p,alpha);
        System.out.println(sim.run());
        MTableMultiTester tester = new MTableMultiTester(k,p,alpha);
        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        int[] mtable = generator.getMTable();
        System.out.println(tester.computeFailureProbability(mtable));

        /*MTableMultiTester analyticalProcedure = new MTableMultiTester(k, p, alpha);

        ArrayList<MTableFailProbPair> analytical = analyticalProcedure.createDescendingMTables();
        ArrayList<Double> experimental = new ArrayList<>();

        for(MTableFailProbPair pair : analytical){
            Simulator sim = new Simulator(5000, k,p,pair.getAlpha());
            experimental.add(sim.run());
        }*/

        //CSVWriter writer = new CSVWriter();
        //writer.writeMTableFailProbPairAndSimulationFailProbPairToCSV(analytical, experimental);

    }
}
