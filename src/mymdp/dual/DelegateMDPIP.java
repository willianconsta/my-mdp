package mymdp.dual;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singleton;

import java.util.HashSet;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunction;
import mymdp.problem.ImprecisionGenerator;
import mymdp.problem.MDPIPBuilder;

public class DelegateMDPIP
	implements
		MDPIP
{
	private final MDPIP mdpip;

	DelegateMDPIP(final MDP mdp, final Policy policy, final ImprecisionGenerator generator) {
		this.mdpip = MDPIPBuilder.createFromMDP(new MDP() {
			private final MDP innerMdp = mdp;
			private final Set<Action> actions = new HashSet<>();

			{
				for ( final State s : mdp.getStates() ) {
					actions.add(policy.getActionFor(s));
				}
				checkArgument(innerMdp.getAllActions().containsAll(actions));
			}

			@Override
			public Set<State> getStates() {
				return innerMdp.getStates();
			}

			@Override
			public Set<Action> getAllActions() {
				return actions;
			}

			@Override
			public Set<Action> getActionsFor(final State state) {
				checkArgument(innerMdp.getActionsFor(state).contains(policy.getActionFor(state)));
				return singleton(policy.getActionFor(state));
			}

			@Override
			public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action) {
				if ( action.equals(policy.getActionFor(initialState)) ) {
					return innerMdp.getPossibleStatesAndProbability(initialState, action);
				}
				return TransitionProbability.empty(initialState, action);
			}

			@Override
			public double getRewardFor(final State state) {
				return innerMdp.getRewardFor(state);
			}

			@Override
			public double getDiscountFactor() {
				return innerMdp.getDiscountFactor();
			}
		}, generator);
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
	public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action, final UtilityFunction function) {
		return mdpip.getPossibleStatesAndProbability(initialState, action, function);
	}

	@Override
	public double getRewardFor(final State state) {
		return mdpip.getRewardFor(state);
	}

	@Override
	public double getDiscountFactor() {
		return mdpip.getDiscountFactor();
	}

	@Override
	public int hashCode() {
		return mdpip.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return mdpip.equals(obj);
	}

	@Override
	public String toString() {
		return mdpip.toString();
	}
}
