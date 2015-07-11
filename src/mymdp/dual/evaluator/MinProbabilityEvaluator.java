package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class MinProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	@Override
	void setMode() {
		ProbLinearSolver.setMinimizing();
	}
}