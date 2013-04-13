package mymdp.solver;

import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;

public interface PolicyEvaluator {

    UtilityFunction policyEvaluation(Policy policy, UtilityFunction function,
	    MDP mdp);

}
