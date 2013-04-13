package mymdp.solver;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

public class ModifiedPolicyEvaluator implements PolicyEvaluator {
    private final int timesToExecute;

    public ModifiedPolicyEvaluator(final int k) {
	checkArgument(k > 0);
	this.timesToExecute = k;
    }

    @Override
    public UtilityFunction policyEvaluation(final Policy policy,
	    final UtilityFunction function, final MDP mdp) {
	UtilityFunction evaluatedFunction = function;
	for (int i = 0; i < timesToExecute; i++) {
	    evaluatedFunction = singleEvaluation(policy, evaluatedFunction, mdp);
	}
	return evaluatedFunction;
    }

    private UtilityFunction singleEvaluation(final Policy policy,
	    final UtilityFunction function, final MDP mdp) {
	final UtilityFunction evaluatedFunction = new UtilityFunctionImpl(
		function);
	for (final State s : mdp.getStates()) {
	    final Action a = policy.getActionFor(s);
	    double value = 0.0;
	    for (final Entry<State, Double> nextStateAndProb : mdp
		    .getPossibleStatesAndProbability(s, a).entrySet()) {
		value += nextStateAndProb.getValue()
			* function.getUtility(nextStateAndProb.getKey());
	    }
	    evaluatedFunction.updateUtility(s,
		    mdp.getRewardFor(s) + mdp.getDiscountFactor() * value);
	}
	return evaluatedFunction;
    }
}
