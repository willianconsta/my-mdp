package mymdp.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import mymdp.core.State;
import mymdp.core.UtilityFunction;

/**
 * Calculates the distance between two {@link UtilityFunction}s.
 * 
 * @author Willian
 */
public class UtilityFunctionDistanceEvaluator
{

	/**
	 * Calculates the distance between two {@link UtilityFunction}s. The {@link UtilityFunction}s must be comparable, ou seja, the set of states of the two must
	 * be equal.
	 * 
	 * @param function1
	 *            the first utility function
	 * @param function2
	 *            the second utility function
	 * @return the maximal distance between the values of a given state.
	 */
	public static double distanceBetween(final UtilityFunction function1, final UtilityFunction function2) {
		checkArgument(function1.getStates().equals(function2.getStates()), "Uncomparable utility functions, different states. Function1 = "
				+ function1.getStates() + ", Function2 = " + function2.getStates());
		if ( function1.getStates().isEmpty() ) {
			return 0.0;
		}

		double maxDistance = Double.NEGATIVE_INFINITY;
		for ( final State s : function1.getStates() ) {
			maxDistance = max(maxDistance, abs(function1.getUtility(s) - function2.getUtility(s)));
		}
		return maxDistance;
	}
}
