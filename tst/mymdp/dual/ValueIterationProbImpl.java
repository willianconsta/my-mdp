package mymdp.dual;

import static java.lang.Math.abs;

import java.util.Map;
import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.solver.ValueIterationIP;
import mymdp.util.Trio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ValueIterationProbImpl implements ValueIterationIP {
    private static final Logger log = LogManager.getLogger(ValueIterationProbImpl.class);

    private final UtilityFunction initialValues;

    public ValueIterationProbImpl(final UtilityFunction initialValues) {
	this.initialValues = initialValues;
    }

    @Override
    public UtilityFunction solve(final MDPIP mdpip, final double maxError) {
	UtilityFunctionWithProbImpl oldFunction = new UtilityFunctionWithProbImpl(initialValues);
	UtilityFunctionWithProbImpl actualFunction;

	double actualError;
	do {
	    actualFunction = new UtilityFunctionWithProbImpl(oldFunction);
	    actualError = iteration(mdpip, oldFunction, actualFunction);
	    log.debug("Actual error: " + actualError);
	    oldFunction = actualFunction;
	} while (actualError > maxError);
	return actualFunction;
    }

    private double iteration(final MDPIP mdpip, final UtilityFunctionWithProbImpl oldFunction,
	    final UtilityFunctionWithProbImpl actualFunction) {
	log.debug("Starting iteration");
	log.debug("Actual function = " + actualFunction);
	double maxVariation = 0;
	for (final State state : mdpip.getStates()) {
	    final double oldUtility = oldFunction.getUtility(state);
	    final Trio<Double, Action, Map<State, Double>> trio =
		    calculateUtilityIP(mdpip, state, oldFunction);
	    final double actualUtility = trio.first;
	    log.trace("Value of state " + state + " = " + actualUtility);
	    actualFunction.updateUtility(state, actualUtility, trio.second, trio.third);

	    if (abs(actualUtility - oldUtility) > maxVariation) {
		maxVariation = abs(actualUtility - oldUtility);
	    }
	    log.trace("Max variation = " + maxVariation);
	}
	log.debug("End of iteration");
	return maxVariation;
    }

    private static Trio<Double, Action, Map<State, Double>> calculateUtilityIP(final MDPIP mdpip, final State state,
	    final UtilityFunctionWithProbImpl oldFunction) {
	Map<State, Double> probOfMaxAction = null;
	Action maxAction = null;
	double maxUtilityOfActions = 0;
	if (!mdpip.getActionsFor(state).isEmpty()) {
	    maxUtilityOfActions = Double.NEGATIVE_INFINITY;
	}
	for (final Action action : mdpip.getActionsFor(state)) {
	    double utilityOfAction = 0;
	    final Map<State, Double> possibleStatesAndProbability = mdpip.getPossibleStatesAndProbability(state, action, oldFunction);
	    for (final Entry<State, Double> nextStateAndProb : possibleStatesAndProbability.entrySet()) {
		utilityOfAction += nextStateAndProb.getValue().doubleValue() * oldFunction.getUtility(nextStateAndProb.getKey());
	    }
	    if (utilityOfAction > maxUtilityOfActions) {
		maxAction = action;
		maxUtilityOfActions = utilityOfAction;
		probOfMaxAction = possibleStatesAndProbability;
	    }
	}
	if (maxUtilityOfActions == Double.NEGATIVE_INFINITY) {
	    maxUtilityOfActions = 0;
	}
	return Trio.newTrio(mdpip.getRewardFor(state) + mdpip.getDiscountFactor() * maxUtilityOfActions, maxAction, probOfMaxAction);
    }
}
