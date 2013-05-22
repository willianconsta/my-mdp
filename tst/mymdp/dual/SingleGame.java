package mymdp.dual;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import mymdp.core.MDPIP;
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

    @Parameters
    public static Collection<Object[]> data() {
	return Arrays.asList(new Object[][] { {
		"navigation01.net" }, {
		"navigation02.net" }, {
		"navigation03.net" }, {
		"navigation04.net" }, {
		"navigation05.net" }, {
		"navigation06.net" }, {
		"navigation07.net" }, {
		"navigation08.net" }, {
		"navigation09.net" }
		// {
		// "navigation10.net" }, {
		// "navigation11.net" }, {
		// "navigation12.net" }, }
	});
    }

    private final String filename;

    public SingleGame(final String filename) {
	this.filename = filename;
    }

    @Test(timeout = 600000L)
    public void test() {
	// Reads the MDP's definition from file and turns it to an imprecise
	// problem
	log.info("Current Problem: " + filename);
	final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(0.25);
	final MDPImpreciseFileProblemReaderImpl initialReader = new MDPImpreciseFileProblemReaderImpl(initialProblemImprecisionGenerator);
	final MDPIP mdpip = initialReader.readFromFile(PROBLEMS_DIR + "\\" + filename);
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
    }
}
