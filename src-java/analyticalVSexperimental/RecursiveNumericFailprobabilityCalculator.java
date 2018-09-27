package analyticalVSexperimental;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;

public class RecursiveNumericFailprobabilityCalculator {




//    private double binomDistProb(int successes, int trials){
//        if(trials<pmfCache.size() && pmfCache.get(trials) != null){
//            if(pmfCache.get(trials).size()>successes && pmfCache.get(trials).get(successes) != null){
//                return pmfCache.get(trials).get(successes);
//            }else{
//                BinomialDistribution binomialDistribution = new BinomialDistribution(trials,p);
//                ArrayList<Double> currentTrials = new ArrayList<>();
//                for(int i=pmfCache.get(trials).size()-1; i<=successes; i++){
//                    currentTrials.add(binomialDistribution.probability(i));
//                }
//                pmfCache.add(trials, currentTrials);
//                return pmfCache.get(trials).get(successes);
//            }
//
//        }else{
//            for(int i=pmfCache.size(); i<=trials; i++){
//                BinomialDistribution binomialDistribution = new BinomialDistribution(i,p);
//                ArrayList<Double> currentTrials = new ArrayList<>();
//                for(int j=0;j<=successes; j++){
//                    currentTrials.add(binomialDistribution.probability(j));
//                }
//                pmfCache.add(currentTrials);
//            }
//            return pmfCache.get(trials).get(successes);
//        }
//    }
}
