package mymdp.problem;

import java.util.concurrent.Callable;

import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunction;
import mymdp.dual.DualGame;
import mymdp.test.MDPAssertions;
import mymdp.util.Pair;

import org.junit.Test;

public class TestDual {
	private static final String FILENAME = "navigation01.net";
	private static final double MAX_RELAXATION = 0.15;

	private class DualTask implements Callable<Pair<UtilityFunction, Policy>> {
		@Override
		public Pair<UtilityFunction, Policy> call() {
			final DualGame dualGame = new DualGame(FILENAME, MAX_RELAXATION);
			final SolutionReport report = dualGame.solve();
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	@Test
	public void test() {
		final Pair<UtilityFunction, Policy> dualResult = new DualTask().call();
		final Policy result = dualResult.second;
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
