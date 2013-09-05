package mymdp.dual;

import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.ProbabilityFunction;
import mymdp.core.State;
import mymdp.core.UtilityFunctionWithProbImpl;

public class DelegateMDP implements MDP {
	private final MDPIP mdpip;
	private UtilityFunctionWithProbImpl function;

	public DelegateMDP(final MDPIP mdpip) {
		this.mdpip = mdpip;
	}

	@Override
	public Set<State> getStates() {
		return mdpip.getStates();
	}

	@Override
	public Set<Action> getAllActions() {
		return mdpip.getAllActions();
	}

	@Override
	public Set<Action> getActionsFor(final State state) {
		return mdpip.getActionsFor(state);
	}

	@Override
	public ProbabilityFunction getPossibleStatesAndProbability(final State initialState, final Action action) {
		// if (function.getProbability(initialState).first.equals(action)) {
		// return function.getProbability(initialState).second;
		// } else {
		return mdpip.getPossibleStatesAndProbability(initialState, action, function);
		// }
	}

	@Override
	public double getRewardFor(final State state) {
		return mdpip.getRewardFor(state);
	}

	@Override
	public double getDiscountFactor() {
		return mdpip.getDiscountFactor();
	}

	public void setFunction(final UtilityFunctionWithProbImpl function) {
		this.function = function;
	}
}
