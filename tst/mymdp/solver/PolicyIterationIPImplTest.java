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
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.test.MDPAssertions;

public class PolicyIterationIPImplTest
{
	private final PolicyIterationIPImpl subject = new PolicyIterationIPImpl(new ModifiedPolicyEvaluatorIP(1));

	@Test
	public void oneStateWithNoNextStates() {
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(a.name()).thenReturn("a");
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getPossibleStatesAndProbability(eq(s), eq(a), any())).thenReturn(TransitionProbability.empty(s, a));
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		final Policy result = subject.solve(mdpip);
		MDPAssertions.assertThat(result).stateHasAction("s", "a");
	}

	@Test
	public void oneStateWithSelfTransition() {
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(a.name()).thenReturn("a");
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getPossibleStatesAndProbability(eq(s), eq(a), any()))
				.thenReturn(TransitionProbability.createSimple(s, a, ImmutableMap.of(s, 1.0)));
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		final Policy result = subject.solve(mdpip);
		MDPAssertions.assertThat(result).stateHasAction("s", "a");
	}

	@Test(expected = IllegalStateException.class)
	public void oneStateWithNoActions() {
		final MDPIP mdpip = mock(MDPIP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdpip.getStates()).thenReturn(ImmutableSet.of(s));
		when(mdpip.getActionsFor(any())).thenReturn(ImmutableSet.of());
		when(mdpip.getRewardFor(s)).thenReturn(1.0);
		when(mdpip.getDiscountFactor()).thenReturn(0.5);
		subject.solve(mdpip);
	}
}
