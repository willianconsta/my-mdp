package mymdp.core;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

public interface ProbabilityFunction extends Iterable<Entry<State, Double>> {

	Double getProbabilityFor(State state);

	/**
	 * Tells whether this probability density function is empty, i.e., does not
	 * have probabilities for any states.
	 * 
	 * @return <code>true</code> if is empty, <code>false</code> c.c.
	 */
	boolean isEmpty();

	/**
	 * Instance factory for probability density functions.
	 * 
	 * @author Willian
	 */
	public class Instance {
		private static ProbabilityFunction emptyInstance = new SimpleProbabiliyFunction(Collections.<State, Double> emptyMap());

		Instance() {
			throw new UnsupportedOperationException();
		}

		/**
		 * Creates a simple probability density function.
		 * 
		 * @param distributions
		 * @return
		 */
		public static ProbabilityFunction createSimple(final Map<State, Double> distributions) {
			if (distributions.isEmpty()) {
				return empty();
			}

			return new SimpleProbabiliyFunction(distributions);
		}

		public static ProbabilityFunction empty() {
			return emptyInstance;
		}
	}
}
