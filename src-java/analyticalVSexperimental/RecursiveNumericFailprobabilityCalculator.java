package analyticalVSexperimental;

import java.util.ArrayList;
import java.util.HashMap;

public class RecursiveNumericFailprobabilityCalculator extends FailprobabilityCalculator {

    private HashMap<LegalAssignmentKey, Double> legalAssignmentCache = new HashMap<>();

    public RecursiveNumericFailprobabilityCalculator(int k, double p, double alpha) {
        super(k, p, alpha);
    }

    @Override
    public double calculateFailprobability() {
        int maxProtected = auxMTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxMTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1, blockSizes.size());
        double succesProb = findLegalAssignments(maxProtected, blockSizes);
        return succesProb == 0 ? 0 : 1 - succesProb;
    }

    public double findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes) {

        return findLegalAssignmentsAux(numCandidates, blockSizes, 1, 0);
    }

    public double findLegalAssignmentsAux(int numCandidates, ArrayList<Integer> blockSizes, int currentBlockNumber, int candidatesAssignedSoFar) {
        if (blockSizes.size() == 0) {
            return 1;
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

                double suffixes = calculateLegalAssignmentsAux(newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber + 1, candidatesAssignedSoFar + itemsThisBlock);

                assignments = assignments + getFromPmfCache(maxPossibleThisBlock, itemsThisBlock) * suffixes;

            }
            return assignments;
        }
    }

    private double calculateLegalAssignmentsAux(int remainingCandidates, ArrayList<Integer> remainingBlockSizes, int currentBlockNumber, int candidatesAssignedSoFar){
        LegalAssignmentKey key = new LegalAssignmentKey(remainingCandidates,remainingBlockSizes,currentBlockNumber,candidatesAssignedSoFar);
        if(legalAssignmentCache.get(key)!= null){
            return legalAssignmentCache.get(key);
        }else{
            double value = findLegalAssignmentsAux(remainingCandidates,remainingBlockSizes,currentBlockNumber,candidatesAssignedSoFar);
            legalAssignmentCache.put(key,value);
            return value;
        }

    }


}
