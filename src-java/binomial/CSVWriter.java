package binomial;

import java.io.*;
import java.util.ArrayList;

public class CSVWriter {

    public void writeMTableToCSV(int k, double p, double alpha, int[] mtable) throws Exception {
        String filename = "mtable_" + k + "_" + p + "_" + alpha + ".csv";
        //String filename = "hello.csv";
        PrintWriter pw = new PrintWriter(new File("C:\\Users\\TSuehr\\Documents\\Simulation\\src\\csvFiles\\java-csv\\" + filename));
        StringBuilder sb = new StringBuilder();

        for (int i : mtable) {
            sb.append(i);
            sb.append(';');
        }

        pw.write(sb.toString());
        pw.close();
    }

    public void appendStrToFile(String fileName, String str) {
        try {

            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePlotToCSV(String data, String title) throws FileNotFoundException {
        String filename = title + ".csv";
        PrintWriter pw = new PrintWriter(new File("C:\\Users\\Tom\\Desktop\\" + filename));
        pw.write(data);
        pw.close();
    }

    public void writeRankingToCSV(int k, double p, double alpha, ArrayList<Boolean> ranking) throws FileNotFoundException {
        String filename = "ranking_" + k + "_" + p + "_" + alpha + ".csv";
        //String filename = "hello.csv";
        PrintWriter pw = new PrintWriter(new File("C:\\Users\\TSuehr\\Documents\\Simulation\\src\\csvFiles\\java-csv\\" + filename));
        StringBuilder sb = new StringBuilder();

        for (Boolean b : ranking) {
            if (b) {
                sb.append(1);
            } else {
                sb.append(0);
            }
            sb.append(';');
        }
        pw.write(sb.toString());
        pw.close();
    }

    public double getPropotionFromCSVFile(int k, double p, double alpha) throws IOException {
        String filename = "ranking_" + k + "_" + p + "_" + alpha + ".csv";
        String csvFile = "C:\\Users\\Tom\\Desktop\\CIT\\Work\\synth-fair-binomial.test-java\\csvFiles\\java-csv\\" + filename;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        String[] ranking = new String[k];

        br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            ranking = line.split(cvsSplitBy);
        }
        int protectedCounter = 0;
        for (String s : ranking) {
            protectedCounter += Integer.parseInt(s);
        }

        return (double) protectedCounter / k;
    }

    public boolean mTableIsEqual(int k, double p, double alpha) throws IOException {
        String filename = "mtable_" + k + "_" + p + "_" + alpha + ".csv";
        String csvFileJava = "C:\\Users\\Tom\\Desktop\\CIT\\Work\\synth-fair-binomial.test-java\\csvFiles\\java-csv\\" + filename;
        String csvFilePython = "C:\\Users\\Tom\\Desktop\\CIT\\Work\\synth-fair-binomial.test-java\\csvFiles\\csv-python\\" + filename;
        BufferedReader brJava = null;
        BufferedReader brPython = null;
        String line = "";
        String cvsSplitBy = ";";
        String[] mtable_python = new String[k];
        String[] mtable_java = new String[k];
        brJava = new BufferedReader(new FileReader(csvFileJava));
        while ((line = brJava.readLine()) != null) {
            mtable_java = line.split(cvsSplitBy);
        }

        brPython = new BufferedReader(new FileReader(csvFilePython));
        line = brPython.readLine();
        mtable_python = line.split(cvsSplitBy);


        for (int i = 0; i < k; i++) {
            if (!mtable_java[i].equals(mtable_python[i])) {
                System.out.println("k:" + k + ", p:" + p + ", alpha:" + alpha + " Mtable is different at position: " + i + " pythontable: " + mtable_python[i] + ", javaTable: " + mtable_java[i] + ".");
            }
        }

        return true;
    }

}