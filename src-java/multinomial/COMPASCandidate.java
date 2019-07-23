package multinomial;

public class COMPASCandidate {

    private double score;
    private int group;
    private int id;

    public COMPASCandidate(double score, int group, int id) {
        this.score = score;
        this.group = group;
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public int getGroup() {
        return group;
    }

    public int getId() {
        return id;
    }
}
