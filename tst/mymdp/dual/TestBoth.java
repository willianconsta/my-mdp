package mymdp.dual;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;

import mymdp.core.UtilityFunction;
import mymdp.util.UtilityFunctionDistanceEvaluator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestBoth {

    private static final double MAX_RELAXATION = 0.15;
    private static final double STEP_RELAXATION = 0.15;

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

    public TestBoth(final String filename, final double maxRelaxation, final double stepRelaxation) {
	this.filename = filename;
	this.maxRelaxation = maxRelaxation;
	this.stepRelaxation = stepRelaxation;
    }

    @Test
    public void both() {
	final SingleGame singleGame = new SingleGame(filename, maxRelaxation, stepRelaxation);
	final DualGame dualGame = new DualGame(filename, maxRelaxation, stepRelaxation);
	singleGame.test();
	dualGame.test();
	final UtilityFunction singleResult = singleGame.result;
	final UtilityFunction dualResult = dualGame.result;
	assertThat(UtilityFunctionDistanceEvaluator.distanceBetween(singleResult, dualResult)).isLessThan(0.01);
    }
}
