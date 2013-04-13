package mymdp.solver;

import mymdp.core.MDP;
import mymdp.core.Policy;

public interface PolicyIteration {
    Policy solve(MDP mdp);
}
