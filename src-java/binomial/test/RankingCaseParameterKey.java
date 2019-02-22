package binomial.test;

public class RankingCaseParameterKey {

    private final int k;
    private final double p;
    private final double alpha;

    public RankingCaseParameterKey(int k, double p, double alpha){
        this.k = k;
        this.p = p;
        this.alpha = alpha;
    }

    @Override
    public boolean equals(final Object o){
        if(!(o instanceof RankingCaseParameterKey)) return false;
        if(((RankingCaseParameterKey) o).k != k) return false;
        if(((RankingCaseParameterKey) o).p != p) return false;
        if(((RankingCaseParameterKey) o).alpha != alpha) return false;
        return true;
    }

    @Override
    public int hashCode(){
        return (k << 16) + Double.valueOf(alpha+p).hashCode();

    }
}
