import org.apache.commons.math3.distribution.BinomialDistribution;

public class MTableGenerator {

    private int[] mTable;
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
    }

//    private int[] computeMTable() {
//        int[] table = new int[this.n+1];
//        table[0] = 0;
//        for (int i = 1; i <= this.n; i++) {
//            table[i] = m(i);
//        }
//        return table;
//    }

    private int[] computeMTable() {
        int[] table = new int[this.n+1];

        for (int i = 0; i < this.n+1; i++) {
            table[i] = m(i);
        }
        return table;
    }

    private Integer m(int k) {

        BinomialDistribution dist = new BinomialDistribution(k, p);

        return dist.inverseCumulativeProbability(alpha);

    }

    public int[] getMTable() {
        if (this.mTable == null) {
            this.mTable = computeMTable();
        }
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


}
