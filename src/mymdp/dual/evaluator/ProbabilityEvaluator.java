package mymdp.dual.evaluator;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.UtilityFunction;

public interface ProbabilityEvaluator
{
	MDP evaluate(MDPIP mdpip, UtilityFunction function);
}
