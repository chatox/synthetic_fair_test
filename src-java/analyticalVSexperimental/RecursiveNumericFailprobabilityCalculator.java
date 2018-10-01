package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;
import java.util.HashMap;

public class RecursiveNumericFailprobabilityCalculator {

    private int k;
    private double p;
    private double alpha;
    private int[] mTable;
    private DataFrame auxMTable;
    private HashMap<BinomDistKey,Double> pmfCache;


    public RecursiveNumericFailprobabilityCalculator(int k, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        this.mTable = generator.getMTable();
        this.auxMTable = generator.getAuxMTable();
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.pmfCache = new HashMap<>();
    }

    public double calculateFailProbability(){
        int maxProtected = auxMTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxMTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1,blockSizes.size());
        double possibilities = findLegalAssignments(maxProtected, blockSizes);



        return possibilities;
    }

    private double findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes){
        double prefix = 1;
        return  findLegalAssignmentsAux(prefix, numCandidates, blockSizes,1, 0);
    }

    private double findLegalAssignmentsAux(double prefix, int numCandidates, ArrayList<Integer> blockSizes, int currentBlockNumber, int candidatesAssignedSoFar) {
        if(blockSizes.size() == 0){
            return 1;
        }else{
            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if(minNeededThisBlock<0){
                minNeededThisBlock=0;
            }
            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);
            double assignments = 0;

            ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());
            for(int itemsThisBlock = minNeededThisBlock; itemsThisBlock<= maxPossibleThisBlock; itemsThisBlock++){
                int newRemainingCandidates = numCandidates - itemsThisBlock;
                if(pmfCache.get(new BinomDistKey(maxPossibleThisBlock,itemsThisBlock)) == null){
                    BinomialDistribution binomialDistribution = new BinomialDistribution(maxPossibleThisBlock,p);
                    pmfCache.put(new BinomDistKey(maxPossibleThisBlock,itemsThisBlock),binomialDistribution.probability(itemsThisBlock));
                }
                double newPrefix = prefix * pmfCache.get(new BinomDistKey(maxPossibleThisBlock,itemsThisBlock));
                double suffixes = findLegalAssignmentsAux(newPrefix, newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber+1, numCandidates+itemsThisBlock);

                assignments = assignments + newPrefix*suffixes;
            }
            return assignments;
        }
    }

    private ArrayList<Integer> sublist(ArrayList<Integer> array, int startIndex, int endIndex){
        ArrayList<Integer> sublist = new ArrayList<>();
        for(int i=startIndex; i<endIndex; i++){
            sublist.add(array.get(i));
        }
        return sublist;
    }



}
