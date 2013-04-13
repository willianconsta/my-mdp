package mymdp.solver;

import static java.lang.Math.abs;
import static mymdp.solver.BellmanUtils.calculateUtility;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

public class ValueIterationImpl implements ValueIteration {

    @Override
    public UtilityFunction solve(final MDP mdp, final double maxError) {
	UtilityFunction oldFunction = new UtilityFunctionImpl(mdp.getStates());
	UtilityFunction actualFunction;

	double actualError;
	do {
	    System.out.println();
	    System.out.println();
	    System.out.println();
	    actualFunction = new UtilityFunctionImpl(oldFunction);
	    actualError = iteration(mdp, oldFunction, actualFunction);
	    oldFunction = actualFunction;
	} while (actualError > maxError);
	return actualFunction;
    }

    private double iteration(final MDP mdp, final UtilityFunction oldFunction,
	    final UtilityFunction actualFunction) {
	double maxVariation = 0;
	for (final State state : mdp.getStates()) {
	    final double oldUtility = oldFunction.getUtility(state);
	    final double actualUtility = calculateUtility(mdp, state,
		    oldFunction);
	    System.out.println("Value of state " + state + " = "
		    + actualUtility);
	    actualFunction.updateUtility(state, actualUtility);

	    if (abs(actualUtility - oldUtility) > maxVariation) {
		maxVariation = abs(actualUtility - oldUtility);
	    }
	}
	return maxVariation;
    }

}
