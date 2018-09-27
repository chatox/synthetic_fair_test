package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

public class FailprobCalculator {


    private int[] mtable;
    private DataFrame auxTable;
    private double p;
    private double alpha;
    private ArrayList<ArrayList<Double>> pmfCache;



    public FailprobCalculator(int[] mtable, double p, double alpha){
        this.mtable = mtable;
        this.auxTable = computeAuxTMTable();
        this.p = p;
        this.alpha = alpha;
        pmfCache = new ArrayList<>();
    }



    public double calculateFailProbability(){
        int maxProtected = auxTable.getSumOf("block");
        ArrayList<Integer> blockSizes = auxTable.getColumn("block");
        blockSizes = sublist(blockSizes, 1,blockSizes.size());
//        ArrayList<ArrayList<Integer>> possibilities = ;
//
//
//
//
//
//
//        double successProb = 0;
//        for(ArrayList<Integer> possability : possibilities){
//            double currentTrial = 1;
//            for(int i=0; i<possability.size(); i++){
//                BinomialDistribution binomialDistribution = new BinomialDistribution(blockSizes.get(i), p);
//                currentTrial = currentTrial * binomialDistribution.probability(possability.get(i));
//            }
//            successProb += currentTrial;
//        }

        return 1-findLegalAssignments(maxProtected, blockSizes);
    }

    private boolean blockChecking(ArrayList<Integer> possibilities){
        int assignedSoFar =0;
        for(int i=0; i<possibilities.size(); i++){
            assignedSoFar+= possibilities.get(i);
            if(assignedSoFar<=i){
                return false;
            }
        }
        return true;
    }

    private double findLegalAssignments(int numCandidates, ArrayList<Integer> blockSizes){
        //ArrayList<Integer> prefix = new ArrayList<>();
        return  findLegalAssignmentsAux(1, numCandidates, blockSizes, 1, 0);
    }

    private double binomDistProb(int successes, int trials){
        if(trials<pmfCache.size() && pmfCache.get(trials) != null){
            if(pmfCache.get(trials).size()>successes && pmfCache.get(trials).get(successes) != null){
                return pmfCache.get(trials).get(successes);
            }else{
                BinomialDistribution binomialDistribution = new BinomialDistribution(trials,p);
                ArrayList<Double> currentTrials = new ArrayList<>();
                for(int i=pmfCache.get(trials).size()-1; i<=successes; i++){
                    currentTrials.add(binomialDistribution.probability(i));
                }
                pmfCache.add(trials, currentTrials);
                return pmfCache.get(trials).get(successes);
            }

        }else{
            for(int i=pmfCache.size(); i<=trials; i++){
                BinomialDistribution binomialDistribution = new BinomialDistribution(i,p);
                ArrayList<Double> currentTrials = new ArrayList<>();
                for(int j=0;j<=successes; j++){
                    currentTrials.add(binomialDistribution.probability(j));
                }
                pmfCache.add(currentTrials);
            }
            return pmfCache.get(trials).get(successes);
        }
    }

    private double findLegalAssignmentsAux(double prefix, int numCandidates, ArrayList<Integer> blockSizes,int currentBlockNumber,int candidatesAssignedSoFar) {
        if(blockSizes.size() == 0){
            //ArrayList<ArrayList<Integer>> empty = new ArrayList<>();
            return 0;
        }else{
            //int currentBlockNumber = prefix.size()+1;
            //int candidatesAssignedSoFar= sum(prefix);

            int minNeededThisBlock = currentBlockNumber - candidatesAssignedSoFar;
            if(minNeededThisBlock<0){
                minNeededThisBlock=0;
            }


            int maxPossibleThisBlock = Math.min(blockSizes.get(0), numCandidates);
            double assignments = 0;

            for(int itemsThisBlock = minNeededThisBlock; itemsThisBlock<= maxPossibleThisBlock; itemsThisBlock++){
                int newRemainingCandidates = numCandidates - itemsThisBlock;
                ArrayList<Integer> newRemainingBlockSizes = sublist(blockSizes, 1, blockSizes.size());

                //ArrayList<Integer> newPrefix = new ArrayList<>(prefix);
                //newPrefix.add(itemsThisBlock);
                double newPrefix = prefix * binomDistProb(itemsThisBlock,maxPossibleThisBlock);
                double suffixes = findLegalAssignmentsAux(newPrefix, newRemainingCandidates, newRemainingBlockSizes, currentBlockNumber+1, candidatesAssignedSoFar+itemsThisBlock);

                if(suffixes==1){
                    //ArrayList<Integer> newPossibility = new ArrayList<>(prefix);
                    //newPossibility.add(itemsThisBlock);
                    //assignments.add(newPossibility);
                    double newPossibility = prefix;
                    newPossibility = newPossibility*binomDistProb(itemsThisBlock,maxPossibleThisBlock);
                    assignments= assignments + newPossibility;
                }else{
                    //for(int i=0; i<suffixes.size(); i++){
                        //ArrayList<Integer> newPossibility = new ArrayList<>();

                        //newPossibility.addAll(suffixes.get(i));
                        assignments = assignments + suffixes;
                    //}
                }

            }
            return assignments;

        }
    }

//    private ArrayList<ArrayList<Integer>> findLegalAssignmentsIterative(int maxProtected, ArrayList<Integer> blockSizes){
//
//        ArrayList<ArrayList<Integer>> assignments = new ArrayList<>();
//
//
//        for(int i=blockSizes.size(); i<maxProtected; i++){
//            int candidatesToPlace = i;
//            ArrayList<Integer> currentBlockDistribution = new ArrayList<>();
//
//
//        }
//    }

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

    public DataFrame getAuxTable() {
        return auxTable;
    }

    public int[] getMtable(){
        return this.mtable;
    }
}
