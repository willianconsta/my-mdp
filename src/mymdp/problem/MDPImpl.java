package mymdp.problem;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static java.util.Objects.hash;
import static mymdp.util.CollectionUtils.nullToEmpty;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;

final class MDPImpl
	implements
		MDP
{
	private final Set<State> states;
	private final Map<Action,Map<State,Map<State,Double>>> transitions;
	private final double discountRate;
	private final Map<State,Double> rewards;

	MDPImpl(final Set<State> states,
			final Map<Action,Map<State,Map<State,Double>>> transitions,
			final Map<State,Double> rewards,
			final double discountRate) {
		this.states = ImmutableSet.copyOf(states);
		this.transitions = ImmutableMap.copyOf(transitions);
		this.rewards = ImmutableMap.copyOf(rewards);
		this.discountRate = discountRate;

		validateModelConsistency();
	}

	private void validateModelConsistency() {
		final Set<State> allInitialStates = transitions.values().stream().flatMap(map -> map.keySet().stream())
				.collect(Collectors.toSet());
		checkArgument(allInitialStates.containsAll(states), "States must have at least one valid action. States %s have no action.",
				Sets.filter(states, not(in(allInitialStates))));

		checkArgument(transitions.entrySet().stream().allMatch(entry -> entry.getValue().values().stream()
				.allMatch(map -> DoubleMath.fuzzyEquals(map.values().stream().reduce(0.0, Double::sum), 1.0, 0.00001))),
				"All transitions must sum 1.",
				transitions.entrySet().stream().filter(entry -> entry.getValue().values().stream()
						.filter(map -> !DoubleMath.fuzzyEquals(map.values().stream().reduce(0.0, Double::sum), 1.0, 0.00001))
						.findAny().isPresent()).collect(Collectors.toList()));

		checkArgument(states.equals(rewards.keySet()), "Rewards are not defined to some states. States %s have no reward.",
				Sets.difference(states, rewards.keySet()));

		checkArgument(Range.open(0., 1.).contains(discountRate), "Discount rate should be between 0..1. Is %s", discountRate);
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
			if ( action.isApplicableTo(state) ) {
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
		return rewards.get(state);
	}

	@Override
	public double getDiscountFactor() {
		return discountRate;
	}

	@Override
	public int hashCode() {
		return hash(states, discountRate);
	}

	@Override
	public boolean equals(final @Nullable Object obj) {
		if ( obj == this ) {
			return true;
		}
		if ( !( obj instanceof MDPImpl ) ) {
			return false;
		}
		final MDPImpl other = (MDPImpl) obj;
		return equal(this.discountRate, other.discountRate)
				&& equal(this.states, other.states)
				&& equal(this.rewards, other.rewards)
				&& equal(this.transitions, other.transitions);
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