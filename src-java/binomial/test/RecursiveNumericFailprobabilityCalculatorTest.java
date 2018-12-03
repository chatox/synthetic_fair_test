package binomial.test;

import binomial.analyticalVSexperimental.RecursiveNumericFailprobabilityCalculator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class RecursiveNumericFailprobabilityCalculatorTest {

    HashMap<RankingCaseParameterKey, Double> failProbMap;
    double precisionBoundary = 0.05;

    @Before
    public void setUp() {
        failProbMap = new HashMap<>();

        failProbMap.put(new RankingCaseParameterKey(6,0.85,0.05), 0.058);

        //Cases where the Mtable is 0 at any position
        failProbMap.put(new RankingCaseParameterKey(10, 0.1, 0.05), 0d);
        failProbMap.put(new RankingCaseParameterKey(10, 0.1, 0.1), 0d);
        failProbMap.put(new RankingCaseParameterKey(10, 0.1, 0.15), 0d);
        failProbMap.put(new RankingCaseParameterKey(10, 0.2, 0.05), 0d);
        failProbMap.put(new RankingCaseParameterKey(10, 0.2, 0.1), 0d);

        //10k Simulation failprobability values with low k,p
        failProbMap.put(new RankingCaseParameterKey(10, 0.2, 0.15), 0.1379);

        failProbMap.put(new RankingCaseParameterKey(10, 0.3, 0.05), 0.0387);
        failProbMap.put(new RankingCaseParameterKey(10, 0.3, 0.1), 0.0796);

        //10k Simulation failprobability values with high k and low p
        failProbMap.put(new RankingCaseParameterKey(100, 0.1, 0.05), 0.1111);
        failProbMap.put(new RankingCaseParameterKey(100, 0.1, 0.1), 0.2272);
        failProbMap.put(new RankingCaseParameterKey(100, 0.1, 0.15), 0.3187);

        failProbMap.put(new RankingCaseParameterKey(100, 0.2, 0.05), 0.1571);

        failProbMap.put(new RankingCaseParameterKey(200, 0.1, 0.05), 0.1699);
    }

//    @Test
//    public void runRecursiveTableFailprobCalculatorWithVeryLowK(){
//        RecursiveNumericFailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(6,0.85,0.05);
//
//        boolean test1 = Math.abs(1-(calculator.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(6,0.85,0.05)))) < precisionBoundary;
//
//        TestCase.assertTrue(test1);
//    }

    @Test
    public void runRecursiveTableFailprobCalculatorWithLowKAndZeroMTables(){
        RecursiveNumericFailprobabilityCalculator calculator1 = new RecursiveNumericFailprobabilityCalculator(10,0.1,0.05);
        RecursiveNumericFailprobabilityCalculator calculator2 = new RecursiveNumericFailprobabilityCalculator(10,0.1,0.1);
        RecursiveNumericFailprobabilityCalculator calculator3 = new RecursiveNumericFailprobabilityCalculator(10,0.1,0.15);
        RecursiveNumericFailprobabilityCalculator calculator4 = new RecursiveNumericFailprobabilityCalculator(10,0.2,0.05);
        RecursiveNumericFailprobabilityCalculator calculator5 = new RecursiveNumericFailprobabilityCalculator(10,0.2,0.1);

//        boolean test1 = (calculator1.calculateFailprobability() == failProbMap.get(new RankingCaseParameterKey(10,0.1,0.05)));
//        boolean test2 = (calculator2.calculateFailprobability() == failProbMap.get(new RankingCaseParameterKey(10,0.1,0.1)));
//        boolean test3 = (calculator3.calculateFailprobability() == failProbMap.get(new RankingCaseParameterKey(10,0.1,0.15)));
//        boolean test4 = (calculator4.calculateFailprobability() == failProbMap.get(new RankingCaseParameterKey(10,0.2,0.05)));
//        boolean test5 = (calculator5.calculateFailprobability() == failProbMap.get(new RankingCaseParameterKey(10,0.2,0.1)));

//        TestCase.assertTrue(test1 && test2 && test3 && test4 && test5);

    }

    @Test
    public void runRecursiveTableFailprobCalculatorWithLowKAndNonZeroMTables(){
        RecursiveNumericFailprobabilityCalculator calculator1 = new RecursiveNumericFailprobabilityCalculator(10,0.2,0.15);
        RecursiveNumericFailprobabilityCalculator calculator2 = new RecursiveNumericFailprobabilityCalculator(10,0.3,0.05);
        RecursiveNumericFailprobabilityCalculator calculator3 = new RecursiveNumericFailprobabilityCalculator(10,0.3,0.1);

//        boolean test1 = Math.abs(1-(calculator1.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(10,0.2,0.15)))) < precisionBoundary;
//        boolean test2 = Math.abs(1-(calculator2.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(10,0.3,0.05)))) < precisionBoundary;
//        boolean test3 = Math.abs(1-(calculator3.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(10,0.3,0.1)))) < precisionBoundary;

//        TestCase.assertTrue(test1 && test2 && test3);
    }


    //TODO Divide binomial.test into smaller parts
    @Test
    public void runRecursiveTableFailprobCalculatorWithHighK(){
        RecursiveNumericFailprobabilityCalculator calculator1 = new RecursiveNumericFailprobabilityCalculator(100,0.1,0.05);
        RecursiveNumericFailprobabilityCalculator calculator2 = new RecursiveNumericFailprobabilityCalculator(100,0.1,0.1);
        RecursiveNumericFailprobabilityCalculator calculator3 = new RecursiveNumericFailprobabilityCalculator(100,0.1,0.15);
        RecursiveNumericFailprobabilityCalculator calculator4 = new RecursiveNumericFailprobabilityCalculator(100,0.2,0.05);
        RecursiveNumericFailprobabilityCalculator calculator5 = new RecursiveNumericFailprobabilityCalculator(200,0.2,0.1);

//        boolean test1 = Math.abs(1-(calculator1.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(100,0.1,0.05)))) < precisionBoundary;
//        boolean test2 = Math.abs(1-(calculator2.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(100,0.1,0.1)))) < precisionBoundary;
//        boolean test3 = Math.abs(1-(calculator3.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(100,0.1,0.15)))) < precisionBoundary;
//        boolean test4 = Math.abs(1-(calculator4.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(100,0.2,0.05)))) < precisionBoundary;
//        boolean test5 = Math.abs(1-(calculator5.calculateFailprobability() / failProbMap.get(new RankingCaseParameterKey(200,0.2,0.1)))) < precisionBoundary;
//
//        TestCase.assertTrue(test1 && test2 && test3 && test4 && test5);
    }
}
