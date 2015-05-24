package mymdp.core;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

import mymdp.exception.InvalidProbabilityFunctionException;

final class SimpleTransitionFunction
	implements
		TransitionProbability
{
	private final Map<State,Double> distributions;
	private final State currentState;
	private final Action action;

	SimpleTransitionFunction(final State currentState, final Action action, final Map<State,Double> distributions) {
		this.currentState = currentState;
		this.action = action;

		double total = 0.0;
		for ( final Double prob : distributions.values() ) {
			total += prob;
		}
		if ( !( distributions.isEmpty() || Math.abs(1 - total) < 0.001 ) ) {
			throw new InvalidProbabilityFunctionException("Invalid distribution. Transitions must sum 1 but sum " + total);
		}

		this.distributions = ImmutableMap.copyOf(distributions);
	}

	@Override
	public Iterator<Entry<State,Double>> iterator() {
		return distributions.entrySet().iterator();
	}

	@Override
	public boolean isEmpty() {
		return distributions.isEmpty();
	}

	@Override
	public double getProbabilityFor(final State state) {
		return firstNonNull(distributions.get(state), Double.valueOf(0.0));
	}

	@Override
	public State getCurrentState() {
		return currentState;
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public boolean equals(final Object arg0) {
		if ( !( arg0 instanceof TransitionProbability ) ) {
			return false;
		}
		if ( arg0 == this ) {
			return true;
		}
		return Sets.newHashSet(this).equals(Sets.newHashSet((TransitionProbability) arg0));
	}

	@Override
	public String toString() {
		return distributions.toString();
	}
}
