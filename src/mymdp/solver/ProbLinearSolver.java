package mymdp.solver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mymdp.core.State;
import mymdp.core.UtilityFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ProbLinearSolver {
    private static final Logger log = LogManager.getLogger(ProbLinearSolver.class);

    public static Map<State, Double> minimizeExpectedValues(final Map<State, String> nextStates, final double initialReward,
	    final UtilityFunction function, final Collection<String> variables, final Collection<String> restrictions) {
	final Map<State, Double> result = new HashMap<>();
	if (nextStates.isEmpty()) {
	    return result;
	}

	for (final Entry<State, String> pair : nextStates.entrySet()) {
	    try {
		final double prob = Double.parseDouble(pair.getValue());
		result.put(pair.getKey(), prob);
	    } catch (final NumberFormatException e) {
		result.clear();
		break;
	    }
	}
	if (!result.isEmpty()) {
	    return result;
	}

	final List<String> obj = Lists.newArrayList();
	obj.add(String.valueOf(initialReward));

	for (final Entry<State, String> entry : nextStates.entrySet()) {
	    if (entry.getValue().contains("*0.0*") || entry.getValue().endsWith("*0.0")) {
		log.debug("Escaping from zero probability. Restriction = " + entry.getValue());
		continue;
	    }

	    final double value = function.getUtility(entry.getKey());
	    checkState(value != Double.POSITIVE_INFINITY);
	    if (value == Double.NEGATIVE_INFINITY) {
		log.debug("Escaping from useless utility. State = " + entry.getKey() + ", function = " + function);
		continue;
	    }
	    if (Math.abs(value) < 0.00001) {
		log.debug("Escaping from zero utility. State = " + entry.getKey() + ", function = " + function);
		continue;
	    }
	    obj.add(entry.getValue() + "*" + value);
	}

	if (obj.size() == 1) {
	    for (final State state : nextStates.keySet()) {
		result.put(state, 1.0 / nextStates.size());
	    }
	    return result;
	}

	final SolveCaller solveCaller = new SolveCaller("D:\\Programação\\Mestrado\\amplcml\\");
	solveCaller.setFileName("prob1.txt");
	solveCaller.salveAMPLFile(obj, ImmutableList.copyOf(variables), ImmutableList.copyOf(restrictions), false);
	try {
	    solveCaller.callSolver();
	} catch (final RuntimeException e) {
	    log.error(solveCaller.getLog());
	    log.catching(e);
	}
	final Map<String, Float> currentValuesProb = solveCaller.getCurrentValuesProb();
	if (currentValuesProb.isEmpty()) {
	    log.error(solveCaller.getLog());
	    throw new IllegalStateException();
	}

	for (final Entry<State, String> entry : nextStates.entrySet()) {
	    final String constr = entry.getValue();
	    int i = 0;
	    final String[] values = constr.split("[\\*]");
	    Double value = currentValuesProb.containsKey(values[i]) ? currentValuesProb.get(values[i]) : Double.parseDouble(values[i]);
	    final String[] ops = constr.split("[^\\*]");
	    for (final String op : ops) {
		if (op.equals("*")) {
		    final double val1 = currentValuesProb.containsKey(values[i]) ? currentValuesProb.get(values[i]) : Double
			    .parseDouble(values[i]);
		    value *= val1;
		    i++;
		}
	    }
	    checkNotNull(value);
	    result.put(entry.getKey(), value);
	}
	return result;
    }
}
