package mymdp.dual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.SolutionReport;
import mymdp.core.State;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.problem.ProbabilityRestrictionUtils;
import mymdp.solver.ProbLinearSolver.SolutionType;
import mymdp.solver.SolveCaller;
import mymdp.util.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

public class DualLinearProgrammingSolver implements ProblemSolver {
	private static final Logger log = LogManager.getLogger(DualLinearProgrammingSolver.class);
	private static final String PROBLEMS_DIR = "precise_problems";
	private static final String SOLUTIONS_DIR = "solutions";

	private final String filename;
	private final int maxRelaxation;
	private final SolveCaller solveCaller;

	public DualLinearProgrammingSolver(final String filename, final int maxRelaxation) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
		try {
			this.solveCaller = new SolveCaller("amplcml\\");
		} catch (final IOException e) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public SolutionReport solve() {
		log.info("Current Problem: " + filename);
		final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP problem = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				initialProblemImprecisionGenerator);

		final List<String> variables = new ArrayList<>();
		final List<String> objective = new ArrayList<>();
		final List<String> constraints = new ArrayList<>();

		variables.add("discount");
		constraints.add("discount = " + problem.getDiscountFactor());

		for (final State s : problem.getStates()) {
			final String valueVariable = 'v' + s.name();
			final String rewardVariable = 'r' + s.name();

			variables.add(valueVariable);
			variables.add(rewardVariable);

			objective.add(valueVariable);

			constraints.add(rewardVariable + " = " + problem.getRewardFor(s));

			for (final Action a : problem.getActionsFor(s)) {
				String actionConstraint = valueVariable + " >= " + rewardVariable + " + discount * ( ";

				problem.getPossibleStatesAndProbability(initialState, action, function)
				
				actionConstraint += " )";
				constraints.add(actionConstraint);
			}
		}

		solveCaller.saveAMPLFile(objective, variables, constraints, SolutionType.MINIMIZE);

		return null;
	}
}
