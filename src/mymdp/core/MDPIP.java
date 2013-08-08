package mymdp.core;

import java.util.Set;

public interface MDPIP {
    Set<State> getStates();

    Set<Action> getAllActions();

    Set<Action> getActionsFor(State state);

    ProbabilityFunction getPossibleStatesAndProbability(State initialState, Action action, UtilityFunction function);

    double getRewardFor(State state);

    double getDiscountFactor();
}
