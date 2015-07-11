package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class AnyFeasibleProbabilityEvaluator
	extends
		AbstractProbabilityEvaluator
{
	@Override
	void setMode() {
		ProbLinearSolver.setFeasibilityOnly();
	}

}
