package mymdp.core;

/**
 * Interface for policies. A policy defines which action to take in a given state.
 * 
 * @author Willian
 */
public interface Policy
{

	/**
	 * Gets the action defined for the state.
	 * 
	 * @param state
	 *            a state.
	 * @return the action for the state.
	 */
	Action getActionFor(State state);

	/**
	 * Gets the action defined for the state with a given name.
	 * 
	 * @param stateName
	 *            a state name.
	 * @return the action for the state.
	 */
	Action getActionFor(String stateName);

	/**
	 * Updates the policy associating the action with the state.
	 * 
	 * @param state
	 *            the state
	 * @param action
	 *            the action
	 */
	void updatePolicy(State state, Action action);
}
