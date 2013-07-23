package mymdp.core;

import java.util.Set;

public interface MDP {
    Set<State> getStates();

    Set<Action> getAllActions();

    Set<Action> getActionsFor(State state);

    ProbabilityFunction getPossibleStatesAndProbability(State initialState, Action action);

    double getRewardFor(State state);

    double getDiscountFactor();
}
