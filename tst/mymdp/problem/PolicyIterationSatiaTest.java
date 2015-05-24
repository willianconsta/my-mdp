package mymdp.problem;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.solver.PolicyIterationSatia;
import mymdp.test.MDPAssertions;

import org.junit.Test;

public class PolicyIterationSatiaTest
{
	@Test
	public void test() {
		final MDPIP mdpip = MDPImpreciseFileProblemReader.readFromFile("precise_problems\\navigation01.net", new ImprecisionGeneratorImpl(
				0.15));
		final double delta = 0.001;
		final Policy result = new PolicyIterationSatia(delta).solve(mdpip);
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