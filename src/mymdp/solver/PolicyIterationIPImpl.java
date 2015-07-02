package mymdp.solver;

import static mymdp.solver.BellmanUtils.getGreedyActionForState;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.PolicyImpl;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.util.Pair;

public class PolicyIterationIPImpl
	implements
		PolicyIterationIP
{

	private final ModifiedPolicyEvaluatorIP evaluator;

	public PolicyIterationIPImpl(final ModifiedPolicyEvaluatorIP evaluator) {
		this.evaluator = evaluator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mymdp.solver.PolicyIterationIP#solve(mymdp.core.MDPIP)
	 */
	@Override
	public Policy solve(final MDPIP mdpip) {
		final UtilityFunction function = new UtilityFunctionImpl(mdpip.getStates());
		final Policy policy = new PolicyImpl(mdpip);

		boolean hasChanged = false;
		do {
			hasChanged = iteration(policy, function, mdpip);
		} while ( hasChanged );

		return policy;
	}

	private boolean iteration(final Policy policy, final UtilityFunction function, final MDPIP mdpip) {
		final UtilityFunction evaluatedFunction = evaluator.policyEvaluation(policy, function, mdpip);
		boolean hasChanged = false;
		for ( final State s : mdpip.getStates() ) {

			Action maxA = policy.getActionFor(s);
			double maxValue = 0.0;
			final Pair<Action,Double> greedyAction = getGreedyActionForState(s, evaluatedFunction, mdpip);
			if ( greedyAction.getFirst() != null ) {
				maxA = greedyAction.getFirst();
				maxValue = greedyAction.getSecond().doubleValue();
			}
			double policyValue = 0.0;
			for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, policy.getActionFor(s),
					evaluatedFunction) ) {
				policyValue += nextStateAndProb.getValue() * evaluatedFunction.getUtility(nextStateAndProb.getKey());
			}

			if ( maxValue > policyValue || maxValue == policyValue && policy.getActionFor(s).name().compareTo(maxA.name()) < 0 ) {
				policy.updatePolicy(s, maxA);
				hasChanged = true;
			}
		}
		return hasChanged;
	}
}
