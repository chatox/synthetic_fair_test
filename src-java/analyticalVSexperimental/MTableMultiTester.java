import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

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

    public double findMinimumStepForMTableChange() {
        double step = 0.0001;
        double stepsize = 0.0001;
        double alpha_c = this.alpha - step;
        MTableGenerator generator = new MTableGenerator(this.n, this.p, this.alpha - step);
        int[] modifiedMtable = generator.getMTable();

        while (mTablesAreEqual(modifiedMtable, this.mTable)) {
            alpha_c = alpha_c - step;
            stepsize += step;
            generator = new MTableGenerator(this.n, this.p, alpha_c);
            modifiedMtable = generator.getMTable();
        }


        return step;
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

    public boolean everyArrayEntryIsZero(int[] array) {
        for (int i : array) {
            if (i != 0) {
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
        while (currentMass > 1200 && currentAlpha>0) {
            System.out.println("looping...");
            MTableGenerator generator = new MTableGenerator(this.n, this.p, currentAlpha);

            while (mTablesAreEqual(currentTable, generator.getMTable())) {
                currentAlpha = currentAlpha -step;
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
        DataFrame auxMTable = computeAuxTMTable(mTable);
        int maxProtected = auxMTable.getLengthOf("inv") - 1;
        if (maxProtected == -1) {
            return alpha;
        }
        int minProtected = 1;
        double successProbability = 0;

        ArrayList<Double> currentTrial;

        double[] successObtainedProb = new double[maxProtected];
        successObtainedProb = fillWithZeros(successObtainedProb);
        successObtainedProb[0] = 1.0;
        //Cache for the probability Mass Function for every trial
        //a trial is a block and every list in pmfCache is the pmf of a block of
        //a certain size (pmfCache.get(2) is a list of the probability mass function values
        // of a block of the size 2)
        ArrayList<ArrayList<Double>> pmfCache = new ArrayList<>();

        while (minProtected < maxProtected) {
            //get the current blockLength from auxMTable
            int blockLength = auxMTable.at(minProtected, "block");
            if (blockLength < pmfCache.size() && pmfCache.get(blockLength) != null) {
                currentTrial = pmfCache.get(blockLength);
            } else {
                currentTrial = new ArrayList<>();
                //this has to be done to simulate an arrayList of the blocklength-size
                for (int j = 0; j <= blockLength; j++) {
                    currentTrial.add(null);
                }
                BinomialDistribution binomDist = new BinomialDistribution(blockLength, p);
                for (int i = 0; i <= blockLength; i++) {
                    //enter the pmf value for position i in a block of blockLength size
                    currentTrial.set(i, binomDist.probability(i));
                }

                //insert empty lists so that we have the current trial inserted on the right position
                pmfCache = adjustPmfCache(pmfCache, blockLength);
                pmfCache.set(blockLength, currentTrial);
            }
            //initialize with zeroes
            double[] newSuccessObtainedProb = fillWithZeros(new double[maxProtected]);
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

    private ArrayList<ArrayList<Double>> adjustPmfCache(ArrayList<ArrayList<Double>> pmfCache, int blocklength) {
        if (pmfCache.size() <= blocklength) {
            for (int i = pmfCache.size(); i <= blocklength; i++) {
                pmfCache.add(null);
            }
        }
        return pmfCache;
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

    private double[] fillWithZeros(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0;
        }

        return array;
    }

    /**
     * Shifts all entries of an array to the right for pos positions
     * Example: shiftToRight('1,2,3,4',2) ---> 3,4,1,2
     *
     * @param array the array that should be shifted
     * @param pos   positions to shift to the right
     * @return the shifted array
     */
    private double[] shiftToRight(double[] array, int pos) {
        double[] shifted = new double[array.length];
        pos = pos % array.length;
        for (int i = 0; i < shifted.length; i++) {
            if (pos == 0) {
                shifted[i] = array[i];
            } else if (i + pos > shifted.length - 1) {
                shifted[i % pos] = array[i];
            } else {
                shifted[i + pos] = array[i];
            }
        }
        return shifted;
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
