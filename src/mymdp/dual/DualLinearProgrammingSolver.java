package mymdp.dual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Doubles;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.SolutionReport;
import mymdp.core.State;
import mymdp.problem.MDPIPFileProblemReader;
import mymdp.problem.ProbabilityRestrictionUtils;
import mymdp.solver.ProbLinearSolver.SolutionType;
import mymdp.solver.SolveCaller;

public class DualLinearProgrammingSolver
	implements
		ProblemSolver<MDPIP,Void>
{
	private static final Logger log = LogManager.getLogger(DualLinearProgrammingSolver.class);

	private final SolveCaller solveCaller;

	public DualLinearProgrammingSolver() {
		try {
			this.solveCaller = new SolveCaller("amplcml\\");
		} catch ( final IOException e ) {
			throw Throwables.propagate(e);
		}
	}

	@Override
	public SolutionReport solve(final Problem<MDPIP,Void> problem) {
		final MDPIP mdpip = problem.getModel();

		final Set<String> variables = new HashSet<>();
		final Set<String> probabilityVariables = new HashSet<>();
		final List<String> objective = new ArrayList<>();
		final List<String> constraints = new ArrayList<>();

		variables.add("discount");
		constraints.add("discount = " + mdpip.getDiscountFactor());

		for ( final State s : mdpip.getStates() ) {
			final String valueVariable = 'v' + s.name().replace("-", "");
			final String rewardVariable = 'r' + s.name().replace("-", "");

			variables.add(valueVariable);
			variables.add(rewardVariable);

			objective.add(valueVariable);

			constraints.add(rewardVariable + " = " + mdpip.getRewardFor(s));

			final Multimap<Action,State> nextStatesByAction = ProbabilityRestrictionUtils.nextStates(mdpip, s);

			for ( final Action a : mdpip.getActionsFor(s) ) {
				String actionConstraint = valueVariable + " >= " + rewardVariable + " + discount * ( ";

				final List<String> allPossible = new ArrayList<>();
				for ( final State nextState : nextStatesByAction.get(a) ) {
					final String transitionVariable = ProbabilityRestrictionUtils.transitionVariable(mdpip, s, a, nextState);
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

		probabilityVariables.addAll(ProbabilityRestrictionUtils.getAllVars(mdpip));
		constraints.addAll(ProbabilityRestrictionUtils.readProbabilityRestrictions(mdpip).getSecond());

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
		new DualLinearProgrammingSolver().solve(new Problem<MDPIP,Void>() {
			@Override
			public String getName() {
				return "navigation01";
			}

			@Override
			public MDPIP getModel() {
				return MDPIPFileProblemReader.readFromFile("precise_problems\\navigation01.net");
			}

			@Override
			public Void getComplement() {
				return null;
			}
		});
	}
}
