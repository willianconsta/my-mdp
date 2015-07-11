package mymdp.dual;

import mymdp.core.SolutionReport;

public interface ProblemSolver<M, C>
{
	SolutionReport solve(Problem<M,C> problem);
}
