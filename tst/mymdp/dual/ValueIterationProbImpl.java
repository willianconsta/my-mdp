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
	    log.info("Actual error: " + actualError);
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
	    final Trio<Double, Action, Map<State, Double>> trio = calculateUtilityIP(mdpip, state, oldFunction);
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

    public static Trio<Double, Action, Map<State, Double>> calculateUtilityIP(final MDPIP mdpip, final State state,
	    final UtilityFunctionWithProbImpl oldFunction) {
	Map<State, Double> probOfMaxAction = null;
	Action maxAction = null;
	double maxUtilityOfActions = 0;
	log.debug("There are " + mdpip.getActionsFor(state).size() + " possible actions.");
	if (!mdpip.getActionsFor(state).isEmpty()) {
	    log.trace("Initializing maxUtility with negative infinity");
	    maxUtilityOfActions = Double.NEGATIVE_INFINITY;
	}
	for (final Action action : mdpip.getActionsFor(state)) {
	    double utilityOfAction = 0;
	    String utilityCalculus = "0 ";
	    final Map<State, Double> possibleStatesAndProbability = mdpip.getPossibleStatesAndProbability(state, action, oldFunction);
	    for (final Entry<State, Double> nextStateAndProb : possibleStatesAndProbability.entrySet()) {
		utilityCalculus += " + " + nextStateAndProb.getValue().toString() + " * "
			+ oldFunction.getUtility(nextStateAndProb.getKey());
		utilityOfAction += nextStateAndProb.getValue().doubleValue() * oldFunction.getUtility(nextStateAndProb.getKey());
	    }
	    log.trace("Utility of action " + action + " in state " + state + " = " + utilityCalculus);
	    if (utilityOfAction > maxUtilityOfActions) {
		log.trace("New max utility " + utilityOfAction);
		maxAction = action;
		maxUtilityOfActions = utilityOfAction;
		probOfMaxAction = possibleStatesAndProbability;
	    }
	}
	if (maxUtilityOfActions == Double.NEGATIVE_INFINITY) {
	    maxUtilityOfActions = 0;
	}
	log.debug("Max utility of state " + state + " is " + maxUtilityOfActions);
	return Trio.newTrio(mdpip.getRewardFor(state) + mdpip.getDiscountFactor() * maxUtilityOfActions, maxAction, probOfMaxAction);
    }
}
