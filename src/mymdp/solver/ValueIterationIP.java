package mymdp.solver;

import mymdp.core.MDPIP;
import mymdp.core.UtilityFunction;

public interface ValueIterationIP
{
	UtilityFunction solve(MDPIP mdpip, double maxError);
}
