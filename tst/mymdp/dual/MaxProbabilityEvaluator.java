package mymdp.dual;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.MDPFileProblemReaderImpl;
import mymdp.solver.ProbLinearSolver;
import mymdp.solver.ProbLinearSolver.SolutionType;

public class MaxProbabilityEvaluator implements ProbabilityEvaluator {
    private final String fullFilename;

    public MaxProbabilityEvaluator(final String fullFilename) {
	this.fullFilename = fullFilename;
    }

    @Override
    public MDP evaluate(final MDPIP mdpip) {
	final SolutionType previousMode = ProbLinearSolver.getMode();
	try {
	    ProbLinearSolver.setMaximizing();
	    final UtilityFunctionWithProbImpl result2 = (UtilityFunctionWithProbImpl) new ValueIterationProbImpl(
		    new UtilityFunctionWithProbImpl(mdpip.getStates(), 0.0))
		    .solve(mdpip, 0.1);
	    final PreciseProblemGenerator generator2 = new PreciseProblemGenerator(result2, mdpip, mdpip);
	    generator2
		    .writeToFile(fullFilename, mdpip.getStates().iterator().next(), mdpip.getStates());
	    return new MDPFileProblemReaderImpl().readFromFile(fullFilename);
	} finally {
	    ProbLinearSolver.setMode(previousMode);
	}
    }

}
