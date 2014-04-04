package mymdp.solver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.util.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class ProbLinearSolver {
	private static final Logger log = LogManager.getLogger(ProbLinearSolver.class);

	// FIXME: não devia estar hard-coded este path :(
	private static final SolveCaller solveCaller;
	static {
		try {
			solveCaller = new SolveCaller("amplcml\\");
		} catch (final IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public static enum SolutionType {
		MAXIMIZE,
		MINIMIZE,
		ANY_FEASIBLE;
	}

	/**
	 * Current mode of the solver
	 */
	private static SolutionType solution = SolutionType.MINIMIZE;

	private static int numberOfSolverCalls;

	public static void initializeCount() {
		numberOfSolverCalls = 0;
	}

	public static int getNumberOfSolverCalls() {
		return numberOfSolverCalls;
	}

	/**
	 * Gets the last solver call log.
	 * 
	 * @return a pair with the request and the result.
	 */
	public static Pair<String, String> getLastFullLog() {
		return Pair.of(solveCaller.getFileContents(), solveCaller.getLog());
	}

	/**
	 * Gets the current mode of the solver.
	 * 
	 * @return current mode.
	 */
	public static SolutionType getMode() {
		return solution;
	}

	/**
	 * Put the solver in maximize mode.
	 */
	public static void setMaximizing() {
		solution = SolutionType.MAXIMIZE;
	}

	/**
	 * Put the solver in the specified mode.
	 * 
	 * @param mode
	 *            the mode, must be not null.
	 */
	public static void setMode(final SolutionType mode) {
		checkNotNull(mode);
		solution = mode;
	}

	/**
	 * Put the solver in minimize mode.
	 */
	public static void setMinimizing() {
		solution = SolutionType.MINIMIZE;
	}

	/**
	 * Put the solver in feasibility-only mode.
	 */
	public static void setFeasibilityOnly() {
		solution = SolutionType.ANY_FEASIBLE;
	}

	/**
	 * Obtain the transition probabilities that minimize the expected value for
	 * all next states.
	 * 
	 * @param nextStates
	 *            map which keys are the next states and the values are
	 *            equations that define the probability of the transition
	 * @param initialReward
	 *            initial reward already obtained
	 * @param function
	 *            utility function of the actual step of the algorithm
	 * @param variables
	 *            names of the variables of the imprecise problem
	 * @param restrictions
	 *            restrictions over the variables
	 * 
	 * @return a map defining the next states and the probability to reach them
	 */
	public static Map<State, Double> minimizeExpectedValues(final Map<State, String> nextStates, final double initialReward,
			final UtilityFunction function, final Collection<String> variables, final Collection<String> restrictions) {
		return solve(SolutionType.MINIMIZE, nextStates, initialReward, function, variables, restrictions);
	}

	/**
	 * Obtain the transition probabilities that maximize the expected value for
	 * all next states.
	 * 
	 * @param nextStates
	 *            map which keys are the next states and the values are
	 *            equations that define the probability of the transition
	 * @param initialReward
	 *            initial reward already obtained
	 * @param function
	 *            utility function of the actual step of the algorithm
	 * @param variables
	 *            names of the variables of the imprecise problem
	 * @param restrictions
	 *            restrictions over the variables
	 * 
	 * @return a map defining the next states and the probability to reach them
	 */
	public static Map<State, Double> maximizeExpectedValues(final Map<State, String> nextStates, final double initialReward,
			final UtilityFunction function, final Collection<String> variables, final Collection<String> restrictions) {
		return solve(SolutionType.MAXIMIZE, nextStates, initialReward, function, variables, restrictions);
	}

	public static Map<State, Double> solve(final Map<State, String> nextStates, final double initialReward,
			final UtilityFunction function, final Collection<String> variables, final Collection<String> restrictions) {
		return solve(solution, nextStates, initialReward, function, variables, restrictions);
	}

	private static Map<State, Double> solve(final SolutionType type, final Map<State, String> nextStates, final double initialReward,
			final UtilityFunction function, final Collection<String> variables, final Collection<String> restrictions) {
		final Map<State, Double> result = new LinkedHashMap<>();

		// if there are no next states, return
		if (nextStates.isEmpty()) {
			return result;
		}

		// if all equations are just constant there is no need to call the
		// solver. Try to make them numbers.
		for (final Entry<State, String> pair : nextStates.entrySet()) {
			try {
				final double prob = Double.parseDouble(pair.getValue());
				result.put(pair.getKey(), prob);
			} catch (final NumberFormatException e) {
				// at least one probability is an equation, clear the result map
				// to call the solver
				result.clear();
				break;
			}
		}
		if (!result.isEmpty()) {
			// all probabilities were constants, return
			return result;
		}

		numberOfSolverCalls++;

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

		solveCaller.saveAMPLFile(obj, ImmutableSet.copyOf(variables), ImmutableSet.<String> of(), ImmutableList.copyOf(restrictions),
				type);
		try {
			solveCaller.callSolver();
		} catch (final RuntimeException e) {
			log.error("Solver failed!\nProblem:" + solveCaller.getFileContents() + "\nError log: " + solveCaller.getLog());
			log.catching(e);
			throw Throwables.propagate(e);
		}
		final Map<String, Double> currentValuesProb = solveCaller.getCurrentValuesProb();
		if (currentValuesProb.isEmpty()) {
			log.error("Solver failed!\nProblem:" + solveCaller.getFileContents() + "\nError log: " + solveCaller.getLog());
			throw new IllegalStateException();
		}

		for (final Entry<State, String> entry : nextStates.entrySet()) {
			final String constr = entry.getValue();
			int i = 0;
			final String[] values = constr.split("[\\*]");
			double value = currentValuesProb.containsKey(values[i]) ? currentValuesProb.get(values[i]).doubleValue() : Double
					.parseDouble(values[i]);
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
