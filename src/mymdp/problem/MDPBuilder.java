package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Double.parseDouble;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;

public class MDPBuilder
{
	private final Map<String,StateImpl> states = new LinkedHashMap<>();
	private final Map<Action,Map<State,Map<State,Double>>> transitions = new LinkedHashMap<>();
	private final Map<State,Double> rewards = new LinkedHashMap<>();
	private double discountRate;

	private MDPBuilder() {
	}

	public static MDPBuilder newBuilder() {
		return new MDPBuilder();
	}

	public MDPBuilder states(final Set<String> stateDefs) {
		for ( final String stateDef : stateDefs ) {
			states.put(stateDef, new StateImpl(stateDef));
		}
		return this;
	}

	/**
	 * 
	 * @param actionName
	 * @param transitionDefs
	 *            a set of string arrays where the 0th index is the current state, the 1st index is the next state and the 2nd index is the probability of
	 *            transition.
	 * @return
	 */
	public MDPBuilder action(final String actionName, final Set<String[]> transitionDefs) {
		final Set<State> appliableStates = new HashSet<>();
		for ( final String[] transitionDef : transitionDefs ) {
			final StateImpl state1 = checkNotNull(states.get(transitionDef[0]));
			appliableStates.add(state1);
		}

		final ActionImpl action = new ActionImpl(actionName, appliableStates);

		for ( final String[] transitionDef : transitionDefs ) {
			Map<State,Map<State,Double>> probs = transitions.get(action);
			if ( probs == null ) {
				probs = new LinkedHashMap<>();
				transitions.put(action, probs);
			}

			final StateImpl state1 = checkNotNull(states.get(transitionDef[0]));
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

	public MDPBuilder rewards(final Map<String,Double> rewards) {
		this.rewards.putAll(rewards.entrySet().stream().collect(Collectors.toMap(entry -> states.get(entry.getKey()), Map.Entry::getValue)));
		return this;
	}

	public MDPBuilder discountRate(final double rate) {
		checkArgument(Range.closed(0.0, 1.0).contains(rate), "Discount rate must be in 0..1 but is %s", rate);
		this.discountRate = rate;
		return this;
	}

	public MDP build() {
		return new MDPImpl(ImmutableSet.copyOf(states.values()), transitions, rewards, discountRate);
	}
}
