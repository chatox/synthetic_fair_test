package multinomial.experiments.germancredit;

import java.io.IOException;

import multinomial.util.DataFrame;

public class Experiment {
    
    public static void cleanData() {
        
    }
    
    public static void main (String[] args) throws IOException {
        /**
         * command arguments
         *      args[0] = experiment name (COMPAS xor GermanCredit xor LSAT)
         *      args[-1] = column headers of data file as one argument (needs to be put into "")
         */
        String[] columnHeaders = args[args.length - 1].split(" ");
        //Which experiment?
        switch (args[0]) {
        case "COMPAS":
            
            break;
        case "GermanCredit":
            DataFrame frame = new DataFrame(columnHeaders);
            frame.readCSV(System.getProperty("user.dir") + "../../data/GermanCredit/german.data-numeric", "   ");
            break;
        case "LSAT":
            break;

        default:
            throw new IllegalArgumentException("experiment needs to be specified: either COMPAS or GermanCredit or LSAT");
        }
        System.out.println("Running German Credit Experiment");
    }
}
