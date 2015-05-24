package mymdp.core;

import java.util.Set;

public interface MDPIP
{
	Set<State> getStates();

	Set<Action> getAllActions();

	Set<Action> getActionsFor(State state);

	TransitionProbability getPossibleStatesAndProbability(State initialState, Action action, UtilityFunction function);

	// to be added
	// ProbabilityDensityFunction getProbabilityFunction();

	double getRewardFor(State state);

	double getDiscountFactor();
}
