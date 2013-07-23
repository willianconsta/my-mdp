package mymdp.solver;

import static mymdp.solver.BellmanUtils.getGreedyActionForState;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.PolicyImpl;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.util.Pair;

public class PolicyIterationImpl implements PolicyIteration {
    private final PolicyEvaluator evaluator;

    public PolicyIterationImpl(final PolicyEvaluator evaluator) {
	this.evaluator = evaluator;
    }

    @Override
    public Policy solve(final MDP mdp) {
	final UtilityFunction function = new UtilityFunctionImpl(mdp.getStates());
	final Policy policy = new PolicyImpl(mdp);

	boolean hasChanged = false;
	do {
	    hasChanged = iteration(policy, function, mdp);
	} while (hasChanged);

	return policy;
    }

    private boolean iteration(final Policy policy, final UtilityFunction function, final MDP mdp) {
	// evaluates the initial policy
	final UtilityFunction evaluatedFunction = evaluator.policyEvaluation(policy, function, mdp);

	boolean hasChanged = false;

	// for each state
	for (final State s : mdp.getStates()) {
	    Action maxA = policy.getActionFor(s);
	    double maxValue = 0.0;
	    final Pair<Action, Double> greedyAction = getGreedyActionForState(s, evaluatedFunction, mdp);
	    if (greedyAction.first != null) {
		maxA = greedyAction.first;
		maxValue = greedyAction.second.doubleValue();
	    }
	    double policyValue = 0.0;
	    for (final Entry<State, Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(s, policy.getActionFor(s))) {
		policyValue += nextStateAndProb.getValue() * evaluatedFunction.getUtility(nextStateAndProb.getKey());
	    }

	    if (maxValue > policyValue) {
		policy.updatePolicy(s, maxA);
		hasChanged = true;
	    }
	}
	return hasChanged;
    }
}
