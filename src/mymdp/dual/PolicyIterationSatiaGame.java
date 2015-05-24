package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationSatia;
import mymdp.solver.ProbLinearSolver;

public class PolicyIterationSatiaGame
	implements
		ProblemSolver
{
	private static final Logger log = LogManager.getLogger(PolicyIterationSatiaGame.class);

	private static final String PROBLEMS_DIR = "precise_problems";

	private final String filename;
	private final double maxRelaxation;
	private final double maxError;

	public PolicyIterationSatiaGame(final String filename, final double maxRelaxation) {
		this(filename, maxRelaxation, 0.001);
	}

	public PolicyIterationSatiaGame(final String filename, final double maxRelaxation, final double maxError) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
		this.maxError = maxError;
	}

	@Override
	public SolutionReport solve() {
		ProbLinearSolver.initializeCount();
		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: " + filename);
		final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				initialProblemImprecisionGenerator);
		// log.info("Initial problem is " + mdpip.toString());
		log.info("Problem read.");
		log.debug("Starting MDPIP");
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDPIP = Stopwatch.createStarted();
		final Stopwatch watch1 = Stopwatch.createStarted();
		final Policy result = new PolicyIterationSatia(maxError).solve(mdpip);
		watchMDPIP.stop();
		log.debug("End of MDPIP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		log.info("MDPIP: " + result);

		log.info("Summary:");
		log.info("Number of solver calls in solving = " + ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
		assertTrue(true);

		log.info("Solving done. Generating report...\n");
		try {
			final ModifiedPolicyEvaluatorIP evaluator = new ModifiedPolicyEvaluatorIP(10);
			return new SolutionReport(result,
					evaluator.policyEvaluation(result, new UtilityFunctionWithProbImpl(mdpip.getStates()), mdpip));
		} finally {
			log.info("End of problem " + filename + "\n\n\n\n\n");
		}
	}
}
