package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;
import java.util.HashMap;

public class RecursiveBlockMatrixFailprobabilityCalculator {

    private int k;
    private double p;
    private double alpha;
    private int[] mTable;
    private DataFrame auxMTable;
    private HashMap<BinomDistKey, Double> pmfCache;
    private ArrayList<SuccessProbBlockMatrixKey> blockMatrixKeys;
    private int trialNumber = 0;


    public RecursiveBlockMatrixFailprobabilityCalculator(int k, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(k, p, alpha);
        this.mTable = generator.getMTable();
        this.auxMTable = generator.getAuxMTable();
        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.pmfCache = new HashMap<>();
        this.blockMatrixKeys = new ArrayList<>();

    }

    public double calculateFailProbability() {
        int maxProtected = auxMTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxMTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1, blockSizes.size());
        findLegalAssignments(maxProtected, blockSizes);
        Integer[][] blockMatrix = new Integer[trialNumber][blockSizes.size()];

        for (SuccessProbBlockMatrixKey key : blockMatrixKeys) {
            blockMatrix[key.getTrialNumber()][key.getBlocknumber() - 1] = key.getCandidatesThisBlock();
        }

        for (int i = 0; i < trialNumber; i++) {
            for (int j = 0; j < blockSizes.size(); j++) {
                if (blockMatrix[i][j] == null){
                    blockMatrix[i][j] = blockMatrix[i-1][j];
                }
            }
        }
        double successProbability = 0;
        for(int i = 0; i<trialNumber; i++){
            double currentTrial = 1;
            for(int j = 0; j<blockSizes.size(); j++){
                currentTrial = currentTrial*pmfCache.get(new BinomDistKey(blockSizes.get(j),blockMatrix[i][j]));
            }
            successProbability += currentTrial;
        }

        return 1-successProbability;
    }

    private void findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes) {
        double prefix = 1;
        for(int i=1; i<=blockSizes.get(0); i++){
            findLegalAssignmentsAux(numCandidates-i, blockSizes, 2, i);
        }
    }

    private void findLegalAssignmentsAux(int numCandidates, ArrayList<Integer> blockSizes, int currentBlockNumber, int candidatesAssignedSoFar) {
        if (blockSizes.size() == 0) {
            trialNumber++;
            return;

        } else {
            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if (minNeededThisBlock < 0) {
                minNeededThisBlock = 0;
            }
            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);
            double assignments = 0;

            ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());
            for (int itemsThisBlock = minNeededThisBlock; itemsThisBlock <= maxPossibleThisBlock; itemsThisBlock++) {
                int newRemainingCandidates = numCandidates - itemsThisBlock;
                if (pmfCache.get(new BinomDistKey(maxPossibleThisBlock, itemsThisBlock)) == null) {
                    BinomialDistribution binomialDistribution = new BinomialDistribution(maxPossibleThisBlock, p);
                    pmfCache.put(new BinomDistKey(maxPossibleThisBlock, itemsThisBlock), binomialDistribution.probability(itemsThisBlock));
                }

                blockMatrixKeys.add(new SuccessProbBlockMatrixKey(currentBlockNumber, trialNumber, itemsThisBlock));
                findLegalAssignmentsAux(newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber + 1, candidatesAssignedSoFar + itemsThisBlock);
            }
            return;
        }
    }

    private ArrayList<Integer> sublist(ArrayList<Integer> array, int startIndex, int endIndex) {
        ArrayList<Integer> sublist = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            sublist.add(array.get(i));
        }
        return sublist;
    }


}
