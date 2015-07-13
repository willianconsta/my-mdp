package mymdp.problem;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunction;
import mymdp.dual.DualGame;
import mymdp.dual.Problem;
import mymdp.test.MDPAssertions;
import mymdp.util.Pair;

public class TestDual
{
	private static final Logger log = LogManager.getLogger(TestDual.class);
	private static final String FILENAME = "navigation01.net";
	private static final double MAX_RELAXATION = 0.15;
	private static final double MAX_ERROR = 0.001;

	private class DualTask
		implements
			Callable<Pair<UtilityFunction,Policy>>
	{
		@Override
		public Pair<UtilityFunction,Policy> call() {
			// Reads the MDP's definition from file and turns it to an imprecise
			// problem
			log.info("Current Problem: {}", FILENAME);
			final ImprecisionGenerator initialProblemImprecisionGenerator = new CachedImprecisionGenerator(
					new ImprecisionGeneratorImpl(MAX_RELAXATION));
			final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile("precise_problems\\" + FILENAME, initialProblemImprecisionGenerator);
			// log.info("Initial problem is {}", mdpip);
			log.info("Problem read.");

			final DualGame dualGame = new DualGame(MAX_ERROR);
			final SolutionReport report = dualGame.solve(new Problem<MDPIP,ImprecisionGenerator>() {
				@Override
				public String getName() {
					return FILENAME;
				}

				@Override
				public MDPIP getModel() {
					return mdpip;
				}

				@Override
				public ImprecisionGenerator getComplement() {
					return initialProblemImprecisionGenerator;
				}
			});
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	@Test
	public void test() {
		final Pair<UtilityFunction,Policy> dualResult = new DualTask().call();
		final Policy result = dualResult.getSecond();
		MDPAssertions.assertThat(result)
				.stateHasAction("robot-at-x01y01", "move-north")
				.stateHasAction("robot-at-x01y02", "move-north")
				.stateHasAction("robot-at-x01y03", "move-east")
				.stateHasAction("robot-at-x02y01", "move-west")
				.stateHasAction("robot-at-x02y02", "move-north")
				.stateHasAction("robot-at-x02y03", "move-east")
				.stateHasAction("robot-at-x03y01", "move-west")
				.stateHasAction("robot-at-x03y02", "move-north")
				.stateHasAction("robot-at-x03y03", "move-east")
				.stateHasAction("robot-at-x04y01", "move-west")
				.stateHasAction("robot-at-x04y02", "move-north")
				.stateHasAction("robot-at-x04y03", "move-west")
				.stateHasAction("broken-robot", "move-west");
	}
}
