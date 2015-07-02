
package mymdp.solver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunction;
import mymdp.test.MDPAssertions;

public class ValueIterationImplTest
{
	private final ValueIterationImpl subject = new ValueIterationImpl();

	@Test
	public void oneStateWithNoNextStates() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getPossibleStatesAndProbability(s, a)).thenReturn(TransitionProbability.empty(s, a));
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		final UtilityFunction result = subject.solve(mdp, 0.001);
		MDPAssertions.assertThat(result).stateHasValue(s, 1.0, 0.1);
	}

	@Test
	public void oneStateWithSelfTransition() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getPossibleStatesAndProbability(s, a)).thenReturn(TransitionProbability.createSimple(s, a, ImmutableMap.of(s, 1.0)));
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		final UtilityFunction result = subject.solve(mdp, 0.001);

		// 1.0 + 0.5 * ( 1.0 * 0.0 ) = 1.5
		// 1.0 + 0.5 * ( 1.0 * 1.5 ) = 1.75
		// 1.0 + 0.5 * ( 1.0 * 1.75 ) = 1.875
		// 1.0 + 0.5 * ( 1.0 * 1.875 ) = 1.9375
		// 1.0 + 0.5 * ( 1.0 * 1.9375 ) = 1.96875
		// 1.984375
		// 1.9921875
		// 1.99609375
		// 1.998046875
		// 1.9990234375
		MDPAssertions.assertThat(result).stateHasValue(s, 2.0, 0.001);
	}

	@Test(expected = IllegalStateException.class)
	public void oneStateWithNoActions() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of());
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		subject.solve(mdp, 0.001);
	}
}
