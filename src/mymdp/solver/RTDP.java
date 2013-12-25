package mymdp.solver;

import java.util.Set;

import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;

public interface RTDP {
	UtilityFunction solve(MDP mdp, Set<State> initials, Set<State> goals,
			long maxDepth, UtilityFunction initialValues);

	UtilityFunction solve(MDP mdp, Set<State> initials, Set<State> goals,
			UtilityFunction initialValues);

	UtilityFunction solve(MDP mdp, Set<State> initials,
			UtilityFunction initialValues);

	UtilityFunction solve(MDP mdp, Set<State> initials, long maxDepth);

	UtilityFunction solve(MDP mdp, Set<State> initials);

	UtilityFunction solve(MDP mdp);

	public interface ConvergencyCriteria {
		boolean hasConverged();
	}
}
