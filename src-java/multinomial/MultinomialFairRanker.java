package multinomial;

import multinomial.util.MultinomialMTableFailProbPair;
import org.apache.commons.math3.analysis.function.Abs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MultinomialFairRanker {

    private int k;
    private double[] p;
    private double alpha;

    public MultinomialFairRanker(int k, double[] p, double alpha) {
        this.k = k;
        this.p = p;
        this.alpha = alpha;
    }

    public ArrayList<Candidate> readCompasData() throws IOException {
        Path currentDir = Paths.get(".");
        String currentDirString = currentDir.toAbsolutePath().toString();
        BufferedReader readerRace = new BufferedReader(new FileReader(currentDirString+"/data/COMPAS/ProPublica_race.csv"));
        BufferedReader readerGender = new BufferedReader(new FileReader(currentDirString+"/data/COMPAS/ProPublica_sex.csv"));
        String raceLine = readerRace.readLine();
        String genderLine = readerGender.readLine();
        raceLine = readerRace.readLine();
        genderLine = readerGender.readLine();
        ArrayList<Candidate> candidates = new ArrayList<>();
        int id = 0;
        while (raceLine != null) {
            String[] raceArr = raceLine.split(",");
            String[] genderArr = genderLine.split(",");
            double score = Double.parseDouble(raceArr[2]);
            boolean race = Integer.parseInt(raceArr[3]) == 1;
            boolean gender = Integer.parseInt(genderArr[3]) == 1;
            int group = 0;
            if (race && gender) {
                group = 1;
            }
            if (race && !gender) {
                group = 2;
            }
            if (!race && gender) {
                group = 3;
            }
            candidates.add(new Candidate(score, group, id));
            id++;
            raceLine = readerRace.readLine();
            genderLine = readerGender.readLine();
        }
        return candidates;
    }

    public ArrayList<Candidate> createMultinomialFairRankingCOMPAS(MultinomialMTableFailProbPair pair) throws IOException {
        ArrayList<Candidate> ranking = new ArrayList<>();
        HashMap<Integer, ArrayList<int[]>> mtable = pair.getMtable();
        ArrayList<int[]> mirrors = pair.getMirrors();
        ArrayList<Candidate> candidates = readCompasData();
        ArrayList<Candidate> group0 = new ArrayList<>();
        ArrayList<Candidate> group1 = new ArrayList<>();
        ArrayList<Candidate> group2 = new ArrayList<>();
        ArrayList<Candidate> group3 = new ArrayList<>();
        for (Candidate candidate : candidates) {
            if (candidate.getGroup() == 0) {
                group0.add(candidate);
            }
            if (candidate.getGroup() == 1) {
                group1.add(candidate);
            }
            if (candidate.getGroup() == 2) {
                group2.add(candidate);
            }
            if (candidate.getGroup() == 3) {
                group3.add(candidate);
            }
        }
        group0.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                if(o1.getScore()<o2.getScore())
                    return 1;
                if(o1.getScore()>o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });
        group1.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                if(o1.getScore()<o2.getScore())
                    return 1;
                if(o1.getScore()>o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });
        group2.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                if(o1.getScore()<o2.getScore())
                    return 1;
                if(o1.getScore()>o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });
        group3.sort(new Comparator<Candidate>() {
            @Override
            public int compare(Candidate o1, Candidate o2) {
                if(o1.getScore()<o2.getScore())
                    return 1;
                if(o1.getScore()>o2.getScore())
                    return -1;
                else
                    return 0;
            }
        });
        HashMap<Integer, ArrayList<Candidate>> groups = new HashMap<>();
        groups.put(0, group0);
        groups.put(1, group1);
        groups.put(2, group2);
        groups.put(3, group3);
        int[] candidatesRankedSoFar = new int[4];
        ArrayList<int[]> currentLevel = mtable.get(0);
        int[] currentPath = currentLevel.get(0);
        Random random = new Random();

        for (int i = 1; i <= k; i++) {
            currentLevel = getCurrentLevelCandidates(mtable, mirrors, currentPath, i);
            if (currentLevel.size() > 1) {
                int pathNumber = random.nextInt(currentLevel.size()); // THIS LINE change to most likely path
                currentPath = currentLevel.get(pathNumber);
            } else {
                currentPath = currentLevel.get(0);
            }
            int candidateNeeded = getCandidateNeeded(candidatesRankedSoFar, currentPath);
            if (candidateNeeded == 1 && group1.size() > 0) {
                Candidate currentBest = group1.get(0);
                ranking.add(currentBest);
                groups.get(currentBest.getGroup()).remove(0);
                candidatesRankedSoFar[currentBest.getGroup()]++;
            } else if (candidateNeeded == 2 && group2.size() > 0) {
                Candidate currentBest = group2.get(0);
                ranking.add(currentBest);
                groups.get(currentBest.getGroup()).remove(0);
                candidatesRankedSoFar[currentBest.getGroup()]++;
            } else if (candidateNeeded == 3 && group3.size() > 0) {
                Candidate currentBest = group3.get(0);
                ranking.add(currentBest);
                groups.get(currentBest.getGroup()).remove(0);
                candidatesRankedSoFar[currentBest.getGroup()]++;
            }
            //No protected candidate needed => get best next candidate
            else {
                ArrayList<Candidate> next = new ArrayList<>();
                if (group0.size() > 0)
                    next.add(group0.get(0));
                if (group1.size() > 0)
                    next.add(group1.get(0));
                if (group2.size() > 0)
                    next.add(group2.get(0));
                if (group3.size() > 0)
                    next.add(group3.get(0));
                double maxScore = 0;
                Candidate currentBest = null;
                for (int j = 0; j < next.size(); j++) {
                    if (next.get(j).getScore() >= maxScore) {
                        currentBest = next.get(j);
                        maxScore = currentBest.getScore();
                    }
                }
                groups.get(currentBest.getGroup()).remove(0);
                ranking.add(currentBest);
                candidatesRankedSoFar[currentBest.getGroup()]++;
                if(currentBest.getGroup()!=0){
                    candidatesRankedSoFar[0]++;
                }
            }
        }
        return ranking;
    }

    public ArrayList<ArrayList<Candidate>> createBothRankings(int iterations, int stepsize) throws IOException {
        MultinomialMTableFailProbPair pair = MultinomialSimulator.regressionAlphaAdjustment(k, p, alpha, iterations,stepsize);
        ArrayList<Candidate> fairRanking = createMultinomialFairRankingCOMPAS(pair);
        ArrayList<Candidate> scoreRanking = createScoreRanking();

        ArrayList<ArrayList<Candidate>> compareRankings = new ArrayList<>();
        compareRankings.add(fairRanking);
        compareRankings.add(scoreRanking);

        return compareRankings;
    }

    private ArrayList<Candidate> createScoreRanking() throws IOException {
        ArrayList<Candidate> candidates = readCompasData();
        candidates.sort(Comparator.comparing(Candidate::getScore));
        ArrayList<Candidate> ranking = new ArrayList<>();
        for(int i=1; i<=k; i++){
            ranking.add(candidates.get(candidates.size()-1));
            candidates.remove(candidates.size()-1);
        }
        return ranking;
    }

    private int getCandidateNeeded(int[] candidatesRankedSoFar, int[] currentPath) {
        for (int i = 1; i < currentPath.length; i++) {
            if (currentPath[i] > candidatesRankedSoFar[i]) {
                return i;
            }
        }
        return 0;
    }

    private ArrayList<int[]> getCurrentLevelCandidates(HashMap<Integer, ArrayList<int[]>> mtable, ArrayList<
            int[]> mirrors, int[] currentPath, int i) {
        ArrayList<int[]> currentLevelCandidates = mtable.get(i);
//        System.out.println(currentLevelCandidates.size());
        if (mirrors != null && mirrors.size() > 0) {
            for (int[] candidate : currentLevelCandidates) {
                if (mirrors.contains(candidate)) {
                    currentLevelCandidates.add(getMirror(candidate));
                }
            }
        }

        ArrayList<int[]> validCandidates = new ArrayList<>();
        for (int[] candidate : currentLevelCandidates) {
            if (possibleCandidate(currentPath, candidate)) {
                validCandidates.add(candidate);
            }
        }

        return validCandidates;
    }

    private int[] getMirror(int[] candidate) {
        int[] mirror = new int[candidate.length];
        mirror[0] = candidate[0];
        for (int i = 1; i < mirror.length; i++) {
            mirror[i] = candidate[candidate.length - i];
        }
        return mirror;
    }

    private boolean possibleCandidate(int[] current, int[] candidate) {
        boolean possible = false;
        for (int i = 0; i < current.length; i++) {
            if (candidate[i] - current[i] == 1) {
                possible = true;
            } else if (possible && Math.abs(candidate[i] - current[i]) == 1) {
                return false;
            }
        }
        return possible;
    }

    public int getK() {
        return k;
    }

    public double[] getP() {
        return p;
    }

    public double getAlpha() {
        return alpha;
    }
}
