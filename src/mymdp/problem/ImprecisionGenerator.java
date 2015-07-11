package mymdp.problem;

import com.google.common.collect.Range;

public interface ImprecisionGenerator
{
	/**
	 * Generates a range of the given probability for a trio [initial state, action, next state].
	 * 
	 * @param initialState
	 * @param action
	 * @param nextState
	 * @param actualProbability
	 * @return
	 */
	Range<Double> generateRange(String initialState, String action, String nextState, double actualProbability);
}