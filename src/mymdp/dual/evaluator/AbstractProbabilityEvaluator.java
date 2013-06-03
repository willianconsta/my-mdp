package mymdp.dual.evaluator;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.dual.PreciseProblemGenerator;
import mymdp.dual.ValueIterationProbImpl;
import mymdp.problem.MDPFileProblemReaderImpl;
import mymdp.solver.ProbLinearSolver;
import mymdp.solver.ProbLinearSolver.SolutionType;

abstract class AbstractProbabilityEvaluator implements ProbabilityEvaluator {
    private final String fullFilename;

    public AbstractProbabilityEvaluator(final String fullFilename) {
	this.fullFilename = fullFilename;
    }

    @Override
    public final MDP evaluate(final MDPIP mdpip) {
	final SolutionType previousMode = ProbLinearSolver.getMode();
	try {
	    setMode();
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

    abstract void setMode();
}
