package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.util.CollectionUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

public class MDPBuilder {
    @VisibleForTesting
    static class StateImpl implements State {
	private final String name;
	private double reward;

	StateImpl(final String name) {
	    checkArgument(name != null && !name.isEmpty());
	    this.name = name;
	}

	@Override
	public String getName() {
	    return name;
	}

	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
	    if (!(obj instanceof State)) {
		return false;
	    }
	    return name.equals(((State) obj).getName());
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    @VisibleForTesting
    static class ActionImpl implements Action {
	private final Set<State> appliableStates = new LinkedHashSet<>();
	private final String name;

	ActionImpl(final String name) {
	    checkArgument(name != null && !name.isEmpty());
	    this.name = name;
	}

	@Override
	public boolean isApplyableTo(final State state) {
	    return appliableStates.contains(state);
	}

	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
	    if (!(obj instanceof ActionImpl)) {
		return false;
	    }
	    return name.equals(((ActionImpl) obj).name);
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    private final Map<String, StateImpl> states;
    private final Map<Action, Map<State, Map<State, Double>>> transitions;
    private double discountRate;

    public MDPBuilder() {
	states = new LinkedHashMap<>();
	transitions = new LinkedHashMap<>();
    }

    public MDPBuilder states(final Set<String> stateDefs) {
	for (final String stateDef : stateDefs) {
	    states.put(stateDef, new StateImpl(stateDef));
	}
	return this;
    }

    public MDPBuilder actions(final String actionName, final Set<String[]> transitionDefs) {
	final ActionImpl action = new ActionImpl(actionName);
	for (final String[] transitionDef : transitionDefs) {
	    final StateImpl state1 = checkNotNull(states.get(transitionDef[0]));
	    action.appliableStates.add(state1);

	    Map<State, Map<State, Double>> probs = transitions.get(action);
	    if (probs == null) {
		probs = new LinkedHashMap<>();
		transitions.put(action, probs);
	    }

	    final State state2 = checkNotNull(states.get(transitionDef[1]));
	    Map<State, Double> map = probs.get(state1);
	    if (map == null) {
		map = new LinkedHashMap<>();
		probs.put(state1, map);
	    }
	    map.put(state2, Double.parseDouble(checkNotNull(transitionDef[2])));
	}
	return this;
    }

    public MDPBuilder reward(final Map<String, Double> rewards) {
	for (final Entry<String, Double> entry : rewards.entrySet()) {
	    states.get(entry.getKey()).reward = entry.getValue();
	}
	return this;
    }

    public MDPBuilder discountRate(final double rate) {
	checkArgument(Range.closed(0.0, 1.0).contains(rate));
	this.discountRate = rate;
	return this;
    }

    public MDP build() {
	return new MDP() {
	    private final Set<State> states = ImmutableSet.<State> copyOf(MDPBuilder.this.states.values());
	    private final Map<Action, Map<State, Map<State, Double>>> transitions = ImmutableMap.copyOf(MDPBuilder.this.transitions);

	    @Override
	    public Set<State> getStates() {
		return states;
	    }

	    @Override
	    public Set<Action> getAllActions() {
		return transitions.keySet();
	    }

	    @Override
	    public Set<Action> getActionsFor(final State state) {
		final Set<Action> appliableActions = new LinkedHashSet<>();
		for (final Action action : transitions.keySet()) {
		    if (((ActionImpl) action).appliableStates.contains(state)) {
			appliableActions.add(action);
		    }
		}
		return appliableActions;
	    }

	    @Override
	    public Map<State, Double> getPossibleStatesAndProbability(final State initialState, final Action action) {
		return CollectionUtils.nullToEmpty(transitions.get(action).get(initialState));
	    }

	    @Override
	    public double getRewardFor(final State state) {
		return ((StateImpl) state).reward;
	    }

	    @Override
	    public double getDiscountFactor() {
		return discountRate;
	    }

	    @Override
	    public String toString() {
		return Objects.toStringHelper(this).add("states", states).add("transitions", transitions).add("discountRate", discountRate)
			.toString();
	    }
	};
    }
}
