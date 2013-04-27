package mymdp.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mymdp.util.Pair;

public class UtilityFunctionWithProbImpl extends UtilityFunctionImpl {
    private final Map<State, Pair<Action, Map<State, Double>>> actionAndProbByStates;

    public UtilityFunctionWithProbImpl(final Set<State> states) {
	super(states);
	this.actionAndProbByStates = new HashMap<>();
    }

    public UtilityFunctionWithProbImpl(final Set<State> states, final double value) {
	super(states, value);
	this.actionAndProbByStates = new HashMap<>();
    }

    public UtilityFunctionWithProbImpl(final UtilityFunction function) {
	super(function);
	if (!(function instanceof UtilityFunctionWithProbImpl)) {
	    this.actionAndProbByStates = new HashMap<>();
	} else {
	    this.actionAndProbByStates = new HashMap<>(((UtilityFunctionWithProbImpl) function).actionAndProbByStates);
	}
    }

    public void updateUtility(final State state, final double utility, final Action action, final Map<State, Double> prob) {
	super.updateUtility(state, utility);
	actionAndProbByStates.put(state, Pair.newPair(action, prob));
    }

    public Pair<Action, Map<State, Double>> getProbability(final State state) {
	return actionAndProbByStates.get(state);
    }
}
