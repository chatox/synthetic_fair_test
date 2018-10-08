package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;


public class RecursiveBlockMatrixFailprobabilityCalculator extends FailprobabilityCalculator {

    private ArrayList<SuccessProbBlockMatrixKey> blockMatrixKeys;
    private int trialNumber = 0;


    public RecursiveBlockMatrixFailprobabilityCalculator(int k, double p, double alpha) {
        super(k, p, alpha);
        this.blockMatrixKeys = new ArrayList<>();

    }

    @Override
    public double calculateFailprobability() {
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
                if (blockMatrix[i][j] == null) {
                    blockMatrix[i][j] = blockMatrix[i - 1][j];
                }
            }
        }
        double successProbability = 0;
        for (int i = 0; i < trialNumber; i++) {
            double currentTrial = 1;
            for (int j = 0; j < blockSizes.size(); j++) {
                currentTrial = currentTrial * getFromPmfCache(blockSizes.get(j), blockMatrix[i][j]);
            }
            successProbability += currentTrial;
        }

        return 1 - successProbability;
    }

    private void findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes) {
        findLegalAssignmentsAux(numCandidates, blockSizes, 1, 0);
    }

    private void findLegalAssignmentsAux(int numCandidates, ArrayList<Integer> blockSizes, int currentBlockNumber, int candidatesAssignedSoFar) {
        if (blockSizes.size() == 0) {
            trialNumber++;
        } else {
            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if (minNeededThisBlock < 0) {
                minNeededThisBlock = 0;
            }
            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);

            ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());
            for (int itemsThisBlock = minNeededThisBlock; itemsThisBlock <= maxPossibleThisBlock; itemsThisBlock++) {
                int newRemainingCandidates = numCandidates - itemsThisBlock;

                blockMatrixKeys.add(new SuccessProbBlockMatrixKey(currentBlockNumber, trialNumber, itemsThisBlock));
                findLegalAssignmentsAux(newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber + 1, candidatesAssignedSoFar + itemsThisBlock);
            }
        }
    }

}
