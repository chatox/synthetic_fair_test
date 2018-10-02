package analyticalVSexperimental;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class FailprobabilityCalculator {

    int k;
    double p;
    double alpha;
    int[] mTable;
    DataFrame auxMTable;
    HashMap<BinomDistKey, Double> pmfCache;

    public FailprobabilityCalculator(int k, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(k, p, alpha);
        this.mTable = generator.getMTable();
        this.auxMTable = generator.getAuxMTable();
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.pmfCache = new HashMap<>();
    }

    abstract double calculateFailprobability();

    ArrayList<Integer> sublist(ArrayList<Integer> array, int startIndex, int endIndex) {
        ArrayList<Integer> sublist = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            sublist.add(array.get(i));
        }
        return sublist;
    }

    int sum(ArrayList<Integer> array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }
}
