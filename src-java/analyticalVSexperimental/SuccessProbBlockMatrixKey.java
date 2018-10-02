package analyticalVSexperimental;

public class SuccessProbBlockMatrixKey {

    private final int blocknumber;
    private final int trialNumber;
    private final int candidatesThisBlock;

    public SuccessProbBlockMatrixKey(int blocknumber, int trialNumber, int candidatesThisBlock){
        this.blocknumber = blocknumber;
        this.trialNumber = trialNumber;
        this.candidatesThisBlock = candidatesThisBlock;
    }

    @Override
    public boolean equals(final Object o){
        if(!(o instanceof SuccessProbBlockMatrixKey)) return false;
        if(((SuccessProbBlockMatrixKey) o).blocknumber != blocknumber) return false;
        if(((SuccessProbBlockMatrixKey) o).candidatesThisBlock != candidatesThisBlock) return false;
        if(((SuccessProbBlockMatrixKey) o).trialNumber != trialNumber) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return (candidatesThisBlock << 16) + trialNumber+blocknumber;

    }

    public int getBlocknumber() {
        return blocknumber;
    }

    public int getTrialNumber() {
        return trialNumber;
    }

    public int getCandidatesThisBlock() {
        return candidatesThisBlock;
    }
}
