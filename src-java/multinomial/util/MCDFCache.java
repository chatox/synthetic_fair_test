package multinomial.util;

import binomial.CSVWriter;
import binomial.analyticalVSexperimental.RecursiveNumericFailprobabilityCalculator;
import multinomial.MultinomialSimulator;
import umontreal.ssj.probdistmulti.MultinomialDist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class MCDFCache {

    private double[] p;
    private HashMap<int[],Double> mcdfCache;

    public MCDFCache(double[] p){
        this.p = p;
        this.mcdfCache = new HashMap<>();
    }

    public double mcdf(int[] signature){
        int trials = signature[0];
        if(mcdfCache.get(signature)==null){
            double cdf = MultinomialDist.cdf(trials,p,signature);
            mcdfCache.put(signature, cdf);
            return cdf;
        }
        return mcdfCache.get(signature);
    }
}