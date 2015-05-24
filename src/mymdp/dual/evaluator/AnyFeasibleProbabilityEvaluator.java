package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class AnyFeasibleProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	public AnyFeasibleProbabilityEvaluator(final String fullFilename) {
		super(fullFilename);
	}

	@Override
	void setMode() {
		ProbLinearSolver.setFeasibilityOnly();
	}

}
