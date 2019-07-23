package multinomial.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class MultinomialMTableGenerator {

    private int k;
    private double[] p;
    private double alpha;
    private MCDFCache mcdfCache;
    private HashMap<Integer,ArrayList<int[]>> mtable;
    private ArrayList<int[]> hasAMirror = new ArrayList<>();
    public static int cores = 2;

    public MultinomialMTableGenerator(int k, double[] p, double alpha, MCDFCache mcdfCache) {
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.mcdfCache = mcdfCache;
    }

    public HashMap<Integer,ArrayList<int[]>> getMtable() throws ExecutionException, InterruptedException {
        if(this.mtable == null){
            return computeMultinomialMtable();
        }else{
            return mtable;
        }
    }

    private HashMap<Integer,ArrayList<int[]>> computeMultinomialMtable() throws ExecutionException, InterruptedException {
        this.mtable = new HashMap<>();
        int position = 0;
        boolean symmetricPreferences = getSymmetricMode();
        int[] root = new int[this.p.length];
        ArrayList<int[]> positionZero = new ArrayList<>();
        positionZero.add(root);
        mtable.put(position, positionZero);
        while (position < this.k) {
            HashMap<int[],Boolean> childCache = new HashMap<>();
            ArrayList<int[]> currentLevel = mtable.get(position);
            ArrayList<int[]> currentChildCandidates = new ArrayList<>();
//            System.out.println(currentLevel.size());
            if(currentLevel.size()>=15){
//                long start = System.nanoTime();
                int size = currentLevel.size();
                int part = currentLevel.size()/cores;
                ExecutorService pool = Executors.newFixedThreadPool(cores);
                Set<Future<ArrayList<int[]>>> set = new HashSet<>();
                for(int i = 0; i<cores; i++){
                    int min = i*part;
                    int max = 0;
                    if(i==cores-1){
                        max = size;
                    }else{
                        max = (i+1)*part;
                    }
                    Callable<ArrayList<int[]>> callable = new InverseMultinomialCDFThread("t"+i,new ArrayList<>(currentLevel.subList(min,max)),mcdfCache,p,alpha);
                    Future<ArrayList<int[]>> future = pool.submit(callable);
                    set.add(future);
                }
                for(Future<ArrayList<int[]>> f : set){
                    currentChildCandidates.addAll(f.get());
                }
//                System.out.println("generating all childs: "+(System.nanoTime()-start)/1000000000.0+" --- size: "+currentChildCandidates.size());
            }else{
                for (int[] tuple : currentLevel) {
                    currentChildCandidates.addAll(inverseMultinomialCDF(tuple));
                }
            }
            ArrayList<int[]> finalChildCandidates = new ArrayList<>();

            for (int[] child : currentChildCandidates){
                if(!containsSignature(finalChildCandidates,child)){
                    if(!symmetricPreferences){
                        finalChildCandidates.add(child);
                    }else{
                        if(!containsMirrorImage(finalChildCandidates,child)){
                            finalChildCandidates.add(child);
                        }
                    }
                }
            }
            position++;
            mtable.put(position,finalChildCandidates);
        }
        return mtable;
    }

    private boolean getSymmetricMode() {
        for(int i=1; i<p.length; i++){
            for(int j=1; j<p.length; j++){
                if (p[i] != p[j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean containsMirrorImage(ArrayList<int[]> list, int[] sig) {
        for (int[] a : list) {
            int found = 0;
            for (int i = 1; i < a.length; i++) {
                if (a[a.length-i] == sig[i]) {
                    found++;
                } else {
                    break;
                }
            }
            if (found == sig.length-1) {
                hasAMirror.add(sig);
                return true;
            }
        }
        return false;
    }

    private ArrayList<int[]> inverseMultinomialCDF(int[] tuple) {

        int[] minProportions = new int[tuple.length];
        System.arraycopy(tuple, 0, minProportions, 0, tuple.length);
        minProportions[0]++;
        ArrayList<int[]> result = new ArrayList<>();
        double mcdf = mcdfCache.mcdf(minProportions);
        if (mcdf > alpha) {
            int[] data = new int[minProportions.length];
            System.arraycopy(minProportions, 0, data, 0, minProportions.length);
            result.add(data);
        } else {
            for (int i = 1; i < this.p.length; i++) {
                int[] temp = new int[minProportions.length];
                System.arraycopy(minProportions, 0, temp, 0, minProportions.length);
                temp[i]++;
                double mcdfTemp = mcdfCache.mcdf(temp);
                if (mcdfTemp > alpha) {
                    result.add(temp);
                }
            }
        }
        return result;
    }

    private boolean containsSignature(ArrayList<int[]> list, int[] sig) {
        for (int[] a : list) {
            int found = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i] == sig[i]) {
                    found++;
                } else {
                    break;
                }
            }
            if (found == sig.length) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<int[]> getMirrors() {
        if(hasAMirror.size()>0){
            return this.hasAMirror;
        }
        return null;
    }
}
