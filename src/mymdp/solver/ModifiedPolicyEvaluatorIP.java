package mymdp.solver;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

public class ModifiedPolicyEvaluatorIP {
    private final int timesToExecute;

    public ModifiedPolicyEvaluatorIP(final int k) {
	checkArgument(k > 0);
	this.timesToExecute = k;
    }

    public UtilityFunction policyEvaluation(final Policy policy, final UtilityFunction function, final MDPIP mdpip) {
	UtilityFunction evaluatedFunction = function;
	for (int i = 0; i < timesToExecute; i++) {
	    evaluatedFunction = singleEvaluation(policy, evaluatedFunction, mdpip);
	}
	return evaluatedFunction;
    }

    private UtilityFunction singleEvaluation(final Policy policy, final UtilityFunction function, final MDPIP mdpip) {
	final UtilityFunction evaluatedFunction = new UtilityFunctionImpl(function);
	for (final State s : mdpip.getStates()) {
	    final Action a = policy.getActionFor(s);
	    double value = 0.0;
	    for (final Entry<State, Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, function)) {
		value += nextStateAndProb.getValue() * function.getUtility(nextStateAndProb.getKey());
	    }
	    evaluatedFunction.updateUtility(s, mdpip.getRewardFor(s) + mdpip.getDiscountFactor() * value);
	}
	return evaluatedFunction;
    }
}
