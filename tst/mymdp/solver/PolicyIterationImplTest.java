package mymdp.solver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.test.MDPAssertions;

public class PolicyIterationImplTest
{
	private final PolicyIterationImpl subject = new PolicyIterationImpl(new ModifiedPolicyEvaluator(1));

	@Test
	public void oneStateWithNoNextStates() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(a.name()).thenReturn("a");
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getPossibleStatesAndProbability(s, a)).thenReturn(TransitionProbability.empty(s, a));
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		final Policy result = subject.solve(mdp);
		MDPAssertions.assertThat(result).stateHasAction("s", "a");
	}

	@Test
	public void oneStateWithSelfTransition() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		final Action a = mock(Action.class);
		when(a.name()).thenReturn("a");
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of(a));
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getPossibleStatesAndProbability(s, a)).thenReturn(TransitionProbability.createSimple(s, a, ImmutableMap.of(s, 1.0)));
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		final Policy result = subject.solve(mdp);
		MDPAssertions.assertThat(result).stateHasAction("s", "a");
	}

	@Test(expected = IllegalStateException.class)
	public void oneStateWithNoActions() {
		final MDP mdp = mock(MDP.class);
		final State s = mock(State.class);
		when(s.name()).thenReturn("s");
		when(mdp.getStates()).thenReturn(ImmutableSet.of(s));
		when(mdp.getActionsFor(any())).thenReturn(ImmutableSet.of());
		when(mdp.getRewardFor(s)).thenReturn(1.0);
		when(mdp.getDiscountFactor()).thenReturn(0.5);
		subject.solve(mdp);
	}
}
