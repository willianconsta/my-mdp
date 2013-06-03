package mymdp.dual.evaluator;

import mymdp.solver.ProbLinearSolver;

final class MaxProbabilityEvaluator extends AbstractProbabilityEvaluator {
    public MaxProbabilityEvaluator(final String fullFilename) {
	super(fullFilename);
    }

    @Override
    void setMode() {
	ProbLinearSolver.setMaximizing();
    }
}
