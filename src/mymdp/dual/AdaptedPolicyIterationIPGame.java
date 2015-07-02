package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationIPImpl;
import mymdp.solver.ProbLinearSolver;

public class AdaptedPolicyIterationIPGame
{
	private static final Logger log = LogManager.getLogger(AdaptedPolicyIterationIPGame.class);

	private static final String PROBLEMS_DIR = "precise_problems";

	private final String filename;
	private final double maxRelaxation;
	private UtilityFunction valueResult;
	private Policy policyResult;

	public AdaptedPolicyIterationIPGame(final String filename, final double maxRelaxation) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
	}

	public void solve() {
		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: {}", filename);
		final ModifiedPolicyEvaluatorIP evaluator = new ModifiedPolicyEvaluatorIP(100);
		final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
		final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile(PROBLEMS_DIR + "\\" + filename,
				initialProblemImprecisionGenerator);
		// log.info("Initial problem is " + mdpip.toString());
		log.debug("Starting MDPIP");
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDPIP = Stopwatch.createStarted();
		final Stopwatch watch1 = Stopwatch.createStarted();
		final Policy result = new PolicyIterationIPImpl(evaluator).solve(mdpip);
		watchMDPIP.stop();
		log.debug("End of MDPIP: {}ms", watch1.elapsed(TimeUnit.MILLISECONDS));
		log.info("MDPIP: {}", result);

		log.info("Summary:");
		log.info("Number of solver calls in solving = {}", ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Time in MDPIP = {}ms", watchMDPIP.elapsed(TimeUnit.MILLISECONDS));
		assertTrue(true);

		log.info("End of problem {}\n\n\n\n\n", filename);
		this.policyResult = result;
		this.valueResult = evaluator.policyEvaluation(result, new UtilityFunctionImpl(mdpip.getStates()), mdpip);
	}

	public Policy getPolicyResult() {
		return policyResult;
	}

	public UtilityFunction getValueResult() {
		return valueResult;
	}
}
