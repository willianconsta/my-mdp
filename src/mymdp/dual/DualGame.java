package mymdp.dual;

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
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.solver.ProbLinearSolver;
import mymdp.util.Pair;
import mymdp.util.UtilityFunctionDistanceEvaluator;

public class DualGame
	implements
		ProblemSolver<MDPIP,ImprecisionGenerator>
{
	private static final Logger log = LogManager.getLogger(DualGame.class);

	private final double maxError;

	private final Stopwatch watchInitialGuess = Stopwatch.createUnstarted();
	private final Stopwatch watchMDP = Stopwatch.createUnstarted();
	private final Stopwatch watchMDPIP = Stopwatch.createUnstarted();
	private final Stopwatch watchAll = Stopwatch.createUnstarted();

	public DualGame(final double maxError) {
		this.maxError = maxError;
	}

	@Override
	public SolutionReport solve(final Problem<MDPIP,ImprecisionGenerator> problem) {
		watchInitialGuess.reset();
		watchMDP.reset();
		watchMDPIP.reset();
		watchAll.reset().start();

		ProbLinearSolver.setMinimizing();
		ProbLinearSolver.initializeCount();

		final MDPIP initialMdpip = problem.getModel();

		MDP mdp = guessFirstProbability(initialMdpip);

		int i = 1;

		Policy currentPolicy = new PolicyImpl(mdp);
		UtilityFunction currentValue;
		UtilityFunctionWithProbImpl evaluatedValue = new UtilityFunctionWithProbImpl(mdp.getStates(), -10.0);
		while ( true ) {
			log.debug("Iteration {}", i);

			final Pair<Policy,UtilityFunction> improvements = improvePolicy(mdp, currentPolicy, evaluatedValue);
			currentPolicy = improvements.getFirst();
			currentValue = improvements.getSecond();

			final MDPIP mdpip = new DelegateMDPIP(mdp, currentPolicy, problem.getComplement());
			log.info("MDPIP is {}", mdpip);

			evaluatedValue = evaluate(currentValue, mdpip, currentPolicy);
			log.info("Values after evaluation: {}", evaluatedValue);

			final double actualError = UtilityFunctionDistanceEvaluator.distanceBetween(currentValue, evaluatedValue);
			log.debug("Error between two MDPs = {}", actualError);
			if ( actualError < maxError ) {
				log.info("Values: {}", evaluatedValue);
				watchAll.stop();
				break;
			}

			mdp = completeNaturesPolicy(initialMdpip, evaluatedValue);
			log.info("MDP is {}", mdp);
			i++;
		}
		printSummary(i);
		return new SolutionReport(currentPolicy, evaluatedValue);
	}

	private MDP guessFirstProbability(final MDPIP initialMdpip) {
		ProbLinearSolver.getCounter().startCounting("InitialGuess");
		watchInitialGuess.start();
		final ProbabilityEvaluator probabilityEvaluator = ProbabilityEvaluatorFactory.getAnyFeasibleInstance();
		final MDP mdp = probabilityEvaluator.evaluate(initialMdpip, new UtilityFunctionImpl(initialMdpip.getStates()));
		watchInitialGuess.stop();
		ProbLinearSolver.getCounter().stopCounting("InitialGuess");
		return mdp;
	}

	private static Pair<Policy,UtilityFunction> improvePolicy(final MDP mdp, final Policy currentPolicy, final UtilityFunction currentValue) {
		final Stopwatch watchMDP = Stopwatch.createUnstarted();
		final ModifiedPolicyEvaluator policyEvaluator = new ModifiedPolicyEvaluator(400);
		// log.debug("Starting MDP");
		watchMDP.start();
		ProbLinearSolver.getCounter().startCounting("PolicyImprovement(MDP)");
		final Policy improvedPolicy = new PolicyIterationImpl(policyEvaluator).solve(mdp, currentPolicy, currentValue);
		watchMDP.stop();
		// log.info("End of MDP: " +
		// watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		final UtilityFunction improvedValue = policyEvaluator.policyEvaluation(improvedPolicy, new UtilityFunctionImpl(mdp.getStates()), mdp);
		// checkState(improvedValue.compareTo(currentValue) >= 0, "Value function should not get worse.");
		// log.info(result);
		ProbLinearSolver.getCounter().stopCounting("PolicyImprovement(MDP)");
		log.info("Values after improve: {}", improvedValue);
		return Pair.of(improvedPolicy, improvedValue);
	}

	private UtilityFunctionWithProbImpl evaluate(final UtilityFunction baseValue, final MDPIP mdpip,
			final Policy policy) {
		ProbLinearSolver.getCounter().startCounting("PolicyEvaluation(MDPIP)");
		watchMDPIP.start();

		// evaluation
		log.debug("Evaluating policy...");
		UtilityFunction value = new UtilityFunctionWithProbImpl(baseValue);
		ProbLinearSolver.setFeasibilityOnly();
		final ProbabilityEvaluator evaluator = ProbabilityEvaluatorFactory.getAnyFeasibleInstance();
		value = new ModifiedPolicyEvaluator(1000).policyEvaluation(policy, value, evaluator.evaluate(mdpip, value));
		UtilityFunction newValue = new UtilityFunctionWithProbImpl(value);
		ProbLinearSolver.setMinimizing();
		do {
			value = newValue;
			newValue = new UtilityFunctionWithProbImpl(value);
			for ( final State s : mdpip.getStates() ) {
				newValue.updateUtility(s, calculate(mdpip, s, policy.getActionFor(s), value));
			}
		} while ( UtilityFunctionDistanceEvaluator.distanceBetween(value, newValue) > maxError );
		final UtilityFunctionWithProbImpl utilityFunction = new UtilityFunctionWithProbImpl(value);
		watchMDPIP.stop();

		ProbLinearSolver.getCounter().stopCounting("PolicyEvaluation(MDPIP)");
		return utilityFunction;
	}

	private static double calculate(final MDPIP mdpip, final State s, final Action a, final UtilityFunction value) {
		double utilityOfAction = 0;
		for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, value) ) {
			utilityOfAction += nextStateAndProb.getValue().doubleValue() * value.getUtility(nextStateAndProb.getKey());
		}
		return mdpip.getRewardFor(s) + mdpip.getDiscountFactor() * utilityOfAction;
	}

	private MDP completeNaturesPolicy(final MDPIP initialMdpip, final UtilityFunctionWithProbImpl evaluatedValue) {
		ProbLinearSolver.getCounter().startCounting("Nature'sPolicyCompletion(MDPIP)");
		watchMDPIP.start();
		final MDP mdp = new DelegateMDP(initialMdpip, evaluatedValue);
		watchMDPIP.stop();
		ProbLinearSolver.getCounter().stopCounting("Nature'sPolicyCompletion(MDPIP)");
		return mdp;
	}

	private void printSummary(final int i) {
		log.info("Summary:");
		log.info("Number of iterations = {}", i);
		log.info(ProbLinearSolver.getCounter().getSummaryAndReset());
		log.info("Number of solver calls in total = {}", ProbLinearSolver.getNumberOfSolverCalls());

		log.info("Time in Initial Guess = {}ms.", watchInitialGuess.elapsed(TimeUnit.MILLISECONDS));
		log.info("Time in MDP = {}ms.", watchMDP.elapsed(TimeUnit.MILLISECONDS));
		log.info("Time in MDPIP = {}ms.", watchMDPIP.elapsed(TimeUnit.MILLISECONDS));
		log.info("Time Solving = {}ms.", watchAll.elapsed(TimeUnit.MILLISECONDS));

		log.info("End of problem\n\n\n\n\n");
	}
}
