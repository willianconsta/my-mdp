package mymdp.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class UtilityFunctionImpl implements UtilityFunction {

    private final Map<State, Double> utilities = new HashMap<>();

    public UtilityFunctionImpl(final Set<State> states, final double constantValue) {
	for (final State state : states) {
	    utilities.put(state, Double.valueOf(constantValue));
	}
    }

    public UtilityFunctionImpl(final Set<State> states) {
	this(states, 0.0);
    }

    public UtilityFunctionImpl(final UtilityFunction function) {
	this(function.getStates());
	for (final State state : function.getStates()) {
	    updateUtility(state, function.getUtility(state));
	}
    }

    @Override
    public Set<State> getStates() {
	return utilities.keySet();
    }

    @Override
    public void updateUtility(final State state, final double utility) {
	if (utilities.get(state) == null) {
	    throw new IllegalStateException("Updating inexistent state: " + state);
	}
	utilities.put(state, utility);
    }

    @Override
    public double getUtility(final State state) {
	final Double utility = utilities.get(state);
	return utility.doubleValue();
    }

    @Override
    public int hashCode() {
	return utilities.hashCode();
    }

    @Override
    public boolean equals(final Object arg0) {
	if (!(arg0 instanceof UtilityFunctionImpl)) {
	    return false;
	}
	return utilities.equals(((UtilityFunctionImpl) arg0).utilities);
    }

    @Override
    public String toString() {
	return ImmutableSortedMap.orderedBy(Ordering.usingToString()).putAll(utilities).build().toString();
    }
}
