package mymdp.solver;

import static java.lang.Math.max;
import static mymdp.util.Pair.newPair;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.util.Pair;

public final class BellmanUtils {
    public static double calculateUtility(final MDP mdp, final State state, final UtilityFunction function) {
	double maxUtilityOfActions = 0;
	if (mdp.getActionsFor(state).size() > 0) {
	    maxUtilityOfActions = Double.NEGATIVE_INFINITY;
	}
	for (final Action action : mdp.getActionsFor(state)) {
	    double utilityOfAction = 0;
	    for (final Entry<State, Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(state, action).entrySet()) {
		utilityOfAction += nextStateAndProb.getValue().doubleValue() * function.getUtility(nextStateAndProb.getKey());
	    }
	    maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);
	}
	return mdp.getRewardFor(state) + mdp.getDiscountFactor() * maxUtilityOfActions;
    }

    public static double calculateUtilityIP(final MDPIP mdpip, final State state, final UtilityFunction function) {
	double maxUtilityOfActions = 0;
	if (mdpip.getActionsFor(state).size() > 0) {
	    maxUtilityOfActions = Double.NEGATIVE_INFINITY;
	}
	for (final Action action : mdpip.getActionsFor(state)) {
	    double utilityOfAction = 0;
	    for (final Entry<State, Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(state, action, function).entrySet()) {
		utilityOfAction += nextStateAndProb.getValue().doubleValue() * function.getUtility(nextStateAndProb.getKey());
	    }
	    maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);
	}
	return mdpip.getRewardFor(state) + mdpip.getDiscountFactor() * maxUtilityOfActions;
    }

    public static Pair<Action, Double> getGreedyActionForState(final State s, final UtilityFunction function, final MDP mdp) {
	Action maxA = null;
	double maxValue = 0;
	if (mdp.getActionsFor(s).size() > 0) {
	    maxValue = Double.NEGATIVE_INFINITY;
	}
	for (final Action a : mdp.getActionsFor(s)) {
	    double value = 0.0;
	    for (final Entry<State, Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(s, a).entrySet()) {
		value += nextStateAndProb.getValue() * function.getUtility(nextStateAndProb.getKey());
	    }
	    if (value > maxValue) {
		maxA = a;
		maxValue = value;
	    }
	}
	return newPair(maxA, maxValue);
    }

    public static Pair<Action, Double> getGreedyActionForState(final State s, final UtilityFunction function, final MDPIP mdpip) {
	Action maxA = null;
	double maxValue = 0;
	if (mdpip.getActionsFor(s).size() > 0) {
	    maxValue = Double.NEGATIVE_INFINITY;
	}
	for (final Action a : mdpip.getActionsFor(s)) {
	    double value = 0.0;
	    for (final Entry<State, Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, function).entrySet()) {
		value += nextStateAndProb.getValue() * function.getUtility(nextStateAndProb.getKey());
	    }
	    if (value > maxValue) {
		maxA = a;
		maxValue = value;
	    }
	}
	return newPair(maxA, maxValue);
    }
}
