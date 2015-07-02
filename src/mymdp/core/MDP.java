package mymdp.core;

import java.util.Set;

public interface MDP
{
	Set<State> getStates();

	Set<Action> getAllActions();

	/**
	 * Returns the actions the agent can execute in the given state. Should never be empty.
	 * 
	 * @param state
	 *            the current state
	 * @return all actions the agent can execute in the state.
	 */
	Set<Action> getActionsFor(State state);

	TransitionProbability getPossibleStatesAndProbability(State initialState, Action action);

	double getRewardFor(State state);

	double getDiscountFactor();
}
