package mymdp.solver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.max;
import static mymdp.util.Pair.of;

import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.util.Pair;

public final class BellmanUtils
{
	public static double calculateUtility(final MDP mdp, final State state, final UtilityFunction function) {
		double maxUtilityOfActions = Double.NEGATIVE_INFINITY;
		checkState(!mdp.getActionsFor(state).isEmpty(), "No actions found for state %s", state);
		for ( final Action action : mdp.getActionsFor(state) ) {
			double utilityOfAction = 0;
			for ( final Entry<State,Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(state, action) ) {
				utilityOfAction += nextStateAndProb.getValue().doubleValue() * function.getUtility(nextStateAndProb.getKey());
			}
			maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);
		}
		return mdp.getRewardFor(state) + mdp.getDiscountFactor() * maxUtilityOfActions;
	}

	public static double calculateUtility(final MDP mdp, final State state, final Policy policy, final UtilityFunction function) {
		double maxUtilityOfActions = Double.NEGATIVE_INFINITY;
		final Action action = policy.getActionFor(state);
		checkNotNull(action);
		double utilityOfAction = 0;
		for ( final Entry<State,Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(state, action) ) {
			utilityOfAction += nextStateAndProb.getValue().doubleValue() * function.getUtility(nextStateAndProb.getKey());
		}
		maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);

		return mdp.getRewardFor(state) + mdp.getDiscountFactor() * maxUtilityOfActions;
	}

	public static double calculateUtilityIP(final MDPIP mdpip, final State state, final UtilityFunction function) {
		double maxUtilityOfActions = Double.NEGATIVE_INFINITY;
		checkState(!mdpip.getActionsFor(state).isEmpty());
		for ( final Action action : mdpip.getActionsFor(state) ) {
			double utilityOfAction = 0;
			for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(state, action, function) ) {
				utilityOfAction += nextStateAndProb.getValue().doubleValue() * function.getUtility(nextStateAndProb.getKey());
			}
			maxUtilityOfActions = max(maxUtilityOfActions, utilityOfAction);
		}
		return mdpip.getRewardFor(state) + mdpip.getDiscountFactor() * maxUtilityOfActions;
	}

	public static Pair<Action,Double> getGreedyActionForState(final State s, final UtilityFunction function, final MDP mdp) {
		Action maxA = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		checkState(!mdp.getActionsFor(s).isEmpty(), "No actions found for state %s", s);
		for ( final Action a : mdp.getActionsFor(s) ) {
			double value = 0.0;
			for ( final Entry<State,Double> nextStateAndProb : mdp.getPossibleStatesAndProbability(s, a) ) {
				value += nextStateAndProb.getValue() * function.getUtility(nextStateAndProb.getKey());
			}
			if ( value > maxValue || value == maxValue && maxA.name().compareTo(a.name()) < 0 ) {
				maxA = a;
				maxValue = value;
			}
		}
		return of(maxA, maxValue);
	}

	public static Pair<Action,Double> getGreedyActionForState(final State s, final UtilityFunction function, final MDPIP mdpip) {
		Action maxA = null;
		double maxValue = Double.NEGATIVE_INFINITY;
		checkState(!mdpip.getActionsFor(s).isEmpty());
		for ( final Action a : mdpip.getActionsFor(s) ) {
			double value = 0.0;
			for ( final Entry<State,Double> nextStateAndProb : mdpip.getPossibleStatesAndProbability(s, a, function) ) {
				value += nextStateAndProb.getValue() * function.getUtility(nextStateAndProb.getKey());
			}
			if ( value > maxValue || value == maxValue && maxA.name().compareTo(a.name()) < 0 ) {
				maxA = a;
				maxValue = value;
			}
		}
		return of(maxA, maxValue);
	}
}
