package test;

import analyticalVSexperimental.MTableGenerator;
import analyticalVSexperimental.MTableMultiTester;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;


public class MTableMultiTesterTest {

    HashMap<RankingCaseParameterKey, Double> failProbMap;
    double precisionBoundary = 0.05;

    @Before
    public void setUp() {
        failProbMap = new HashMap<>();

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

    @Test
    public void runIterativeMTableMultiTesterWithLowKValuesAndZeroMTableTest() {
        MTableGenerator gen1 = new MTableGenerator(10, 0.1, 0.05);
        MTableGenerator gen2 = new MTableGenerator(10, 0.1, 0.1);
        MTableGenerator gen3 = new MTableGenerator(10, 0.1, 0.15);
        MTableGenerator gen4 = new MTableGenerator(10, 0.2, 0.05);
        MTableGenerator gen5 = new MTableGenerator(10, 0.2, 0.1);

        MTableMultiTester test1 = new MTableMultiTester(10, 0.1, 0.05);
        MTableMultiTester test2 = new MTableMultiTester(10, 0.1, 0.1);
        MTableMultiTester test3 = new MTableMultiTester(10, 0.1, 0.15);
        MTableMultiTester test4 = new MTableMultiTester(10, 0.2, 0.05);
        MTableMultiTester test5 = new MTableMultiTester(10, 0.2, 0.1);

        boolean testRun1 = test1.computeFailureProbability(gen1.getMTable()) == failProbMap.get(new RankingCaseParameterKey(10, 0.1, 0.05));
        boolean testRun2 = test2.computeFailureProbability(gen2.getMTable()) == failProbMap.get(new RankingCaseParameterKey(10, 0.1, 0.1));
        boolean testRun3 = test3.computeFailureProbability(gen3.getMTable()) == failProbMap.get(new RankingCaseParameterKey(10, 0.1, 0.15));
        boolean testRun4 = test4.computeFailureProbability(gen4.getMTable()) == failProbMap.get(new RankingCaseParameterKey(10, 0.2, 0.05));
        boolean testRun5 = test5.computeFailureProbability(gen5.getMTable()) == failProbMap.get(new RankingCaseParameterKey(10, 0.2, 0.1));

        TestCase.assertTrue(testRun1 && testRun2 && testRun3 && testRun4 && testRun5);
    }

    @Test
    public void runIterativeMTableMultiTesterWithLowKValuesAndNonZeroMTableTest() {
        MTableGenerator gen1 = new MTableGenerator(10, 0.2, 0.15);
        MTableGenerator gen2 = new MTableGenerator(10, 0.3, 0.05);
        MTableGenerator gen3 = new MTableGenerator(10, 0.3, 0.1);

        MTableMultiTester test1 = new MTableMultiTester(10, 0.2, 0.15);
        MTableMultiTester test2 = new MTableMultiTester(10, 0.3, 0.05);
        MTableMultiTester test3 = new MTableMultiTester(10, 0.3, 0.1);

        boolean testRun1 = Math.abs(1 - (test1.computeFailureProbability(gen1.getMTable()) / failProbMap.get(new RankingCaseParameterKey(10, 0.2, 0.15)))) < precisionBoundary;
        boolean testRun2 = Math.abs(1 - (test2.computeFailureProbability(gen2.getMTable()) / failProbMap.get(new RankingCaseParameterKey(10, 0.3, 0.05)))) < precisionBoundary;
        boolean testRun3 = Math.abs(1 - (test3.computeFailureProbability(gen3.getMTable()) / failProbMap.get(new RankingCaseParameterKey(10, 0.3, 0.1)))) < precisionBoundary;

        TestCase.assertTrue(testRun1 && testRun2 && testRun3);
    }

    @Test
    public void runIterativeMTableMultiTesterWithHighKValuesAndLowPValues() {
        MTableGenerator gen1 = new MTableGenerator(100, 0.1, 0.05);
        MTableGenerator gen2 = new MTableGenerator(100, 0.1, 0.1);
        MTableGenerator gen3 = new MTableGenerator(100, 0.1, 0.15);
        MTableGenerator gen4 = new MTableGenerator(100, 0.2, 0.05);
        MTableGenerator gen5 = new MTableGenerator(200, 0.1, 0.05);

        MTableMultiTester test1 = new MTableMultiTester(100, 0.1, 0.05);
        MTableMultiTester test2 = new MTableMultiTester(100, 0.1, 0.1);
        MTableMultiTester test3 = new MTableMultiTester(100, 0.1, 0.15);
        MTableMultiTester test4 = new MTableMultiTester(100, 0.2, 0.05);
        MTableMultiTester test5 = new MTableMultiTester(200, 0.1, 0.05);

        boolean testRun1 = Math.abs(1 - (test1.computeFailureProbability(gen1.getMTable()) / failProbMap.get(new RankingCaseParameterKey(100, 0.1, 0.05)))) < precisionBoundary;
        boolean testRun2 = Math.abs(1 - (test2.computeFailureProbability(gen2.getMTable()) / failProbMap.get(new RankingCaseParameterKey(100, 0.1, 0.1)))) < precisionBoundary;
        boolean testRun3 = Math.abs(1 - (test3.computeFailureProbability(gen3.getMTable()) / failProbMap.get(new RankingCaseParameterKey(100, 0.1, 0.15)))) < precisionBoundary;
        boolean testRun4 = Math.abs(1 - (test4.computeFailureProbability(gen4.getMTable()) / failProbMap.get(new RankingCaseParameterKey(100, 0.2, 0.05)))) < precisionBoundary;
        boolean testRun5 = Math.abs(1 - (test5.computeFailureProbability(gen5.getMTable()) / failProbMap.get(new RankingCaseParameterKey(200, 0.1, 0.05)))) < precisionBoundary;

        TestCase.assertTrue(testRun1 && testRun2 && testRun3 && testRun4 && testRun5);
    }
}
