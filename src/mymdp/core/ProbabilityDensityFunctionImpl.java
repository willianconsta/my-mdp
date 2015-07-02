package mymdp.core;

import com.google.common.collect.Table;

public class ProbabilityDensityFunctionImpl
	implements
		ProbabilityDensityFunction
{
	private Table<State,Action,TransitionProbability> transitions;

	@Override
	public TransitionProbability getTransition(final State current, final Action action) {
		return transitions.get(current, action);
	}

}
