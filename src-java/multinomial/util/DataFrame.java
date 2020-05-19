package multinomial.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataFrame {
    private Double[][] data;
    private String[] columnHeaders;
    
    public DataFrame (String[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public Double[][] readCSV(String filename, String separator) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        List<Double[]> lines = new ArrayList<Double[]>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] cells = line.split(separator);
            Double[] cellsAsDouble = Arrays.stream(cells).map(Double::valueOf).toArray(Double[]::new);
            lines.add(cellsAsDouble);
        }
        assert columnHeaders.length == lines.size();
        reader.close();
        // convert list to a String array.
        this.data = new Double[lines.size()][0];
        lines.toArray(this.data);
        return this.data;
    }
}
