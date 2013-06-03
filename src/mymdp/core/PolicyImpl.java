package mymdp.core;

import static com.google.common.collect.Iterables.get;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PolicyImpl implements Policy {

    private final Map<State, Action> policies = new LinkedHashMap<>();

    public PolicyImpl(final MDP mdp) {
	for (final State state : mdp.getStates()) {
	    final Set<Action> actionsFor = mdp.getActionsFor(state);
	    if (!actionsFor.isEmpty()) {
		policies.put(state, get(actionsFor, new Random(1234).nextInt(actionsFor.size())));
	    }
	}
    }

    public PolicyImpl(final MDPIP mdpip) {
	for (final State state : mdpip.getStates()) {
	    final Set<Action> actionsFor = mdpip.getActionsFor(state);
	    if (!actionsFor.isEmpty()) {
		policies.put(state, get(actionsFor, new Random(1234).nextInt(actionsFor.size())));
	    }
	}
    }

    public Set<State> getStates() {
	return policies.keySet();
    }

    @Override
    public void updatePolicy(final State state, final Action policy) {
	if (policies.get(state) == null) {
	    throw new IllegalStateException("Updating inexistent state.");
	}
	policies.put(state, policy);
    }

    @Override
    public Action getActionFor(final State state) {
	return policies.get(state);
    }

    @Override
    public boolean equals(final Object arg0) {
	if (arg0 == null) {
	    return false;
	}
	if (arg0 == this) {
	    return true;
	}
	if (!(arg0 instanceof PolicyImpl)) {
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
