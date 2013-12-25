package mymdp.solver;

import static java.lang.Math.abs;
import static mymdp.solver.BellmanUtils.calculateUtilityIP;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValueIterationIPImpl implements ValueIterationIP {
	private static final Logger log = LogManager.getLogger(ValueIterationIPImpl.class);

	@Override
	public UtilityFunction solve(final MDPIP mdpip, final double maxError) {
		UtilityFunction oldFunction = new UtilityFunctionImpl(mdpip.getStates());
		UtilityFunction actualFunction;

		double actualError;
		do {
			actualFunction = new UtilityFunctionImpl(oldFunction);
			actualError = iteration(mdpip, oldFunction, actualFunction);
			oldFunction = actualFunction;
		} while (actualError > maxError);
		return actualFunction;
	}

	private double iteration(final MDPIP mdpip, final UtilityFunction oldFunction, final UtilityFunction actualFunction) {
		double maxVariation = 0;
		for (final State state : mdpip.getStates()) {
			final double oldUtility = oldFunction.getUtility(state);
			final double actualUtility = calculateUtilityIP(mdpip, state, oldFunction);
			log.trace("Value of state " + state + " = " + actualUtility);
			actualFunction.updateUtility(state, actualUtility);

			if (abs(actualUtility - oldUtility) > maxVariation) {
				maxVariation = abs(actualUtility - oldUtility);
			}
		}
		return maxVariation;
	}

}
