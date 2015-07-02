package mymdp.core;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.hash;
import static org.assertj.core.util.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;

import mymdp.exception.InvalidProbabilityFunctionException;

final class SimpleTransitionFunction
	implements
		TransitionProbability
{
	private final Map<State,Double> distributions;
	private final State currentState;
	private final Action action;

	static TransitionProbability create(final State currentState, final Action action, final Map<State,Double> distributions) {
		final SimpleTransitionFunction result = new SimpleTransitionFunction(currentState, action, distributions);
		final double total = distributions.values().stream().reduce(0.0, Double::sum);
		if ( Math.abs(1 - total) >= 0.001 ) {
			throw new InvalidProbabilityFunctionException(
					"Invalid distribution. Transitions must sum 1 but sum " + total);
		}
		return result;
	}

	static TransitionProbability empty(final State currentState, final Action action) {
		return new SimpleTransitionFunction(currentState, action, ImmutableMap.of());
	}

	private SimpleTransitionFunction(final State currentState, final Action action, final Map<State,Double> distributions) {
		this.currentState = checkNotNull(currentState);
		this.action = checkNotNull(action);
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
		return firstNonNull(distributions.get(checkNotNull(state)), Double.valueOf(0.0));
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
	public int hashCode() {
		return hash(currentState, action);
	}

	@Override
	public boolean equals(final @Nullable Object obj) {
		if ( obj == this ) {
			return true;
		}
		if ( !( obj instanceof TransitionProbability ) ) {
			return false;
		}
		final TransitionProbability other = (TransitionProbability) obj;
		return equal(currentState, other.getCurrentState())
				&& equal(action, other.getAction())
				&& ( other instanceof SimpleTransitionFunction
						? distributions.equals(( (SimpleTransitionFunction) other ).distributions)
						: newHashSet(this).equals(newHashSet(other)) );
	}

	@Override
	public String toString() {
		return distributions.toString();
	}
}
