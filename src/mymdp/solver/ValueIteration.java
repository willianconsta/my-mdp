package mymdp.solver;

import mymdp.core.MDP;
import mymdp.core.UtilityFunction;

public interface ValueIteration {
	UtilityFunction solve(MDP mdp, double maxError);
}
