package multinomial;

public class Candidate {

    private double score;
    private int group;
    private int id;

    public Candidate(double score, int group, int id) {
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
