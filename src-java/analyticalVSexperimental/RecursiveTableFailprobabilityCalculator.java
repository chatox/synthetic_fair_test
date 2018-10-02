package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

public class RecursiveTableFailprobabilityCalculator {


    private int[] mtable;
    private DataFrame auxTable;
    private double p;
    private double alpha;


    public RecursiveTableFailprobabilityCalculator(int k, double p, double alpha){
        MTableGenerator generator = new MTableGenerator(k,p,alpha);
        this.mtable = generator.getMTable();
        this.auxTable = generator.getAuxMTable();
        this.p = p;
        this.alpha = alpha;
    }

    public double calculateFailProbability(){
        int maxProtected = auxTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1,blockSizes.size());
        ArrayList<ArrayList<Integer>> possibilities = findLegalAssignments(maxProtected, blockSizes);

        double successProb = 0;
        for(ArrayList<Integer> possability : possibilities){
            double currentTrial = 1;
            for(int i=0; i<possability.size(); i++){
                BinomialDistribution binomialDistribution = new BinomialDistribution(blockSizes.get(i), p);
                currentTrial = currentTrial * binomialDistribution.probability(possability.get(i));
            }
            successProb += currentTrial;
        }

        return successProb == 0 ? 0 : 1-successProb;
    }

    private ArrayList<ArrayList<Integer>> findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes){
        ArrayList<Integer> prefix = new ArrayList<>();
        return  findLegalAssignmentsAux(prefix, numCandidates, blockSizes);
    }

    private ArrayList<ArrayList<Integer>> findLegalAssignmentsAux(ArrayList<Integer> prefix, int numCandidates, ArrayList<Integer> blockSizes) {
        if(blockSizes.size() == 0){
            ArrayList<ArrayList<Integer>> empty = new ArrayList<>();
            return empty;
        }else{
            int currentBlockNumber = prefix.size()+1;
            int candidatesAssignedSoFar= sum(prefix);

            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if(minNeededThisBlock<0){
                minNeededThisBlock=0;
            }
            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);
            ArrayList<ArrayList<Integer>> assignments = new ArrayList<>();

            ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());
            for(int itemsThisBlock = minNeededThisBlock; itemsThisBlock<= maxPossibleThisBlock; itemsThisBlock++){
                int newRemainingCandidates = numCandidates - itemsThisBlock;

                ArrayList<Integer> newPrefix = new ArrayList<>(prefix);
                newPrefix.add(itemsThisBlock);
                ArrayList<ArrayList<Integer>> suffixes = findLegalAssignmentsAux(newPrefix, newRemainingCandidates, newRemainingBlockSizes);

                if(suffixes.size()==0){
                    ArrayList<Integer> newPossibility = new ArrayList<>(prefix);
                    newPossibility.add(itemsThisBlock);
                    assignments.add(newPossibility);
                }else{
                    for(int i=0; i<suffixes.size(); i++){
                        ArrayList<Integer> newPossibility = new ArrayList<>();
                        newPossibility.addAll(suffixes.get(i));
                        assignments.add(newPossibility);
                    }
                }
            }
            return assignments;
        }
    }

    private int sum(ArrayList<Integer> array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }

    private ArrayList<Integer> sublist(ArrayList<Integer> array, int startIndex, int endIndex){
        ArrayList<Integer> sublist = new ArrayList<>();
        for(int i=startIndex; i<endIndex; i++){
            sublist.add(array.get(i));
        }
        return sublist;
    }

}
