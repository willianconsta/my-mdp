package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class MinProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	public MinProbabilityEvaluator(final String fullFilename) {
		super(fullFilename);
	}

	@Override
	void setMode() {
		ProbLinearSolver.setMinimizing();
	}
}