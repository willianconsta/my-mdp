package mymdp.problem;

import mymdp.core.MDPIP;
import mymdp.core.UtilityFunction;
import mymdp.problem.MDPIPBuilder.StateImpl;
import mymdp.solver.ValueIterationIPImpl;

import org.junit.Assert;
import org.junit.Test;

public class TestNavigationIPFile01 {

    @Test
    public void test01() {
	final MDPIPFileProblemReaderImpl reader = new MDPIPFileProblemReaderImpl(0.10);
	final MDPIP mdpip = reader.readFromFile("problems\\navigation01.net");
	final double delta = 0.001;
	final UtilityFunction result = new ValueIterationIPImpl().solve(mdpip, delta);
	Assert.assertEquals(-9.991404955, result.getUtility(new StateImpl("broken-robot")), delta);
	Assert.assertEquals(-4.684730495, result.getUtility(new StateImpl("robot-at-x01y01")), delta);
	Assert.assertEquals(-3.439, result.getUtility(new StateImpl("robot-at-x01y02")), delta);
	Assert.assertEquals(-2.71, result.getUtility(new StateImpl("robot-at-x01y03")), delta);
	Assert.assertEquals(-5.216171495, result.getUtility(new StateImpl("robot-at-x02y01")), delta);
	Assert.assertEquals(-2.71, result.getUtility(new StateImpl("robot-at-x02y02")), delta);
	Assert.assertEquals(-1.9, result.getUtility(new StateImpl("robot-at-x02y03")), delta);
	Assert.assertEquals(-5.694468395, result.getUtility(new StateImpl("robot-at-x03y01")), delta);
	Assert.assertEquals(-1.9, result.getUtility(new StateImpl("robot-at-x03y02")), delta);
	Assert.assertEquals(-1.0, result.getUtility(new StateImpl("robot-at-x03y03")), delta);
	Assert.assertEquals(-6.124935605, result.getUtility(new StateImpl("robot-at-x04y01")), delta);
	Assert.assertEquals(-1.0, result.getUtility(new StateImpl("robot-at-x04y02")), delta);
	Assert.assertEquals(0.0, result.getUtility(new StateImpl("robot-at-x04y03")), delta);
    }
}
