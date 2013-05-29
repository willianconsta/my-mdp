package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import mymdp.core.MDPIP;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReaderImpl;
import mymdp.solver.SolveCaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Stopwatch;

@RunWith(Parameterized.class)
public class SingleGame {
    private static final Logger log = LogManager.getLogger(SingleGame.class);

    private static final String PROBLEMS_DIR = "precise_problems";
    private static final double MAX_RELAXATION = 0.25;
    private static final double STEP_RELAXATION = 0.10;

    @Parameters
    public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] {
		{ "navigation01.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation02.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation03.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation04.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation05.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation06.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation07.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation08.net", MAX_RELAXATION, STEP_RELAXATION },
		{ "navigation09.net", MAX_RELAXATION, STEP_RELAXATION },
		// { "navigation10.net" },
		// { "navigation11.net" },
		// { "navigation12.net" },
	});
    }

    private final String filename;
    private final double maxRelaxation;
    private final double stepRelaxation;
    UtilityFunction result;

    public SingleGame(final String filename, final double maxRelaxation, final double stepRelaxation) {
	this.filename = filename;
	this.maxRelaxation = maxRelaxation;
	this.stepRelaxation = stepRelaxation;
    }

    @Test(timeout = 600000L)
    public void test() {
	// Reads the MDP's definition from file and turns it to an imprecise
	// problem
	log.info("Current Problem: " + filename);
	final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
	final MDPImpreciseFileProblemReaderImpl initialReader = new MDPImpreciseFileProblemReaderImpl(initialProblemImprecisionGenerator);
	final MDPIP mdpip = initialReader.readFromFile(PROBLEMS_DIR + "\\" + filename);
	// log.info("Initial problem is " + mdpip.toString());
	log.debug("Starting MDPIP");
	SolveCaller.initializeCount();
	final Stopwatch watchMDPIP = new Stopwatch();
	watchMDPIP.start();
	final Stopwatch watch1 = new Stopwatch().start();
	final UtilityFunctionWithProbImpl result = (UtilityFunctionWithProbImpl) new ValueIterationProbImpl(new UtilityFunctionImpl(
		mdpip.getStates())).solve(mdpip, 0.001);
	watchMDPIP.stop();
	log.debug("End of MDPIP: " + watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
	log.info("MDPIP: " + result);

	log.info("Summary:");
	log.info("Number of solver calls in solving = " + SolveCaller.getNumberOfSolverCalls());
	log.info("Time in MDPIP = " + watchMDPIP.elapsed(TimeUnit.MILLISECONDS) + "ms.");
	assertTrue(true);

	log.info("End of problem " + filename + "\n\n\n\n\n");
	this.result = result;
    }
}
