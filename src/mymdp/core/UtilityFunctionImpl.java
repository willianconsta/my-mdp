package mymdp.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class UtilityFunctionImpl
	implements
		UtilityFunction,
		Comparable<UtilityFunction>
{

	private final Map<State,Double> utilities = new LinkedHashMap<>();
	private final Map<String,Double> utilitiesByName = new LinkedHashMap<>();

	public UtilityFunctionImpl(final Set<State> states, final double constantValue) {
		for ( final State state : states ) {
			final Double value = Double.valueOf(constantValue);
			utilities.put(state, value);
			utilitiesByName.put(state.name(), value);
		}
	}

	public UtilityFunctionImpl(final Set<State> states) {
		this(states, 0.0);
	}

	public UtilityFunctionImpl(final UtilityFunction function) {
		this(function.getStates());
		for ( final State state : function.getStates() ) {
			updateUtility(state, function.getUtility(state));
		}
	}

	@Override
	public Set<State> getStates() {
		return utilities.keySet();
	}

	@Override
	public void updateUtility(final State state, final double utility) {
		if ( utilities.get(state) == null ) {
			throw new IllegalStateException("Updating inexistent state: " + state);
		}
		utilities.put(state, utility);
		utilitiesByName.put(state.name(), utility);
	}

	@Override
	public double getUtility(final State state) {
		return utilities.get(state).doubleValue();
	}

	@Override
	public double getUtility(final String stateName) {
		return utilitiesByName.get(stateName).doubleValue();
	}

	@Override
	public int hashCode() {
		return utilities.hashCode();
	}

	@Override
	public boolean equals(final Object arg0) {
		if ( !( arg0 instanceof UtilityFunctionImpl ) ) {
			return false;
		}
		return utilities.equals(( (UtilityFunctionImpl) arg0 ).utilities);
	}

	@Override
	public String toString() {
		return ImmutableSortedMap.orderedBy(Ordering.usingToString()).putAll(utilities).build().toString();
	}

	@Override
	public int compareTo(final UtilityFunction o) {
		UtilityFunction potentialGreater = null;
		for ( final State s : getStates() ) {
			final int result = Double.compare(o.getUtility(s), getUtility(s));
			if ( result > 0 ) {
				if ( potentialGreater == this ) {
					// nada se pode afirmar
					return 0;
				}
				potentialGreater = o;
			}
			if ( result < 0 ) {
				if ( potentialGreater == o ) {
					// nada se pode afirmar
					return 0;
				}
				potentialGreater = this;
			}
		}
		if ( potentialGreater == null ) {
			return 0;
		}
		return potentialGreater == this ? 1 : -1;
	}
}
