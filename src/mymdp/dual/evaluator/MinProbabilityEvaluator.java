package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class MinProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	MinProbabilityEvaluator(final String fullFilename) {
		super(fullFilename);
	}

	@Override
	void setMode() {
		ProbLinearSolver.setMinimizing();
	}
}