package mymdp.solver;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

public class ErrorBoundModifiedPolicyEvaluator
	implements
		PolicyEvaluator
{
	private final double maxError;

	public ErrorBoundModifiedPolicyEvaluator(final double maxError) {
		this.maxError = maxError;
	}

	@Override
	public UtilityFunction policyEvaluation(final Policy policy,
			final UtilityFunction function, final MDP mdp) {
		final UtilityFunction evaluatedFunction = new UtilityFunctionImpl(function);
		double error = Double.POSITIVE_INFINITY;
		while ( error > maxError ) {
			error = singleEvaluation(policy, evaluatedFunction, mdp);
		}
		return evaluatedFunction;
	}

	private double singleEvaluation(final Policy policy, final UtilityFunction updatedFunction, final MDP mdp) {
		final UtilityFunction referenceFunction = new UtilityFunctionImpl(updatedFunction);
		double error = Double.NEGATIVE_INFINITY;
		for ( final State s : mdp.getStates() ) {
			final Action a = policy.getActionFor(s);
			final double oldValue = referenceFunction.getUtility(s);
			double newValue = 0.0;
			for ( final Entry<State,Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(s, a) ) {
				newValue += nextStateAndProb.getValue() * referenceFunction.getUtility(nextStateAndProb.getKey());
			}
			newValue = mdp.getRewardFor(s) + mdp.getDiscountFactor() * newValue;
			updatedFunction.updateUtility(s, newValue);
			error = max(error, abs(oldValue - newValue));
		}
		return error;
	}
}
