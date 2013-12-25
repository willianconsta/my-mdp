package mymdp.core;

import java.util.Set;

public interface UtilityFunction {
	double getUtility(State state);

	void updateUtility(final State state, final double utility);

	Set<State> getStates();
}
