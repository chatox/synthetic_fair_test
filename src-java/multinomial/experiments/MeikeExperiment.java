package multinomial.experiments;

import java.io.IOException;

import multinomial.util.MeikeDataFrame;

public class MeikeExperiment {
    
    public static void cleanData() {
        
    }
    
    public static void main (String[] args) throws IOException {
        /**
         * command arguments
         *      args[0] = experiment name (COMPAS xor GermanCredit xor LSAT)
         */
        MeikeDataFrame data = new MeikeDataFrame();
        //Which experiment?
        switch (args[0]) {
        case "COMPAS":
            data.readCSV(System.getProperty("user.dir") + "../../data/COMPAS/compas_sexAgeRace", ",", true);
            break;
        case "GermanCredit":
            data.readCSV(System.getProperty("user.dir") + "../../data/GermanCredit/germanCredit_sexAgeForeigner.csv", "   ", true);
            break;
        case "LSAT":
            break;

        default:
            throw new IllegalArgumentException("experiment needs to be specified: either COMPAS or GermanCredit or LSAT");
        }
        System.out.println("Running German Credit Experiment");
    }
}
