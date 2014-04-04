package mymdp.solver;

import mymdp.core.MDP;
import mymdp.core.SolutionReport;
import mymdp.dual.ProblemSolver;
import mymdp.problem.MDPFileProblemReader;

public class FileMDPDualLinearProgrammingSolver implements ProblemSolver {
	private final MDPDualLinearProgrammingSolver delegate;

	public FileMDPDualLinearProgrammingSolver(final String filename) {
		final MDP problem = MDPFileProblemReader.readFromFile(filename);
		this.delegate = new MDPDualLinearProgrammingSolver(filename, problem);
	}

	@Override
	public SolutionReport solve() {
		return delegate.solve();
	}
}
