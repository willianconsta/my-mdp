package mymdp.solver;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.PolicyImpl;
import mymdp.core.SolutionReport;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.dual.DualLinearProgrammingSolver;
import mymdp.dual.ProblemSolver;
import mymdp.solver.ProbLinearSolver.SolutionType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

public class MDPDualLinearProgrammingSolver
	implements
		ProblemSolver
{
	private static final Logger log = LogManager.getLogger(DualLinearProgrammingSolver.class);

	private final String problemName;
	private final MDP problem;
	private final SolveCaller solveCaller;

	public MDPDualLinearProgrammingSolver(final String problemName, final MDP problem) {
		this.problemName = problemName;
		this.problem = checkNotNull(problem);
		try {
			this.solveCaller = new SolveCaller("amplcml\\");
		} catch ( final IOException e ) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public SolutionReport solve() {
		log.info("Current Problem: " + problemName);

		final Set<String> variables = new HashSet<>();
		final Set<String> probabilityVariables = new HashSet<>();
		final List<String> objective = new ArrayList<>();
		final List<String> constraints = new ArrayList<>();

		variables.add("discount");
		constraints.add("discount = " + problem.getDiscountFactor());

		for ( final State s : problem.getStates() ) {
			final String valueVariable = stateToValueVariableName(s);
			final String rewardVariable = stateToRewardVariableName(s);

			variables.add(valueVariable);
			variables.add(rewardVariable);

			objective.add(valueVariable);

			constraints.add(rewardVariable + " = " + problem.getRewardFor(s));

			for ( final Action a : problem.getActionsFor(s) ) {
				String actionConstraint = valueVariable + " >= " + rewardVariable + " + discount * ( ";

				final List<String> allPossible = new ArrayList<>();
				for ( final Entry<State,Double> nextState : problem.getPossibleStatesAndProbability(s, a) ) {
					allPossible.add(nextState.getValue() + " * " + stateToValueVariableName(nextState.getKey()));
				}
				actionConstraint += Joiner.on(" + ").join(allPossible);

				actionConstraint += " )";
				constraints.add(actionConstraint);
			}
		}

		try {
			solveCaller.saveAMPLFile(objective, probabilityVariables, variables, constraints, SolutionType.MINIMIZE);
			solveCaller.callSolver();

			log.info(new TreeMap<>(solveCaller.getCurrentValuesProb()));
		} catch ( final RuntimeException e ) {
			log.error("Problem occurred while solving. Log: " + solveCaller.getLog());
			log.error("File Contents: " + solveCaller.getFileContents());
			throw e;
		}

		return new SolutionReport(new PolicyImpl(problem), convertToValueFunction(problem, solveCaller.getCurrentValuesProb()));
	}

	private static String stateToValueVariableName(final State state) {
		return "v" + state.name().replaceAll("-", "");
	}

	private static String stateToRewardVariableName(final State state) {
		return "r" + state.name().replaceAll("-", "");
	}

	private static UtilityFunction convertToValueFunction(final MDP mdp, final Map<String,Double> result) {
		final UtilityFunction valueFunction = new UtilityFunctionImpl(mdp.getStates());
		for ( final State state : mdp.getStates() ) {
			valueFunction.updateUtility(state, result.get(stateToValueVariableName(state)));
		}
		return valueFunction;
	}
}
