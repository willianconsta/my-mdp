package mymdp.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import mymdp.solver.ValueIterationImpl;

public class TestEmptyMDP
{

	@Test(timeout = 5000)
	public void test() {
		final UtilityFunction function = new ValueIterationImpl().solve(new MDP() {

			@Override
			public Set<State> getStates() {
				return Collections.emptySet();
			}

			@Override
			public Set<Action> getAllActions() {
				return Collections.emptySet();
			}

			@Override
			public double getRewardFor(final State state) {
				return 0;
			}

			@Override
			public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action) {
				return TransitionProbability.Instance.empty();
			}

			@Override
			public double getDiscountFactor() {
				return 0.9;
			}

			@Override
			public Set<Action> getActionsFor(final State state) {
				return Collections.emptySet();
			}
		}, 0.01);

		assertThat(function.getStates()).isEmpty();
	}
}
