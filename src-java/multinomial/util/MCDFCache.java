package multinomial.util;

import umontreal.ssj.probdistmulti.MultinomialDist;

import java.util.HashMap;

public class MCDFCache {

    private int k;
    private double[] p;
    private double alpha;
    private HashMap<int[],Double> mcdfCache;

    public MCDFCache(int k, double[] p, double alpha){
        this.k=k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = new HashMap<>();
    }

    public double mcdf(int[] signature){
        int trials = signature[0];
        if(mcdfCache.get(signature)==null){
            mcdfCache.put(signature, MultinomialDist.cdf(trials,p,signature));
        }
        return mcdfCache.get(signature);
    }
}
