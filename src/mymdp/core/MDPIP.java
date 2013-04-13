package mymdp.core;

import java.util.Map;
import java.util.Set;

public interface MDPIP {
    Set<State> getStates();

    Set<Action> getActionsFor(State state);

    Map<State, Double> getPossibleStatesAndProbability(State initialState,
	    Action action, UtilityFunction function);

    double getRewardFor(State state);

    double getDiscountFactor();
}
