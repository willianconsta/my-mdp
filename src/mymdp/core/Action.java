package mymdp.core;

/**
 * Interface for actions.
 * 
 * @author Willian
 */
public interface Action {

    /**
     * Returns whether this action is applicable or not to a given state.
     * 
     * @param state
     *            a given state
     * @return <code>true</code> if this action is applicable,
     *         <code>false</code> if not.
     */
    boolean isApplicableTo(State state);

    /**
     * A name for the action. Actions with the same names <i>should</i> be
     * equal.
     * 
     * @return the action's name.
     */
    String name();
}
