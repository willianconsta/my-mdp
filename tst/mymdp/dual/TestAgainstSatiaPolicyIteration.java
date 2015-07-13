package mymdp.dual;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunction;
import mymdp.problem.CachedImprecisionGenerator;
import mymdp.problem.ImprecisionGenerator;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.util.Pair;

@RunWith(Parameterized.class)
public class TestAgainstSatiaPolicyIteration
{
	private static final Logger log = LogManager.getLogger(TestAgainstSatiaPolicyIteration.class);
	private static final double MAX_RELAXATION = 0.35;

	private static final double MAX_ERROR = 0.001;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][]{{"navigation01.net", MAX_RELAXATION},
				{"navigation02.net", MAX_RELAXATION}, {"navigation03.net", MAX_RELAXATION},
				{"navigation04.net", MAX_RELAXATION}, {"navigation05.net", MAX_RELAXATION},
				{"navigation06.net", MAX_RELAXATION}, {"navigation07.net", MAX_RELAXATION},
				{"navigation08.net", MAX_RELAXATION}, {"navigation09.net", MAX_RELAXATION},});
	}

	private final String filename;
	private final double maxRelaxation;

	public TestAgainstSatiaPolicyIteration(final String filename, final double maxRelaxation) {
		this.filename = filename;
		this.maxRelaxation = maxRelaxation;
	}

	private class SingleTask
		implements
			Callable<Pair<UtilityFunction,Policy>>
	{
		@Override
		public Pair<UtilityFunction,Policy> call() {
			// Reads the MDP's definition from file and turns it to an imprecise
			// problem
			log.info("Current Problem: {}", filename);
			final ImprecisionGeneratorImpl initialProblemImprecisionGenerator = new ImprecisionGeneratorImpl(maxRelaxation);
			final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile("precise_problems\\" + filename, initialProblemImprecisionGenerator);
			// log.info("Initial problem is {}", mdpip);
			log.info("Problem read.");

			final SolutionReport report = new PolicyIterationSatiaGame(MAX_ERROR).solve(new Problem<MDPIP,Void>() {
				@Override
				public MDPIP getModel() {
					return mdpip;
				}

				@Override
				public Void getComplement() {
					return null;
				}

				@Override
				public String getName() {
					return filename;
				}
			});
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	private class DualTask
		implements
			Callable<Pair<UtilityFunction,Policy>>
	{
		@Override
		public Pair<UtilityFunction,Policy> call() {
			// Reads the MDP's definition from file and turns it to an imprecise
			// problem
			log.info("Current Problem: {}", filename);
			final ImprecisionGenerator initialProblemImprecisionGenerator = new CachedImprecisionGenerator(
					new ImprecisionGeneratorImpl(maxRelaxation));
			final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile("precise_problems\\" + filename, initialProblemImprecisionGenerator);
			// log.info("Initial problem is {}", mdpip);
			log.info("Problem read.");

			final SolutionReport report = new DualGame(MAX_ERROR).solve(new Problem<MDPIP,ImprecisionGenerator>() {
				@Override
				public MDPIP getModel() {
					return mdpip;
				}

				@Override
				public ImprecisionGenerator getComplement() {
					return initialProblemImprecisionGenerator;
				}

				@Override
				public String getName() {
					return filename;
				}
			});
			return Pair.of(report.getValueResult(), report.getPolicyResult());
		}
	}

	@Test
	public void both() {
		final Pair<UtilityFunction,Policy> singleResult = new SingleTask().call();
		final Pair<UtilityFunction,Policy> dualResult = new DualTask().call();
		// assertThat(UtilityFunctionDistanceEvaluator.distanceBetween(singleResult.first,
		// dualResult.first)).isLessThan(0.001);
		assertThat(singleResult.getSecond()).isEqualTo(dualResult.getSecond());
	}
}
