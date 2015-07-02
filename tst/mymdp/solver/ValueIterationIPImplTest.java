package mymdp.solver;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunction;
import mymdp.test.MDPAssertions;

public class ValueIterationIPImplTest
{
	private final ValueIterationIPImpl subject = new ValueIterationIPImpl();

	@Test
	public void oneStateWithNoNextStates() {
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getPossibleStatesAndProbability(eq(s), eq(a), any())).thenReturn(TransitionProbability.empty(s, a));
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		final UtilityFunction result = subject.solve(mdpip, 0.001);
		MDPAssertions.assertThat(result).stateHasValue(s, 1.0, 0.1);
	}

	@Test
	public void oneStateWithSelfTransition() {
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getPossibleStatesAndProbability(eq(s), eq(a), any()))
				.thenReturn(TransitionProbability.createSimple(s, a, ImmutableMap.of(s, 1.0)));
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		final UtilityFunction result = subject.solve(mdpip, 0.001);

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
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of());
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		subject.solve(mdpip, 0.001);
	}
}
