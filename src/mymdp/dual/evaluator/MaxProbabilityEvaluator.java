package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class MaxProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	@Override
	void setMode() {
		ProbLinearSolver.setMaximizing();
	}
}
