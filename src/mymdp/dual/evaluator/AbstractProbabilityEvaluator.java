package mymdp.dual.evaluator;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.UtilityFunction;
import mymdp.dual.DelegateMDP;
import mymdp.solver.ProbLinearSolver;
import mymdp.solver.ProbLinearSolver.SolutionType;

abstract class AbstractProbabilityEvaluator
	implements
		ProbabilityEvaluator
{
	@Override
	public final MDP evaluate(final MDPIP mdpip, final UtilityFunction function) {
		final SolutionType previousMode = ProbLinearSolver.getMode();
		try {
			setMode();
			return new DelegateMDP(mdpip, function);
		} finally {
			ProbLinearSolver.setMode(previousMode);
		}
	}

	abstract void setMode();
}
