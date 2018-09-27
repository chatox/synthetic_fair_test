import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

public class RecursiveTableFailprobabilityCalculator {


    private int[] mtable;
    private DataFrame auxTable;
    private double p;
    private double alpha;



    public RecursiveTableFailprobabilityCalculator(int[] mtable, double p, double alpha){
        this.mtable = mtable;
        this.auxTable = computeAuxTMTable();
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

        return 1-successProb;
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

            for(int itemsThisBlock = minNeededThisBlock; itemsThisBlock<= maxPossibleThisBlock; itemsThisBlock++){
                int newRemainingCandidates = numCandidates - itemsThisBlock;
                ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());

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

    private ArrayList<Integer> concatToNewArray(ArrayList<Integer> array, Integer i){
        ArrayList<Integer> newArray = new ArrayList<>();
        for(Integer num : array){
            newArray.add(num);
        }
        newArray.add(i);
        return newArray;
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

    /**
     * Stores the inverse of an mTable entry and the size of the block with respect to the inverse
     *
     * @return A Dataframe with the columns "inv" and "block" for the values of the inverse mTable and blocksize
     */
    public DataFrame computeAuxTMTable() {
        DataFrame table = new DataFrame("inv", "block");
        int lastMSeen = 0;
        int lastPosition = 0;
        for (int position = 1; position < this.mtable.length; position++) {
            if (this.mtable[position] == lastMSeen + 1) {
                lastMSeen += 1;
                table.put(position, position, (position - lastPosition));
                lastPosition = position;
            } else if (this.mtable[position] != lastMSeen) {
                throw new RuntimeException("Inconsistent mtable");
            }
        }
        table.resolveNullEntries();
        return table;
    }
}
