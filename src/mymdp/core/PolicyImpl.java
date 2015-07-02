package mymdp.core;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

public class PolicyImpl
	implements
		Policy
{
	private final Map<String,Action> policies = new LinkedHashMap<>();

	public PolicyImpl(final MDP mdp) {
		for ( final State state : mdp.getStates() ) {
			final Set<Action> actionsForState = mdp.getActionsFor(state);
			checkState(!actionsForState.isEmpty());
			final String actionName = Collections.min(Collections2.transform(actionsForState, Action::name));
			final Action action = Maps.uniqueIndex(actionsForState, Action::name).get(actionName);
			policies.put(state.name(), action);
		}
	}

	public PolicyImpl(final MDPIP mdpip) {
		for ( final State state : mdpip.getStates() ) {
			final Set<Action> actionsForState = mdpip.getActionsFor(state);
			checkState(!actionsForState.isEmpty());
			final String actionName = Collections.min(Collections2.transform(actionsForState, Action::name));
			final Action action = Maps.uniqueIndex(actionsForState, Action::name).get(actionName);
			policies.put(state.name(), action);
		}
	}

	@Override
	public void updatePolicy(final State state, final Action policy) {
		final String stateName = state.name();
		if ( policies.get(stateName) == null ) {
			throw new IllegalStateException("Updating inexistent state.");
		}
		policies.put(stateName, policy);
	}

	@Override
	public Action getActionFor(final State state) {
		return getActionFor(state.name());
	}

	@Override
	public Action getActionFor(final String stateName) {
		return policies.get(stateName);
	}

	@Override
	public boolean equals(final Object arg0) {
		if ( arg0 == null ) {
			return false;
		}
		if ( arg0 == this ) {
			return true;
		}
		if ( !( arg0 instanceof PolicyImpl ) ) {
			return false;
		}
		final PolicyImpl other = (PolicyImpl) arg0;
		return policies.equals(other.policies);
	}

	@Override
	public String toString() {
		return policies.toString();
	}
}
