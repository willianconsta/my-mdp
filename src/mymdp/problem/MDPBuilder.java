package mymdp.problem;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static java.lang.Double.parseDouble;
import static mymdp.util.CollectionUtils.nullToEmpty;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;

public class MDPBuilder
{
	static final class MDPImpl
		implements
			MDP
	{
		private final Set<State> states;
		private final Map<Action,Map<State,Map<State,Double>>> transitions;
		private final double discountRate;

		MDPImpl(final Set<State> states, final Map<Action,Map<State,Map<State,Double>>> transitions, final double discountRate) {
			this.states = ImmutableSet.copyOf(states);
			checkArgument(transitions.keySet().containsAll(states),
					"States must have at least one valid action. States %s have no action.",
					Sets.filter(states, not(in(transitions.keySet()))));
			this.transitions = ImmutableMap.copyOf(transitions);
			checkArgument(Range.open(0., 1.).contains(discountRate), "Discount rate should be between 0..1. Is %s", discountRate);
			this.discountRate = discountRate;
		}

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
			for ( final Action action : transitions.keySet() ) {
				if ( ( (ActionImpl) action ).appliableStates.contains(state) ) {
					appliableActions.add(action);
				}
			}
			return appliableActions;
		}

		@Override
		public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action) {
			return TransitionProbability.createSimple(initialState, action, nullToEmpty(transitions.get(action).get(initialState)));
		}

		@Override
		public double getRewardFor(final State state) {
			return ( (StateImpl) state ).reward;
		}

		@Override
		public double getDiscountFactor() {
			return discountRate;
		}

		@Override
		public String toString() {
			return toStringHelper(this)
					.add("states", states)
					.add("transitions", transitions)
					.add("discountRate", discountRate)
					.toString();
		}
	}

	@VisibleForTesting
	static class StateImpl
		implements
			State
	{
		private final String name;
		private double reward;

		StateImpl(final String name) {
			checkArgument(name != null && !name.isEmpty());
			this.name = name;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if ( !( obj instanceof State ) ) {
				return false;
			}
			return name.equals(( (State) obj ).name());
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@VisibleForTesting
	static class ActionImpl
		implements
			Action
	{
		private final Set<State> appliableStates = new LinkedHashSet<>();
		private final String name;

		ActionImpl(final String name) {
			checkArgument(name != null && !name.isEmpty());
			this.name = name;
		}

		@Override
		public boolean isApplicableTo(final State state) {
			return appliableStates.contains(state);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public boolean equals(final Object obj) {
			if ( !( obj instanceof Action ) ) {
				return false;
			}
			return name.equals(( (Action) obj ).name());
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private final Map<String,StateImpl> states;
	private final Map<Action,Map<State,Map<State,Double>>> transitions;
	private double discountRate;

	public MDPBuilder() {
		states = new LinkedHashMap<>();
		transitions = new LinkedHashMap<>();
	}

	public MDPBuilder states(final Set<String> stateDefs) {
		for ( final String stateDef : stateDefs ) {
			states.put(stateDef, new StateImpl(stateDef));
		}
		return this;
	}

	public MDPBuilder actions(final String actionName, final Set<String[]> transitionDefs) {
		final ActionImpl action = new ActionImpl(actionName);
		for ( final String[] transitionDef : transitionDefs ) {
			final StateImpl state1 = checkNotNull(states.get(transitionDef[0]));
			action.appliableStates.add(state1);

			Map<State,Map<State,Double>> probs = transitions.get(action);
			if ( probs == null ) {
				probs = new LinkedHashMap<>();
				transitions.put(action, probs);
			}

			final State state2 = checkNotNull(states.get(transitionDef[1]));
			Map<State,Double> map = probs.get(state1);
			if ( map == null ) {
				map = new LinkedHashMap<>();
				probs.put(state1, map);
			}
			map.put(state2, parseDouble(checkNotNull(transitionDef[2])));
		}
		return this;
	}

	public MDPBuilder reward(final Map<String,Double> rewards) {
		for ( final Entry<String,Double> entry : rewards.entrySet() ) {
			states.get(entry.getKey()).reward = entry.getValue();
		}
		return this;
	}

	public MDPBuilder discountRate(final double rate) {
		checkArgument(Range.closed(0.0, 1.0).contains(rate), "Discount rate must be in 0..1 but is %s", rate);
		this.discountRate = rate;
		return this;
	}

	public MDP build() {
		return new MDPImpl(ImmutableSet.copyOf(states.values()), transitions, discountRate);
	}
}
