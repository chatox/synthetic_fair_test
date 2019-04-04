package binomial.analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

public class MTableGenerator {

    private int[] mTable;
    private DataFrame auxMTable;
    private int n;
    private double p;
    private double alpha;


    /**
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       the significance level
     */
    public MTableGenerator(int n, double p, double alpha) {
            this.n = n;
            this.p = p;
            this.alpha = alpha;
            this.mTable = computeMTable();
            this.auxMTable = computeAuxMTable();
    }

    private int[] computeMTable() {
        int[] table = new int[this.n+1];
        table[0] = 0;
        for (int i = 1; i < this.n+1; i++) {
            table[i] = m(i);
        }
        return table;
    }

    /**
     * Stores the inverse of an mTable entry and the size of the block with respect to the inverse
     *
     * @return A Dataframe with the columns "inv" and "block" for the values of the inverse mTable and blocksize
     */
    private DataFrame computeAuxMTable(){
        DataFrame table = new DataFrame("inv", "block");
        int lastMSeen = 0;
        int lastPosition = 0;
        for (int position = 1; position < this.mTable.length; position++) {
            if (this.mTable[position] == lastMSeen + 1) {
                lastMSeen += 1;
                table.put(position, position, (position - lastPosition));
                lastPosition = position;
            } else if (this.mTable[position] != lastMSeen) {
                throw new RuntimeException("Inconsistent mtable");
            }
        }
        table.resolveNullEntries();
        return table;
    }

    public DataFrame computeAuxMTable(int[] mTable){
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

    private Integer m(int k) {

        BinomialDistribution dist = new BinomialDistribution(k, p);

        return dist.inverseCumulativeProbability(alpha);

    }

    public int[] getMTable() {
        return mTable;
    }

    public int getN() {
        return n;
    }

    public double getP() {
        return p;
    }

    public double getOriginalAlpha() {
        return alpha;
    }


    public DataFrame getAuxMTable() {
        return auxMTable;
    }
}
