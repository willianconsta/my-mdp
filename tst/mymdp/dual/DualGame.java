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
import mymdp.problem.ImprecisionGenerator;
import mymdp.problem.ImprecisionGeneratorByRanges;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPFileProblemReaderImpl;
import mymdp.problem.MDPImpreciseFileProblemReaderImpl;
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.solver.SolveCaller;
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

    private static final String PROBLEMS_DIR = "precise_problems";
    private static final String SOLUTIONS_DIR = "solutions";

    @Parameters
    public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] { {
		// "navigation01.net" }, {
		// "navigation02.net" }, {
		"navigation03.net" }
		, { "navigation04.net" }
		/*
		 * , { "navigation05.net" }, { "navigation06.net" }, {
		 * "navigation07.net" }, { "navigation08.net" }, {
		 * "navigation09.net" }
		 */
		// {
		// "navigation10.net" }, {
		// "navigation11.net" }, {
		// "navigation12.net" }, }
	});
    }

    private final String filename;

    public DualGame(final String filename) {
	this.filename = filename;
    }

    @Test(timeout = 600000L)
    public void test() {
	final ModifiedPolicyEvaluator policyEvaluator = new ModifiedPolicyEvaluator(100);

	// Reads the MDP's definition from file and turns it to an imprecise
	// problem
	log.info("Current Problem: " + filename);
	final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(0.25);
	final MDPImpreciseFileProblemReaderImpl initialReader = new MDPImpreciseFileProblemReaderImpl(initialProblemImprecisionGenerator);
	final MDPIP initialMdpip = initialReader.readFromFile(PROBLEMS_DIR + "\\" + filename);

	final ImprecisionGenerator imprecisionGenerator = new ImprecisionGeneratorByRanges(initialProblemImprecisionGenerator, 0.1);
	final MDPFileProblemReaderImpl reader = new MDPFileProblemReaderImpl();
	final ProbabilityEvaluator probabilityEvaluator = new MaxProbabilityEvaluator(SOLUTIONS_DIR + "\\initial_" + filename + ".txt");
	SolveCaller.initializeCount();
	final Stopwatch watchInitialGuess = new Stopwatch().start();
	MDP mdp = probabilityEvaluator.evaluate(initialMdpip);
	watchInitialGuess.stop();
	final int numberOfSolverCallsInInitialGuessing = SolveCaller.getNumberOfSolverCalls();

	int i = 1;
	SolveCaller.initializeCount();
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
		result = new PolicyIterationImpl(policyEvaluator).solve(mdp);
		watch1.stop();
		watchMDP.stop();
		log.debug("End of MDP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		result1 = policyEvaluator.policyEvaluation(result, new UtilityFunctionImpl(mdp.getStates()), mdp);
		log.debug(result);
		log.info("MDP: " + result1);
	    }

	    final ImpreciseProblemGenerator generator = new ImpreciseProblemGenerator(result, mdp);
	    generator.writeToFile(SOLUTIONS_DIR + "\\imprecise_problem" + i + ".txt", mdp.getStates().iterator().next(), mdp.getStates());
	    final MDPImpreciseFileProblemReaderImpl reader2 = new MDPImpreciseFileProblemReaderImpl(imprecisionGenerator);
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
	log.info("Number of solver calls in initial guess = " + numberOfSolverCallsInInitialGuessing);
	log.info("Number of solver calls in solving = " + SolveCaller.getNumberOfSolverCalls());

	log.info("Time in Initial Guess = " + watchInitialGuess.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	log.info("Time in MDP = " + watchMDP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	assertTrue(true);

	log.info("End of problem " + filename + "\n\n\n\n\n");
    }
}
