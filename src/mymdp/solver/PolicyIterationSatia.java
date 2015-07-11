package mymdp.solver;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.PolicyImpl;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.dual.evaluator.ProbabilityEvaluator;
import mymdp.dual.evaluator.ProbabilityEvaluatorFactory;
import mymdp.util.UtilityFunctionDistanceEvaluator;

/**
 * Policy Iteration as in Satia.
 * 
 * @author Willian
 */
public final class PolicyIterationSatia
{
	private static final Logger log = LogManager.getLogger(PolicyIterationSatia.class);

	private final double delta;
	private int i;

	public PolicyIterationSatia(final double delta) {
		this.delta = delta;
	}

	private static double calculate(final MDPIP mdpip, final State s, final Action a, final UtilityFunction value) {
		double utilityOfAction = 0;
		for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, value) ) {
			utilityOfAction += nextStateAndProb.getValue().doubleValue() * value.getUtility(nextStateAndProb.getKey());
		}
		return mdpip.getRewardFor(s) + mdpip.getDiscountFactor() * utilityOfAction;
	}

	public Policy solve(final MDPIP mdpip) {
		ProbLinearSolver.initializeCount();
		final Stopwatch watchEvaluating = Stopwatch.createUnstarted();
		final Stopwatch watchImproving = Stopwatch.createUnstarted();
		final Stopwatch watchAll = Stopwatch.createStarted();
		final PolicyImpl policy = new PolicyImpl(mdpip);

		int numberOfSolverCallsInEvaluation = 0;
		int numberOfSolverCallsInImprovement = 0;

		boolean improved;
		i = 0;
		do {
			int numSolverCalls = ProbLinearSolver.getNumberOfSolverCalls();
			watchEvaluating.start();
			final UtilityFunction valueFunction = evaluate(mdpip, policy);
			watchEvaluating.stop();
			numberOfSolverCallsInEvaluation += ProbLinearSolver.getNumberOfSolverCalls() - numSolverCalls;
			log.info("Values after evaluation: " + valueFunction);

			numSolverCalls = ProbLinearSolver.getNumberOfSolverCalls();
			watchImproving.start();
			improved = improvement(mdpip, policy, valueFunction);
			watchImproving.stop();
			numberOfSolverCallsInImprovement += ProbLinearSolver.getNumberOfSolverCalls() - numSolverCalls;
			log.info("Values after improvement: " + valueFunction);
			i++;
		} while ( improved );
		watchAll.stop();
		log.info("Summary:");
		log.info("Number of iterations = " + i);
		log.info("Number of solver calls in solving = " + ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Number of solver calls in evaluation = " + numberOfSolverCallsInEvaluation);
		log.info("Number of solver calls in improvement = " + numberOfSolverCallsInImprovement);

		log.info("Time in Evaluating = " + watchEvaluating.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in Improving = " + watchImproving.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		return policy;
	}

	private static boolean improvement(final MDPIP mdpip, final PolicyImpl policy, final UtilityFunction valueFunction) {
		// improvement
		ProbLinearSolver.setMinimizing();
		log.debug("Improving policy...");
		boolean improvement = false;
		for ( final State s : mdpip.getStates() ) {
			double maxUtilityOfActions = 0;
			Action maxAction = policy.getActionFor(s);
			if ( mdpip.getActionsFor(s).size() > 0 ) {
				maxUtilityOfActions = Double.NEGATIVE_INFINITY;
			}
			for ( final Action action : mdpip.getActionsFor(s) ) {
				double utilityOfAction = 0;
				for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, action,
						valueFunction) ) {
					utilityOfAction += nextStateAndProb.getValue().doubleValue()
							* valueFunction.getUtility(nextStateAndProb.getKey());
				}
				if ( maxUtilityOfActions < utilityOfAction
						|| maxUtilityOfActions == utilityOfAction && maxAction.name().compareTo(action.name()) < 0 ) {
					maxUtilityOfActions = utilityOfAction;
					maxAction = action;
				}
			}

			if ( !policy.getActionFor(s).equals(maxAction) ) {
				log.debug("Policy improvement found. For state " + s + " was action " + policy.getActionFor(s)
						+ " but turns out that " + maxAction + " is better.");
				policy.updatePolicy(s, maxAction);
				improvement = true;
				// return true;
			}
		}
		return improvement;
		// return false;
	}

	private UtilityFunction evaluate(final MDPIP mdpip, final PolicyImpl policy) {
		// evaluation
		log.debug("Evaluating policy...");
		UtilityFunction value = new UtilityFunctionImpl(mdpip.getStates());
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
		} while ( UtilityFunctionDistanceEvaluator.distanceBetween(value, newValue) > delta );
		return value;
	}
}
