package mymdp.dual;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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

    private class SingleTask implements Callable<UtilityFunction> {
	@Override
	public UtilityFunction call() {
	    final SingleGame singleGame = new SingleGame(filename, maxRelaxation, stepRelaxation);
	    singleGame.test();
	    return singleGame.result;
	}
    }

    private class DualTask implements Callable<UtilityFunction> {
	@Override
	public UtilityFunction call() {
	    final DualGame dualGame = new DualGame(filename, maxRelaxation, stepRelaxation);
	    dualGame.test();
	    return dualGame.result;
	}
    }

    @Test
    public void both() throws InterruptedException, ExecutionException {
	// final ExecutorService pool = Executors.newFixedThreadPool(2);
	// final Future<UtilityFunction> singleFuture = pool.submit(new
	// SingleTask());
	// final Future<UtilityFunction> dualFuture = pool.submit(new
	// DualTask());
	// UtilityFunction singleResult = singleFuture.get();
	// UtilityFunction dualResult = dualFuture.get();
	final UtilityFunction singleResult = new SingleTask().call();
	final UtilityFunction dualResult = new DualTask().call();
	assertThat(UtilityFunctionDistanceEvaluator.distanceBetween(singleResult, dualResult)).isLessThan(0.01);
    }
}
