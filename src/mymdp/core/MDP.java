package mymdp.core;

import java.util.Set;

public interface MDP
{
	Set<State> getStates();

	Set<Action> getAllActions();

	Set<Action> getActionsFor(State state);

	TransitionProbability getPossibleStatesAndProbability(State initialState, Action action);

	double getRewardFor(State state);

	double getDiscountFactor();
}
