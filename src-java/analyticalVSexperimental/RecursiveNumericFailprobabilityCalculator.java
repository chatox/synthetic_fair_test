package analyticalVSexperimental;

import java.util.ArrayList;

public class RecursiveNumericFailprobabilityCalculator extends FailprobabilityCalculator {

    private double successProb;

    public RecursiveNumericFailprobabilityCalculator(int k, double p, double alpha) {
        super(k, p, alpha);
        this.successProb = 0;
    }

    @Override
    public double calculateFailprobability() {
        int maxProtected = auxMTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxMTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1, blockSizes.size());
        findLegalAssignments(maxProtected, blockSizes);

        return this.successProb == 0 ? 0 : 1 - this.successProb;
    }

    public double findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes) {
        double prefix = 1;
        return findLegalAssignmentsAux(prefix, numCandidates, blockSizes, 1, 0);
    }

    public double findLegalAssignmentsAux(double prefix, int numCandidates, ArrayList<Integer> blockSizes, int currentBlockNumber, int candidatesAssignedSoFar) {
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

                double newPrefix = prefix * getFromPmfCache(new BinomDistKey(maxPossibleThisBlock, itemsThisBlock));
                double suffixes = findLegalAssignmentsAux(newPrefix, newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber + 1, candidatesAssignedSoFar + itemsThisBlock);

                assignments = assignments + newPrefix * suffixes;
                if (blockSizes.size() == 1) {
                    this.successProb += newPrefix;
                }
            }
            return assignments;
        }
    }


}
