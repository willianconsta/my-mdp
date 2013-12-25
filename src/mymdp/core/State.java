package mymdp.core;

/**
 * Interface for states.
 * 
 * @author Willian
 */
public interface State {

	/**
	 * A name for the state. States with the same names <i>should</i> be equal.
	 * 
	 * @return the state's name.
	 */
	String name();
}
