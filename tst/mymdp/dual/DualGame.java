package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.MDPFileProblemReaderImpl;
import mymdp.problem.MDPIPFileProblemReaderImpl;
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.util.UtilityFunctionDistanceEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Stopwatch;

@RunWith(Parameterized.class)
public class DualGame {
    private static final Logger log = LogManager.getLogger(DualGame.class);

    private static final String PROBLEMS_DIR = "problems";
    private static final String SOLUTIONS_DIR = "solutions";

    @Parameters
    public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] { {
		"navigation01.net" }, {
		"navigation02.net" }, {
		"navigation03.net" }, {
		"navigation04.net" }, {
		"navigation05.net" }, {
		"navigation06.net" }, {
		"navigation07.net" }, {
		"navigation08.net" }, {
		"navigation09.net" }, {
		"navigation10.net" }, {
		"navigation11.net" }, {
		"navigation12.net" }, {
		} });
    }

    private final String filename;

    public DualGame(final String filename) {
	this.filename = filename;
    }

    @Test(timeout = 600000L)
    public void test() {
	log.info("Current Problem: " + filename);
	final MDPFileProblemReaderImpl reader = new MDPFileProblemReaderImpl();
	MDP mdp = reader.readFromFile(PROBLEMS_DIR + "\\" + filename);
	final ModifiedPolicyEvaluator evaluator = new ModifiedPolicyEvaluator(100);
	int i = 1;
	final Stopwatch watchMDP = new Stopwatch();
	final Stopwatch watchMDPIP = new Stopwatch();
	final Stopwatch watchAll = new Stopwatch().start();
	while (true) {
	    log.debug("Iteration " + i);
	    final Policy result;
	    final UtilityFunction result1;
	    {
		log.debug("Starting MDP");
		watchMDP.start();
		final Stopwatch watch1 = new Stopwatch().start();
		result = new PolicyIterationImpl(evaluator).solve(mdp);
		watch1.stop();
		watchMDP.stop();
		log.debug("End of MDP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		result1 = evaluator.policyEvaluation(result, new UtilityFunctionImpl(mdp.getStates()), mdp);
		log.debug(result);
		log.info("MDP: " + result1);
	    }

	    final ImpreciseProblemGenerator generator = new ImpreciseProblemGenerator(result, mdp);
	    generator.writeToFile(SOLUTIONS_DIR + "\\imprecise_problem" + i + ".txt", mdp.getStates().iterator().next(), mdp.getStates());
	    final MDPIPFileProblemReaderImpl reader2 = new MDPIPFileProblemReaderImpl(0.1);
	    final MDPIP mdpip = reader2.readFromFile(SOLUTIONS_DIR + "\\imprecise_problem" + i + ".txt");

	    final UtilityFunctionWithProbImpl result2;
	    {
		log.debug("Starting MDPIP");
		watchMDPIP.start();
		final Stopwatch watch1 = new Stopwatch().start();
		result2 = (UtilityFunctionWithProbImpl) new ValueIterationProbImpl(result1).solve(mdpip, 0.001);
		watch1.stop();
		watchMDPIP.stop();
		log.debug("End of MDPIP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		log.info("MDPIP: " + result2);
	    }

	    final double actualError = UtilityFunctionDistanceEvaluator.distanceBetween(result1, result2);
	    log.debug("Error between two MDPs = " + actualError);
	    if (actualError < 0.01) {
		watchAll.stop();
		break;
	    }

	    final PreciseProblemGenerator generator2 = new PreciseProblemGenerator(result2, mdpip);
	    generator2
		    .writeToFile(SOLUTIONS_DIR + "\\precise_problem" + i + ".txt", mdpip.getStates().iterator().next(), mdpip.getStates());

	    mdp = reader.readFromFile(SOLUTIONS_DIR + "\\precise_problem" + i + ".txt");
	    i++;
	}
	log.info("Summary:");
	log.info("Number of iterations = " + i);
	log.info("Time in MDP = " + watchMDP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	assertTrue(true);

	log.info("End of problem " + filename + "\n\n\n\n\n");
    }
}
