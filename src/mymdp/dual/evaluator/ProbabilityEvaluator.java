package mymdp.dual.evaluator;

import mymdp.core.MDP;
import mymdp.core.MDPIP;

public interface ProbabilityEvaluator
{
	MDP evaluate(MDPIP mdpip);
}
