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
import mymdp.core.SolutionReport;
import mymdp.problem.CachedImprecisionGenerator;
import mymdp.problem.ImprecisionGenerator;
import mymdp.problem.ImprecisionGeneratorImpl;
import mymdp.problem.MDPImpreciseFileProblemReader;
import mymdp.util.UtilityFunctionDistanceEvaluator;

@RunWith(Parameterized.class)
public class TestAgainstAdaptedPI
{
	private static final Logger log = LogManager.getLogger(TestAgainstAdaptedPI.class);
	private static final double MAX_RELAXATION = 0.15;
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
	private final MDPIP mdpip;
	private final ImprecisionGenerator initialProblemImprecisionGenerator;

	public TestAgainstAdaptedPI(final String filename, final double maxRelaxation) {
		this.filename = filename;
		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: {}", filename);
		initialProblemImprecisionGenerator = new CachedImprecisionGenerator(new ImprecisionGeneratorImpl(maxRelaxation));
		mdpip = MDPImpreciseFileProblemReader.readFromFile("precise_problems\\" + filename, initialProblemImprecisionGenerator);
		// log.info("Initial problem is {}", mdpip);
		log.info("Problem read.");
	}

	private class SingleTask
		implements
			Callable<SolutionReport>
	{
		@Override
		public SolutionReport call() {
			return new AdaptedPolicyIterationIPGame().solve(new Problem<MDPIP,Void>() {
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
		}
	}

	private class DualTask
		implements
			Callable<SolutionReport>
	{
		@Override
		public SolutionReport call() {
			return new DualGame(MAX_ERROR).solve(new Problem<MDPIP,ImprecisionGenerator>() {
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
		}
	}

	@Test
	public void both() {
		final SolutionReport singleResult = new SingleTask().call();
		final SolutionReport dualResult = new DualTask().call();
		assertThat(UtilityFunctionDistanceEvaluator.distanceBetween(singleResult.getValueResult(), dualResult.getValueResult()))
				.isLessThan(0.01);
		assertThat(singleResult.getPolicyResult()).isEqualTo(dualResult.getPolicyResult());
	}
}
