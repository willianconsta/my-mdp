package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReaderImpl;
import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationIPImpl;
import mymdp.solver.SolveCaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

public class SingleGame {
    private static final Logger log = LogManager.getLogger(SingleGame.class);

    private static final String PROBLEMS_DIR = "precise_problems";

    private final String filename;
    private final double maxRelaxation;
    private UtilityFunction valueResult;
    private Policy policyResult;

    public SingleGame(final String filename, final double maxRelaxation) {
	this.filename = filename;
	this.maxRelaxation = maxRelaxation;
    }

    public void solve() {
	// Reads the MDP's definition from file and turns it to an imprecise
	// problem
	log.info("Current Problem: " + filename);
	final ModifiedPolicyEvaluatorIP evaluator = new ModifiedPolicyEvaluatorIP(100);
	final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
	final MDPImpreciseFileProblemReaderImpl initialReader = new MDPImpreciseFileProblemReaderImpl(initialProblemImprecisionGenerator);
	final MDPIP mdpip = initialReader.readFromFile(PROBLEMS_DIR + "\\" + filename);
	// log.info("Initial problem is " + mdpip.toString());
	log.debug("Starting MDPIP");
	SolveCaller.initializeCount();
	final Stopwatch watchMDPIP = new Stopwatch();
	watchMDPIP.start();
	final Stopwatch watch1 = new Stopwatch().start();
	final Policy result = new PolicyIterationIPImpl(evaluator).solve(mdpip);
	watchMDPIP.stop();
	log.debug("End of MDPIP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
	log.info("MDPIP: " + result);

	log.info("Summary:");
	log.info("Number of solver calls in solving = " + SolveCaller.getNumberOfSolverCalls());
	log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	assertTrue(true);

	log.info("End of problem " + filename + "\n\n\n\n\n");
	this.policyResult = result;
	this.valueResult = evaluator.policyEvaluation(result, new UtilityFunctionWithProbImpl(mdpip.getStates()), mdpip);
    }

    public Policy getPolicyResult() {
	return policyResult;
    }

    public UtilityFunction getValueResult() {
	return valueResult;
    }
}
