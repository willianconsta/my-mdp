package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.PolicyImpl;
import mymdp.core.SolutionReport;
import mymdp.core.State;
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

public class DualGame
	implements
		ProblemSolver
{
	private static final Logger log = LogManager.getLogger(DualGame.class);

	private static final String PROBLEMS_DIR = ".\\precise_problems";
	private static final String SOLUTIONS_DIR = ".\\solutions";

	private final String filename;
	private final double maxRelaxation;
	private final double stepRelaxation;
	private final double maxError;

	public DualGame(final String filename, final double maxRelaxation) {
		this(filename, maxRelaxation, maxRelaxation, 0.001);
	}

	public DualGame(final String filename, final double maxRelaxation, final double stepRelaxation,
			final double maxError) {
		this.filename = filename;
		this.maxRelaxation = stepRelaxation;
		this.stepRelaxation = stepRelaxation;
		this.maxError = maxError;
	}

	@Override
	public SolutionReport solve() {
		ProbLinearSolver.setMinimizing();
		ProbLinearSolver.initializeCount();
		final ModifiedPolicyEvaluator policyEvaluator = new ModifiedPolicyEvaluator(400);

		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: " + filename);
		final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP initialMdpip = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				initialProblemImprecisionGenerator);

		final ImprecisionGenerator imprecisionGenerator = new ImprecisionGeneratorByRanges(
				initialProblemImprecisionGenerator, stepRelaxation);
		final ProbabilityEvaluator probabilityEvaluator = ProbabilityEvaluatorFactory
				.getAnyFeasibleInstance(SOLUTIONS_DIR + "\\initial_" + filename + ".txt");
		// log.info("Initial problem is " + initialMdpip.toString());
		ProbLinearSolver.initializeCount();
		final Stopwatch watchInitialGuess = Stopwatch.createStarted();
		MDP mdp = probabilityEvaluator.evaluate(initialMdpip);
		watchInitialGuess.stop();
		final int numberOfSolverCallsInInitialGuessing = ProbLinearSolver.getNumberOfSolverCalls();

		int numberOfSolverCallsInImprovement = 0;
		int numberOfSolverCallsInEvaluation = 0;

		int i = 1;
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDP = Stopwatch.createUnstarted();
		final Stopwatch watchMDPIP = Stopwatch.createUnstarted();
		final Stopwatch watchAll = Stopwatch.createStarted();

		Policy result = new PolicyImpl(mdp);
		UtilityFunction result1;
		UtilityFunctionWithProbImpl result2 = new UtilityFunctionWithProbImpl(mdp.getStates(), -10.0);
		while ( true ) {
			log.debug("Iteration " + i);
			{
				// log.debug("Starting MDP");
				watchMDP.start();
				final Stopwatch watch1 = Stopwatch.createStarted();
				final int numSolverCalls = ProbLinearSolver.getNumberOfSolverCalls();
				result = new PolicyIterationImpl(policyEvaluator).solve(mdp, result, result2);
				watch1.stop();
				watchMDP.stop();
				// log.info("End of MDP: " +
				// watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
				result1 = policyEvaluator.policyEvaluation(result, new UtilityFunctionImpl(mdp.getStates()), mdp);
				// checkState(result1.compareTo(result2) >= 0);
				// log.info(result);
				numberOfSolverCallsInImprovement += ProbLinearSolver.getNumberOfSolverCalls() - numSolverCalls;
				log.info("Values after improve: " + result1);
			}

			final ImpreciseProblemGenerator generator = new ImpreciseProblemGenerator(result, mdp);
			generator.writeToFile(SOLUTIONS_DIR + "\\problem_for_evaluation_" + i + ".txt",
					mdp.getStates().iterator().next(), mdp.getStates());
			final MDPIP mdpip = MDPImpreciseFileProblemReader
					.readFromFile(SOLUTIONS_DIR + "\\problem_for_evaluation_" + i + ".txt", imprecisionGenerator);

			{
				// log.debug("Starting MDPIP");
				watchMDPIP.start();
				final Stopwatch watch1 = Stopwatch.createStarted();
				final int numSolverCalls = ProbLinearSolver.getNumberOfSolverCalls();
				result2 = evaluate(result1, mdpip, result);
				watch1.stop();
				watchMDPIP.stop();
				// log.info("End of MDPIP: " +
				// watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
				// log.info("MDPIP: " + result2);
				numberOfSolverCallsInEvaluation += ProbLinearSolver.getNumberOfSolverCalls() - numSolverCalls;
				log.info("Values after evaluation: " + result2);
			}

			final double actualError = UtilityFunctionDistanceEvaluator.distanceBetween(result1, result2);
			log.debug("Error between two MDPs = " + actualError);
			if ( actualError < 0.01 ) {
				log.info("Values: " + result2);
				watchAll.stop();
				break;
			}

			mdp = new DelegateMDP(initialMdpip);
			( (DelegateMDP) mdp ).setFunction(result2);
			i++;
		}
		log.info("Summary:");
		log.info("Number of iterations = " + i);
		log.info("Number of solver calls in initial guess = " + numberOfSolverCallsInInitialGuessing);
		log.info("Number of solver calls in solving = " + ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Number of solver calls in evaluation = " + numberOfSolverCallsInEvaluation);
		log.info("Number of solver calls in improvement = " + numberOfSolverCallsInImprovement);

		log.info("Time in Initial Guess = " + watchInitialGuess.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in MDP = " + watchMDP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		assertTrue(true);

		log.info("End of problem " + filename + "\n\n\n\n\n");
		return new SolutionReport(result, result2);
	}

	private UtilityFunctionWithProbImpl evaluate(final UtilityFunction baseValue, final MDPIP mdpip,
			final Policy policy) {
		// evaluation
		log.debug("Evaluating policy...");
		UtilityFunction value = new UtilityFunctionWithProbImpl(baseValue);
		ProbLinearSolver.setFeasibilityOnly();
		final ProbabilityEvaluator evaluator = ProbabilityEvaluatorFactory
				.getAnyFeasibleInstance(SOLUTIONS_DIR + "\\evaluating_dual" + ".txt");
		value = new ModifiedPolicyEvaluator(1000).policyEvaluation(policy, value, evaluator.evaluate(mdpip));
		UtilityFunction newValue = new UtilityFunctionWithProbImpl(value);
		ProbLinearSolver.setMinimizing();
		do {
			value = newValue;
			newValue = new UtilityFunctionWithProbImpl(value);
			for ( final State s : mdpip.getStates() ) {
				newValue.updateUtility(s, calculate(mdpip, s, policy.getActionFor(s), value));
			}
		} while ( UtilityFunctionDistanceEvaluator.distanceBetween(value, newValue) > maxError );
		return new UtilityFunctionWithProbImpl(value);
	}

	private double calculate(final MDPIP mdpip, final State s, final Action a, final UtilityFunction value) {
		double utilityOfAction = 0;
		for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, value) ) {
			utilityOfAction += nextStateAndProb.getValue().doubleValue() * value.getUtility(nextStateAndProb.getKey());
		}
		return mdpip.getRewardFor(s) + mdpip.getDiscountFactor() * utilityOfAction;
	}
}
