package mymdp.dual.evaluator;

import static com.google.common.base.Preconditions.checkNotNull;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.dual.EvaluatedProblemGenerator;
import mymdp.problem.MDPFileProblemReader;
import mymdp.solver.ProbLinearSolver;
import mymdp.solver.ProbLinearSolver.SolutionType;

abstract class AbstractProbabilityEvaluator
	implements
		ProbabilityEvaluator
{
	private final String fullFilename;

	AbstractProbabilityEvaluator(final String fullFilename) {
		this.fullFilename = checkNotNull(fullFilename);
	}

	@Override
	public final MDP evaluate(final MDPIP mdpip) {
		final SolutionType previousMode = ProbLinearSolver.getMode();
		try {
			setMode();
			final EvaluatedProblemGenerator generator = new EvaluatedProblemGenerator(mdpip);
			generator.writeToFile(fullFilename, mdpip.getStates().iterator().next(), mdpip.getStates());
			return MDPFileProblemReader.readFromFile(fullFilename);
		} finally {
			ProbLinearSolver.setMode(previousMode);
		}
	}

	abstract void setMode();
}
