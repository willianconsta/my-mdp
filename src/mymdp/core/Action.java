package mymdp.core;

/**
 * Interface for actions.
 * 
 * @author Willian
 */
public interface Action {

    /**
     * Returns whether this action is applyable or not to a given state.
     * 
     * @param state
     *            a given state
     * @return <code>true</code> if this action is applyable, <code>false</code>
     *         if not.
     */
    boolean isApplyableTo(State state);
}
