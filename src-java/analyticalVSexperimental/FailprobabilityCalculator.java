package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

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

    double getFromPmfCache(BinomDistKey key){
        if(pmfCache.containsKey(key)){
            return pmfCache.get(key);
        }else{
            BinomialDistribution binomialDistribution = new BinomialDistribution(key.getTrials(), p);
            double probability = binomialDistribution.probability(key.getSuccesses());
            pmfCache.put(key,probability);
            return probability;
        }
    }

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
