package analyticalVSexperimental;

import analyticalVSexperimental.MTableGenerator;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;
import java.util.HashMap;


public class MTableMultiTester {


    private int n;
    private double p;
    private double alpha;
    private int[] mTable;
    private int currentHigh;
    private MTableGenerator mTableGenerator;

    /*
        Algorithm 1 of the k fair ranking Paper
        @param k the size of the ranking to produce
        @param p the expected proportion of protected elements
        @param alpha the significance for each individual test
        @return the probability of rejecting a fair ranking
     */

    public MTableMultiTester(int n, double p, double alpha) {
        this.n = n;
        this.p = p;
        this.alpha = alpha;
        this.currentHigh = 0;

        this.mTableGenerator = new MTableGenerator(n, p, alpha);
        this.mTable = this.mTableGenerator.getMTable();
        if (this.mTable.length <= 1) {
            throw new IllegalStateException("n must be at least 1");
        }
    }

    public boolean mTablesAreEqual(int[] mtable1, int[] mTable2) {
        if (mtable1.length != mTable2.length) {
            return false;
        }
        for (int i = 0; i < mtable1.length; i++) {
            if (mtable1[i] != mTable2[i]) {
                return false;
            }
        }
        return true;
    }

    public int[] copyArray(int[] array) {
        int[] copy = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = array[i];
        }
        return copy;
    }

    public ArrayList<MTableFailProbPair> createDescendingMTables() {
        ArrayList<MTableFailProbPair> mTableList = new ArrayList<>();
        int[] currentTable = copyArray(this.mTable);

        mTableList.add(new MTableFailProbPair(n, p, alpha, computeFailureProbability(this.mTable), this.mTable));
        int currentMass = getMassOfMTable(currentTable);
        double step = 0.00001;
        double currentAlpha = alpha - step;
        while (currentMass > 1200 && currentAlpha > 0) {
            System.out.println("looping...");
            MTableGenerator generator = new MTableGenerator(this.n, this.p, currentAlpha);

            while (mTablesAreEqual(currentTable, generator.getMTable())) {
                currentAlpha = currentAlpha - step;
                generator = new MTableGenerator(this.n, this.p, currentAlpha);
            }

            mTableList.add(new MTableFailProbPair(n, p, currentAlpha, computeFailureProbability(generator.getMTable()), generator.getMTable()));
            currentTable = generator.getMTable();
            currentMass = getMassOfMTable(currentTable);
            System.out.println(currentMass);

        }
        return mTableList;
    }

    public int getMassOfMTable(int[] table) {
        int mass = 0;
        for (int i = 0; i < table.length; i++) {
            mass += table[i];
        }
        return mass;
    }

    /**
     * Stores the inverse of an mTable entry and the size of the block with respect to the inverse
     *
     * @return A Dataframe with the columns "inv" and "block" for the values of the inverse mTable and blocksize
     */
    public DataFrame computeAuxTMTable(int[] mTable) {
        DataFrame table = new DataFrame("inv", "block");
        int lastMSeen = 0;
        int lastPosition = 0;
        for (int position = 1; position < mTable.length; position++) {
            if (mTable[position] == lastMSeen + 1) {
                lastMSeen += 1;
                table.put(position, position, (position - lastPosition));
                lastPosition = position;
            } else if (mTable[position] != lastMSeen) {
                throw new RuntimeException("Inconsistent mtable");
            }
        }

        table.resolveNullEntries();
        return table;
    }

    /**
     * Computes the probability of rejecting a fair ranking with the given parameters n, p and alpha
     *
     * @return The probability of rejecting a fair ranking
     */
    public double computeFailureProbability(int[] mTable) {
        if (mTable[mTable.length - 1] == 0) {
            return 0;
        }
        DataFrame auxMTable = computeAuxTMTable(mTable);
        int maxProtected = auxMTable.getLengthOf("inv") - 1;
        int minProtected = 1;
        double successProbability = 0;
        ArrayList<Double> currentTrial;
        double[] successObtainedProb = new double[maxProtected];
        successObtainedProb[0] = 1.0;
        HashMap<Integer, ArrayList<Double>> pmfCache = new HashMap<>();

        while (minProtected <= maxProtected) {
            //get the current blockLength from auxMTable

            int blockLength = auxMTable.at(minProtected, "block");
            System.out.println(blockLength);
            if (pmfCache.get(blockLength) != null) {
                currentTrial = pmfCache.get(blockLength);
            } else {
                currentTrial = new ArrayList<>();
                BinomialDistribution binomDist = new BinomialDistribution(blockLength, p);
                for (int i = 0; i <= blockLength; i++) {
                    currentTrial.add(binomDist.probability(i));
                }
                pmfCache.put(blockLength, currentTrial);
            }
            //initialize with zeroes
            double[] newSuccessObtainedProb = new double[maxProtected];
            for (int i = 0; i <= blockLength; i++) {
                //shifts all values to the right for i positions (like python.roll)
                //multiplies the current value with the currentTrial of the right position
                double[] increase = increase(i, successObtainedProb, currentTrial);
                //store the result
                newSuccessObtainedProb = addEntryWise(increase, newSuccessObtainedProb);
            }

            newSuccessObtainedProb[minProtected - 1] = 0;


            successObtainedProb = newSuccessObtainedProb;
            successProbability = sum(successObtainedProb);

            minProtected += 1;
        }

        return 1 - successProbability;

    }


    private double[] increase(int i, double[] successObtainedProb, ArrayList<Double> currentTrial) {
        double[] shifted = shiftToRight(successObtainedProb, i);
        for (int j = 0; j < shifted.length; j++) {
            shifted[j] = shifted[j] * currentTrial.get(i);
        }
        return shifted;
    }


    private double[] addEntryWise(double[] arrayOne, double[] arrayTwo) {
        double[] sum = new double[arrayOne.length];
        for (int i = 0; i < arrayOne.length; i++) {
            sum[i] = arrayOne[i] + arrayTwo[i];
        }
        return sum;
    }

    /**
     * Shifts all entries of an array to the right for pos positions
     * Example: shiftToRight('1,2,3,4',2) ---> 3,4,1,2
     *
     * @param nums the array that should be shifted
     * @param k    positions to shift to the right
     * @return the shifted array
     */

    private double[] shiftToRight(double[] nums, int k) {
        if (k > nums.length)
            k = k % nums.length;

        double[] result = new double[nums.length];

        for (int i = 0; i < k; i++) {
            result[i] = nums[nums.length - k + i];
        }

        int j = 0;
        for (int i = k; i < nums.length; i++) {
            result[i] = nums[j];
            j++;
        }
        return result;
    }

    private double sum(double[] array) {
        double sum = 0;
        for (double anArray : array) {
            sum += anArray;
        }
        return sum;
    }

    private String mTableToString(int[] array) {
        String s = "[";
        for (int i : array) {
            s += "" + i;
            s += ",";
        }
        s += "]";
        return s;
    }


}
