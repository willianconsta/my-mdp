package mymdp.solver;

import static java.lang.Math.max;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.PolicyImpl;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.dual.evaluator.ProbabilityEvaluator;
import mymdp.dual.evaluator.ProbabilityEvaluatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

/**
 * Policy Iteration as in Satia.
 * 
 * @author Willian
 */
public final class PolicyIterationSatia {
	private static final Logger log = LogManager.getLogger(PolicyIterationSatia.class);
	private static final String SOLUTIONS_DIR = "solutions";

	private final double delta;
	private int i;

	public PolicyIterationSatia(final double delta) {
		this.delta = delta;
	}

	private double calculate(final MDPIP mdpip, final State s, final UtilityFunction value) {
		double maxUtilityOfActions = 0;
		if (mdpip.getActionsFor(s).size() > 0) {
			maxUtilityOfActions = Double.NEGATIVE_INFINITY;
		}
		for (final Action action : mdpip.getActionsFor(s)) {
			double utilityOfAction = 0;
			for (final Entry<State, Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, action, value)) {
				utilityOfAction += nextStateAndProb.getValue().doubleValue() * value.getUtility(nextStateAndProb.getKey());
			}
			maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);
		}
		return mdpip.getRewardFor(s) + mdpip.getDiscountFactor() * maxUtilityOfActions;
	}

	public Policy solve(final MDPIP mdpip) {
		ProbLinearSolver.initializeCount();
		final Stopwatch watchEvaluating = new Stopwatch();
		final Stopwatch watchImproving = new Stopwatch();
		final Stopwatch watchAll = new Stopwatch().start();
		final PolicyImpl policy = new PolicyImpl(mdpip);
		boolean improved;
		i = 0;
		do {
			watchEvaluating.start();
			final UtilityFunction valueFunction = evaluate(mdpip, policy);
			watchEvaluating.stop();
			watchImproving.start();
			improved = improvement(mdpip, policy, valueFunction);
			watchImproving.stop();
			i++;
		} while (improved);
		watchAll.stop();
		log.info("Summary:");
		log.info("Number of iterations = " + i);
		log.info("Number of solver calls in solving = " + ProbLinearSolver.getNumberOfSolverCalls());

		log.info("Time in Evaluating = " + watchEvaluating.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time in Improving = " + watchImproving.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		log.info("Time Solving = " + watchAll.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		return policy;
	}

	private boolean improvement(final MDPIP mdpip, final PolicyImpl policy,
			final UtilityFunction valueFunction) {
		// improvement
		log.debug("Improving policy...");
		boolean improvement = false;
		for (final State s : mdpip.getStates()) {
			double maxUtilityOfActions = 0;
			Action maxAction = policy.getActionFor(s);
			if (mdpip.getActionsFor(s).size() > 0) {
				maxUtilityOfActions = Double.NEGATIVE_INFINITY;
			}
			for (final Action action : mdpip.getActionsFor(s)) {
				double utilityOfAction = 0;
				for (final Entry<State, Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, action, valueFunction)) {
					utilityOfAction += nextStateAndProb.getValue().doubleValue() * valueFunction.getUtility(nextStateAndProb.getKey());
				}
				if (maxUtilityOfActions < utilityOfAction || maxUtilityOfActions == utilityOfAction
						&& maxAction.name().compareTo(action.name()) < 0) {
					maxUtilityOfActions = utilityOfAction;
					maxAction = action;
				}
			}

			if (!policy.getActionFor(s).equals(maxAction)) {
				log.debug("Policy improvement found. For state " + s + " was action " + policy.getActionFor(s) + " but turns out that "
						+ maxAction + " is better.");
				policy.updatePolicy(s, maxAction);
				improvement = true;
			}
		}
		return improvement;
	}

	private UtilityFunction evaluate(final MDPIP mdpip, final PolicyImpl policy) {
		// evaluation
		log.debug("Evaluating policy...");
		UtilityFunction value = new UtilityFunctionWithProbImpl(mdpip.getStates());
		boolean improved;
		ProbLinearSolver.setFeasibilityOnly();
		final ProbabilityEvaluator evaluator = ProbabilityEvaluatorFactory.getAnyFeasibleInstance(SOLUTIONS_DIR + "\\evaluating_satia_" + i
				+ ".txt");
		value = new ModifiedPolicyEvaluator(1000).policyEvaluation(policy, value, evaluator.evaluate(mdpip));
		do {
			improved = false;
			ProbLinearSolver.setMinimizing();
			for (final State s : mdpip.getStates()) {
				final double valueMinimizingP = calculate(mdpip, s, value);
				if (Math.abs(value.getUtility(s) - valueMinimizingP) > delta) {
					value.updateUtility(s, valueMinimizingP);
					improved = true;
				}
			}
		} while (improved);
		return value;
	}
}
