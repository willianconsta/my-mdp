package mymdp.solver;

import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;

/**
 * Policy evaluator for precise problems.
 * 
 * @author Willian
 */
interface PolicyEvaluator {

	/**
	 * Evaluates the given policy for a MDP.
	 * 
	 * @param policy
	 *            the policy for evaluation
	 * @param function
	 * @param mdp
	 * @return
	 */
	UtilityFunction policyEvaluation(Policy policy, UtilityFunction function, MDP mdp);
}
