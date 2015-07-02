package mymdp.core;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Holds the transition probability for a pair State-Action to the next states.
 * 
 * @author Willian
 * 
 */
public interface TransitionProbability
	extends
		Iterable<Entry<State,Double>>
{

	/**
	 * Returns the probability to go to {@code nextState}.
	 * 
	 * @param nextState
	 *            the next state.
	 * @return the probability value in [0..1].
	 * @throws NullPointerException
	 *             if {@code nextState} is <code>null</code>.
	 */
	double getProbabilityFor(State nextState);

	/**
	 * Returns the current state in the point of view of this transition.
	 * 
	 * @return the current state.
	 */
	State getCurrentState();

	/**
	 * Returns the action of this transition.
	 * 
	 * @return the action.
	 */
	Action getAction();

	/**
	 * Tells whether this probability density function is empty, i.e., does not have probabilities for any states.
	 * 
	 * @return <code>true</code> if is empty, <code>false</code> c.c.
	 */
	boolean isEmpty();

	/**
	 * Instance factory for transition probability.
	 * 
	 * @author Willian
	 */
	static TransitionProbability createSimple(final State current, final Action action, final Map<State,Double> distributions) {
		return SimpleTransitionFunction.create(current, action, distributions);
	}

	static TransitionProbability empty(final State current, final Action action) {
		return SimpleTransitionFunction.empty(current, action);
	}
}
