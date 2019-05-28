package multinomial.util;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class InverseMultinomialCDFThread implements Callable<ArrayList<int[]>> {

    ArrayList<int[]> tuples;
    private MCDFCache mcdfCache;
    private double alpha;
    private double[] p;

    public InverseMultinomialCDFThread(String name, ArrayList<int[]> tuples, MCDFCache mcdfCache,double[] p, double alpha) {
        this.tuples = tuples;
        this.mcdfCache = mcdfCache;
        this.alpha = alpha;
        this.p = p;
    }

    private ArrayList<int[]> inverseMultinomialCDF(int[] tuple) {
        int[] minProportions = new int[tuple.length];
        System.arraycopy(tuple, 0, minProportions, 0, tuple.length);
        minProportions[0]++;
        double mcdf = mcdfCache.mcdf(minProportions);
        if (mcdf > alpha) {
            int[] data = new int[minProportions.length];
            System.arraycopy(minProportions, 0, data, 0, minProportions.length);
            ArrayList<int[]> result = new ArrayList<>();
            result.add(data);
            return result;
        } else {
            ArrayList<int[]> result = new ArrayList<>();
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = mcdfCache.mcdf(temp);
                if (mcdfTemp > alpha) {
                    result.add(temp);
                }
            }
            return result;
        }
    }

    @Override
    public ArrayList<int[]> call() throws Exception {
        ArrayList<int[]> children = new ArrayList<>();
        for(int[] tuple : tuples){
            children.addAll(inverseMultinomialCDF(tuple));
        }
        return children;
    }
}
