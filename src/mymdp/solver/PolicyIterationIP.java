package mymdp.solver;

import mymdp.core.MDPIP;
import mymdp.core.Policy;

public interface PolicyIterationIP {

    Policy solve(MDPIP mdpip);

}