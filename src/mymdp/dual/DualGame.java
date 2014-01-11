package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.dual.evaluator.ProbabilityEvaluator;
import mymdp.dual.evaluator.ProbabilityEvaluatorFactory;
import mymdp.problem.ImprecisionGenerator;
import mymdp.problem.ImprecisionGeneratorByRanges;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.solver.ProbLinearSolver;
import mymdp.util.UtilityFunctionDistanceEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

public class DualGame implements ProblemSolver {
	private static final Logger log = LogManager.getLogger(DualGame.class);

	private static final String PROBLEMS_DIR = "precise_problems";
	private static final String SOLUTIONS_DIR = "solutions";

	private final String filename;
	private final double maxRelaxation;
	private final double stepRelaxation;
	private final double maxError;

	public DualGame(final String filename, final double maxRelaxation) {
		this(filename, maxRelaxation, maxRelaxation, 0.001);
	}

	public DualGame(final String filename, final double maxRelaxation,
			final double stepRelaxation, final double maxError) {
		this.filename = filename;
		this.maxRelaxation = stepRelaxation;
		this.stepRelaxation = stepRelaxation;
		this.maxError = maxError;
	}

	@Override
	public SolutionReport solve() {
		ProbLinearSolver.initializeCount();
		final ModifiedPolicyEvaluator policyEvaluator = new ModifiedPolicyEvaluator(100);

		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: " + filename);
		final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP initialMdpip = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				initialProblemImprecisionGenerator);

		final ImprecisionGenerator imprecisionGenerator = new ImprecisionGeneratorByRanges(initialProblemImprecisionGenerator,
				stepRelaxation);
		final ProbabilityEvaluator probabilityEvaluator = ProbabilityEvaluatorFactory
				.getAnyFeasibleInstance(SOLUTIONS_DIR + "\\initial_" + filename + ".txt");
		// log.info("Initial problem is " + initialMdpip.toString());
		ProbLinearSolver.initializeCount();
		final Stopwatch watchInitialGuess = new Stopwatch().start();
		MDP mdp = probabilityEvaluator.evaluate(initialMdpip);
		watchInitialGuess.stop();
		final int numberOfSolverCallsInInitialGuessing = ProbLinearSolver.getNumberOfSolverCalls();

		int i = 1;
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDP = new Stopwatch();
		final Stopwatch watchMDPIP = new Stopwatch();
		final Stopwatch watchAll = new Stopwatch().start();

		Policy result;
		UtilityFunction result1;
		UtilityFunctionWithProbImpl result2;
		while (true) {
			log.debug("Iteration " + i);
			{
				log.debug("Starting MDP");
				watchMDP.start();
				final Stopwatch watch1 = new Stopwatch().start();
				result = new PolicyIterationImpl(policyEvaluator).solve(mdp);
				watch1.stop();
				watchMDP.stop();
				log.info("End of MDP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
				result1 = policyEvaluator.policyEvaluation(result, new UtilityFunctionImpl(mdp.getStates()), mdp);
				log.info(result);
				log.info("MDP: " + result1);
			}

			final ImpreciseProblemGenerator generator = new ImpreciseProblemGenerator(result, mdp);
			generator.writeToFile(SOLUTIONS_DIR + "\\problem_for_evaluation_" + i + ".txt", mdp.getStates().iterator().next(),
					mdp.getStates());
			final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile(SOLUTIONS_DIR + "\\problem_for_evaluation_" + i + ".txt",
					imprecisionGenerator);

			{
				log.debug("Starting MDPIP");
				watchMDPIP.start();
				final Stopwatch watch1 = new Stopwatch().start();
				result2 = (UtilityFunctionWithProbImpl) new ValueIterationProbImpl(result1).solve(mdpip, maxError);
				watch1.stop();
				watchMDPIP.stop();
				log.info("End of MDPIP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
				log.info("MDPIP: " + result2);
			}

			final double actualError = UtilityFunctionDistanceEvaluator.distanceBetween(result1, result2);
			log.debug("Error between two MDPs = " + actualError);
			if (actualError < 0.01) {
				watchAll.stop();
				break;
			}

			mdp = new DelegateMDP(initialMdpip);
			((DelegateMDP) mdp).setFunction(result2);
			i++;
		}
		log.info("Summary:");
		log.info("Number of iterations = " + i);
		log.info("Number of solver calls in initial guess = " + numberOfSolverCallsInInitialGuessing);
		log.info("Number of solver calls in solving = " + ProbLinearSolver.getNumberOfSolverCalls());

		log.info("Time in Initial Guess = " + watchInitialGuess.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in MDP = " + watchMDP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		assertTrue(true);

		log.info("End of problem " + filename + "\n\n\n\n\n");
		return new SolutionReport(result, result2);
	}
}
