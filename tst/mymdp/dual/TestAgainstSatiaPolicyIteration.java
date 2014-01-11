package mymdp.dual;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunction;
import mymdp.util.Pair;
import mymdp.util.UtilityFunctionDistanceEvaluator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestAgainstSatiaPolicyIteration {

	private static final double MAX_RELAXATION = 0.15;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ "navigation01.net", MAX_RELAXATION },
				{ "navigation02.net", MAX_RELAXATION },
				{ "navigation03.net", MAX_RELAXATION },
				{ "navigation04.net", MAX_RELAXATION },
				{ "navigation05.net", MAX_RELAXATION },
				{ "navigation06.net", MAX_RELAXATION },
				{ "navigation07.net", MAX_RELAXATION },
				{ "navigation08.net", MAX_RELAXATION },
				{ "navigation09.net", MAX_RELAXATION },
		});
	}

	private final String filename;
	private final double maxRelaxation;

	public TestAgainstSatiaPolicyIteration(final String filename, final double maxRelaxation) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
	}

	private class SingleTask implements Callable<Pair<UtilityFunction, Policy>> {
		@Override
		public Pair<UtilityFunction, Policy> call() {
			final SolutionReport report = new PolicyIterationSatiaGame(filename, maxRelaxation).solve();
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	private class DualTask implements Callable<Pair<UtilityFunction, Policy>> {
		@Override
		public Pair<UtilityFunction, Policy> call() {
			final DualGame dualGame = new DualGame(filename, maxRelaxation);
			final SolutionReport report = dualGame.solve();
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	@Test
	public void both() throws InterruptedException, ExecutionException {
		final Pair<UtilityFunction, Policy> singleResult = new SingleTask().call();
		final Pair<UtilityFunction, Policy> dualResult = new DualTask().call();
		assertThat(UtilityFunctionDistanceEvaluator.distanceBetween(singleResult.first, dualResult.first)).isLessThan(0.001);
		assertThat(singleResult.second).isEqualTo(dualResult.second);
	}
}
