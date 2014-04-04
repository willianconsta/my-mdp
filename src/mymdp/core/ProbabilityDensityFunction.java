package mymdp.core;

/**
 * Represents a probability density function. Contains all the pairs
 * State-Action and their respective probability of transition.
 * 
 * @author Willian
 */
public interface ProbabilityDensityFunction {

	/**
	 * Return the transition probability for the current state and action.
	 * 
	 * @param current
	 *            the current state.
	 * @param action
	 *            the action.
	 * @return the transition probability for the pair.
	 * @throws NullPointerException
	 *             if any of the parameters are <code>null</code>.
	 */
	TransitionProbability getTransition(State current, Action action);
}
