package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

public class RecursiveTableFailprobabilityCalculator extends FailprobabilityCalculator {

    public RecursiveTableFailprobabilityCalculator(int k, double p, double alpha) {
        super(k, p, alpha);
    }

    public double calculateFailprobability() {
        int maxProtected = auxMTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxMTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1, blockSizes.size());
        ArrayList<ArrayList<Integer>> possibilities = findLegalAssignments(maxProtected, blockSizes);

        double successProb = 0;
        for (ArrayList<Integer> possability : possibilities) {
            double currentTrial = 1;
            for (int i = 0; i < possability.size(); i++) {
                currentTrial = currentTrial * getFromPmfCache(new BinomDistKey(blockSizes.get(i), possability.get(i)));
            }
            successProb += currentTrial;
        }

        return successProb == 0 ? 0 : 1 - successProb;
    }

    private ArrayList<ArrayList<Integer>> findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes) {
        ArrayList<Integer> prefix = new ArrayList<>();
        return findLegalAssignmentsAux(prefix, numCandidates, blockSizes);
    }

    private ArrayList<ArrayList<Integer>> findLegalAssignmentsAux(ArrayList<Integer> prefix, int numCandidates, ArrayList<Integer> blockSizes) {
        if (blockSizes.size() == 0) {
            ArrayList<ArrayList<Integer>> empty = new ArrayList<>();
            return empty;
        } else {
            int currentBlockNumber = prefix.size() + 1;
            int candidatesAssignedSoFar = sum(prefix);

            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if (minNeededThisBlock < 0) {
                minNeededThisBlock = 0;
            }
            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);
            ArrayList<ArrayList<Integer>> assignments = new ArrayList<>();

            ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());
            for (int itemsThisBlock = minNeededThisBlock; itemsThisBlock <= maxPossibleThisBlock; itemsThisBlock++) {
                int newRemainingCandidates = numCandidates - itemsThisBlock;

                ArrayList<Integer> newPrefix = new ArrayList<>(prefix);
                newPrefix.add(itemsThisBlock);
                ArrayList<ArrayList<Integer>> suffixes = findLegalAssignmentsAux(newPrefix, newRemainingCandidates, newRemainingBlockSizes);

                if (suffixes.size() == 0) {
                    ArrayList<Integer> newPossibility = new ArrayList<>(prefix);
                    newPossibility.add(itemsThisBlock);
                    assignments.add(newPossibility);
                } else {
                    for (int i = 0; i < suffixes.size(); i++) {
                        ArrayList<Integer> newPossibility = new ArrayList<>();
                        newPossibility.addAll(suffixes.get(i));
                        assignments.add(newPossibility);
                    }
                }
            }
            return assignments;
        }
    }


}
