package mymdp.dual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;

public class DualLinearProgrammingSolver
	implements
		ProblemSolver
{
	private static final Logger log = LogManager.getLogger(DualLinearProgrammingSolver.class);
	private static final String PROBLEMS_DIR = "precise_problems";

	private final String filename;
	private final double maxRelaxation;
	private final SolveCaller solveCaller;

	public DualLinearProgrammingSolver(final String filename, final double maxRelaxation) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
		try {
			this.solveCaller = new SolveCaller("amplcml\\");
		} catch ( final IOException e ) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public SolutionReport solve() {
		log.info("Current Problem: " + filename);
		final ImprecisionGeneratorImpl imprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP problem = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				imprecisionGenerator);

		final Set<String> variables = new HashSet<>();
		final Set<String> probabilityVariables = new HashSet<>();
		final List<String> objective = new ArrayList<>();
		final List<String> constraints = new ArrayList<>();

		variables.add("discount");
		constraints.add("discount = " + problem.getDiscountFactor());

		for ( final State s : problem.getStates() ) {
			final String valueVariable = 'v' + s.name().replace("-", "");
			final String rewardVariable = 'r' + s.name().replace("-", "");

			variables.add(valueVariable);
			variables.add(rewardVariable);

			objective.add(valueVariable);

			constraints.add(rewardVariable + " = " + problem.getRewardFor(s));

			final Multimap<Action,State> nextStatesByAction = ProbabilityRestrictionUtils.nextStates(problem, s);

			for ( final Action a : problem.getActionsFor(s) ) {
				String actionConstraint = valueVariable + " >= " + rewardVariable + " + discount * ( ";

				final List<String> allPossible = new ArrayList<>();
				for ( final State nextState : nextStatesByAction.get(a) ) {
					final String transitionVariable = ProbabilityRestrictionUtils.transitionVariable(problem, s, a, nextState);
					if ( Doubles.tryParse(transitionVariable) == null ) {
						probabilityVariables.add(transitionVariable);
					}
					allPossible.add(transitionVariable + " * v" + nextState.name().replace("-", ""));
				}
				actionConstraint += Joiner.on(" + ").join(allPossible);

				actionConstraint += " )";
				constraints.add(actionConstraint);
			}
		}

		probabilityVariables.addAll(ProbabilityRestrictionUtils.getAllVars(problem));
		constraints.addAll(ProbabilityRestrictionUtils.readProbabilityRestrictions(problem).getSecond());

		try {
			solveCaller.saveAMPLFile(objective, probabilityVariables, variables, constraints, SolutionType.MINIMIZE);
			solveCaller.callSolver();

			log.info(solveCaller.getCurrentValuesProb());
		} catch ( final RuntimeException e ) {
			log.error("Problem occurred while solving. Log: " + solveCaller.getLog());
			log.error("File Contents: " + solveCaller.getFileContents());
			log.error("Error", e);
		}

		System.out.println(solveCaller.getFileContents());
		return null;
	}

	public static void main(final String[] args) {
		new DualLinearProgrammingSolver("navigation01.net", 0.05).solve();
	}
}
