package multinomial.util;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Arrays;
import java.util.Scanner;

public class DataFrame {
    private float [][] data;
    
    public DataFrame() {
        this.data = new float[][];
    }
    
    public DataFrame(int rows, int columns) {
        this.data = new float[rows][columns];
    }

    public float[][] readCSV(String file) {
        Scanner sc = new Scanner(new BufferedReader(new FileReader("sample.txt")));
        while(sc.hasNextLine()) {
            for (int i=0; i<myArray.length; i++) {
                String[] line = sc.nextLine().trim().split(" ");
                for (int j=0; j<line.length; j++) {
                    myArray[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
        
        return this.data;
    }
    public static void main(String args[]) throws Exception {
        int rows = 4;
          int columns = 4;
          System.out.println(Arrays.deepToString(myArray));
    }
}

