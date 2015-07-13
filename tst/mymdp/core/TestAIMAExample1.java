package mymdp.core;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.hash;
import static mymdp.test.MDPAssertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.data.Offset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import mymdp.dual.Problem;
import mymdp.solver.MDPDualLinearProgrammingSolver;
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.solver.RTDP.ConvergencyCriteria;
import mymdp.solver.RTDPImpl;
import mymdp.solver.ValueIterationImpl;

@Ignore
@RunWith(Parameterized.class)
public class TestAIMAExample1
{
	public static final double DELTA_THRESHOLD = 1e-3;

	private static class StateImpl
		implements
			State
	{
		final int i;
		final int j;

		private StateImpl(final int i, final int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public String name() {
			return toString();
		}

		@Override
		public String toString() {
			return String.format("x%02dy%02d", i, j);
		}

		@Override
		public int hashCode() {
			return i * 3 + j * 4;
		}

		@Override
		public boolean equals(final Object obj) {
			if ( obj == null ) {
				return false;
			}

			final StateImpl other = (StateImpl) obj;
			return i == other.i && j == other.j;
		}

		private static Map<State,State> allStates = new LinkedHashMap<>();

		static State createState(final int i, final int j) {
			if ( i == 2 && j == 2 ) {
				return null;
			}
			final StateImpl s = new StateImpl(min(max(i, 1), 3), min(max(j, 1), 4));
			if ( !allStates.containsKey(s) ) {
				allStates.put(s, s);
			}
			return allStates.get(s);
		}
	}

	private static class ActionUp
		implements
			Action
	{
		@Override
		public boolean isApplicableTo(final State state) {
			final StateImpl s = (StateImpl) state;
			return !( s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4 );
		}

		@Override
		public String name() {
			return "ActionUp";
		}

		Map<State,Double> applyOver(final State state) {
			assert isApplicableTo(state);

			final StateImpl s = (StateImpl) state;
			final Map<State,Double> states = new LinkedHashMap<>(4);
			State next1 = StateImpl.createState(s.i + 1, s.j);
			if ( next1 == null ) {
				next1 = state;
			}
			State next2 = StateImpl.createState(s.i, s.j + 1);
			if ( next2 == null ) {
				next2 = state;
			}
			State next3 = StateImpl.createState(s.i, s.j - 1);
			if ( next3 == null ) {
				next3 = state;
			}
			states.put(next1, 0.8);
			Double old = states.put(next2, 0.1);
			if ( old != null ) {
				states.put(next2, 0.1 + old);
			}
			old = states.put(next3, 0.1);
			if ( old != null ) {
				states.put(next3, 0.1 + old);
			}
			return Collections.unmodifiableMap(states);
		}

		@Override
		public int hashCode() {
			return hash(name());
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ActionUp;
		}
	}

	private static class ActionDown
		implements
			Action
	{
		@Override
		public boolean isApplicableTo(final State state) {
			final StateImpl s = (StateImpl) state;
			return !( s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4 );
		}

		@Override
		public String name() {
			return "ActionDown";
		}

		Map<State,Double> applyOver(final State state) {
			assert isApplicableTo(state);

			final StateImpl s = (StateImpl) state;
			final Map<State,Double> states = new LinkedHashMap<>(4);
			State next1 = StateImpl.createState(s.i - 1, s.j);
			if ( next1 == null ) {
				next1 = state;
			}
			State next2 = StateImpl.createState(s.i, s.j + 1);
			if ( next2 == null ) {
				next2 = state;
			}
			State next3 = StateImpl.createState(s.i, s.j - 1);
			if ( next3 == null ) {
				next3 = state;
			}
			states.put(next1, 0.8);
			Double old = states.put(next2, 0.1);
			if ( old != null ) {
				states.put(next2, 0.1 + old);
			}
			old = states.put(next3, 0.1);
			if ( old != null ) {
				states.put(next3, 0.1 + old);
			}
			return Collections.unmodifiableMap(states);
		}

		@Override
		public int hashCode() {
			return hash(name());
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ActionDown;
		}
	}

	private static class ActionLeft
		implements
			Action
	{
		@Override
		public boolean isApplicableTo(final State state) {
			final StateImpl s = (StateImpl) state;
			return !( s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4 );
		}

		@Override
		public String name() {
			return "ActionLeft";
		}

		Map<State,Double> applyOver(final State state) {
			assert isApplicableTo(state);

			final StateImpl s = (StateImpl) state;
			final Map<State,Double> states = new LinkedHashMap<>(4);
			State next1 = StateImpl.createState(s.i, s.j - 1);
			if ( next1 == null ) {
				next1 = state;
			}
			State next2 = StateImpl.createState(s.i + 1, s.j);
			if ( next2 == null ) {
				next2 = state;
			}
			State next3 = StateImpl.createState(s.i - 1, s.j);
			if ( next3 == null ) {
				next3 = state;
			}
			states.put(next1, 0.8);
			Double old = states.put(next2, 0.1);
			if ( old != null ) {
				states.put(next2, 0.1 + old);
			}
			old = states.put(next3, 0.1);
			if ( old != null ) {
				states.put(next3, 0.1 + old);
			}
			return Collections.unmodifiableMap(states);
		}

		@Override
		public int hashCode() {
			return hash(name());
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ActionLeft;
		}
	}

	private static class ActionRight
		implements
			Action
	{
		@Override
		public boolean isApplicableTo(final State state) {
			final StateImpl s = (StateImpl) state;
			return !( s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4 );
		}

		@Override
		public String name() {
			return "ActionRight";
		}

		Map<State,Double> applyOver(final State state) {
			assert isApplicableTo(state);

			final StateImpl s = (StateImpl) state;
			final Map<State,Double> states = new LinkedHashMap<>(4);
			State next1 = StateImpl.createState(s.i, s.j + 1);
			if ( next1 == null ) {
				next1 = state;
			}
			State next2 = StateImpl.createState(s.i + 1, s.j);
			if ( next2 == null ) {
				next2 = state;
			}
			State next3 = StateImpl.createState(s.i - 1, s.j);
			if ( next3 == null ) {
				next3 = state;
			}
			states.put(next1, 0.8);
			Double old = states.put(next2, 0.1);
			if ( old != null ) {
				states.put(next2, 0.1 + old);
			}
			old = states.put(next3, 0.1);
			if ( old != null ) {
				states.put(next3, 0.1 + old);
			}
			return Collections.unmodifiableMap(states);
		}

		@Override
		public int hashCode() {
			return hash(name());
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ActionRight;
		}
	}

	private static class ActionNone
		implements
			Action
	{
		@Override
		public boolean isApplicableTo(final State state) {
			final StateImpl s = (StateImpl) state;
			return s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4;
		}

		@Override
		public String name() {
			return "ActionNone";
		}

		Map<State,Double> applyOver(final State state) {
			assert isApplicableTo(state);

			return Collections.singletonMap(state, 1.0);
		}

		@Override
		public int hashCode() {
			return hash(name());
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof ActionNone;
		}
	}

	private interface Testable
	{
		UtilityFunction solve(MDP mdp, double maxError);
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList((Object[][]) new Testable[][]{
				{(mdp, maxError) -> new ValueIterationImpl().solve(mdp, maxError)},
				{(mdp, maxError) -> {
					final ModifiedPolicyEvaluator evaluator = new ModifiedPolicyEvaluator(100);
					return evaluator.policyEvaluation(new PolicyIterationImpl(evaluator).solve(mdp),
							new UtilityFunctionImpl(mdp.getStates()), mdp);
				} },
				{(mdp, maxError) -> new MDPDualLinearProgrammingSolver().solve(new Problem<MDP,Void>() {
					@Override
					public String getName() {
						return "AIMA Example";
					}

					@Override
					public MDP getModel() {
						return mdp;
					}

					@Override
					public Void getComplement() {
						return null;
					}
				}).getValueResult()}});
	}

	private final Testable subject;

	public TestAIMAExample1(final Testable testable) {
		this.subject = testable;
	}

	@Test
	public void testValueIteration() {
		final UtilityFunction function = subject.solve(createMDP(), DELTA_THRESHOLD);

		final Offset<Double> delta = offset(DELTA_THRESHOLD);
		assertThat(function).stateHasValue("x01y01", 0.705, delta);
		assertThat(function).stateHasValue("x01y02", 0.655, delta);
		assertThat(function).stateHasValue("x01y03", 0.611, delta);
		assertThat(function).stateHasValue("x01y04", 0.388, delta);

		assertThat(function).stateHasValue("x02y01", 0.762, delta);
		assertThat(function).stateHasValue("x02y03", 0.660, delta);
		assertThat(function).stateHasValue("x02y04", -1.00, delta);

		assertThat(function).stateHasValue("x03y01", 0.812, delta);
		assertThat(function).stateHasValue("x03y02", 0.868, delta);
		assertThat(function).stateHasValue("x03y03", 0.918, delta);
		assertThat(function).stateHasValue("x03y04", 1.000, delta);
	}

	@Test
	public void testRTDP() {
		final UtilityFunction function = new RTDPImpl(new ConvergencyCriteria() {
			int iterations = 0;
			protected static final int MAX_ITERATIONS = 25000;

			@Override
			public boolean hasConverged() {
				return iterations++ > MAX_ITERATIONS;
			}
		}).solve(createMDP(), Collections.<State> singleton(StateImpl.createState(1, 1)), 100);

		assertEquals(0.705, function.getUtility(StateImpl.createState(1, 1)), DELTA_THRESHOLD);
		assertEquals(0.655, function.getUtility(StateImpl.createState(1, 2)), DELTA_THRESHOLD);
		assertEquals(0.611, function.getUtility(StateImpl.createState(1, 3)), DELTA_THRESHOLD);
		assertEquals(0.388, function.getUtility(StateImpl.createState(1, 4)), DELTA_THRESHOLD);

		assertEquals(0.762, function.getUtility(StateImpl.createState(2, 1)), DELTA_THRESHOLD);
		assertEquals(0.660, function.getUtility(StateImpl.createState(2, 3)), DELTA_THRESHOLD);
		assertEquals(-1.0, function.getUtility(StateImpl.createState(2, 4)), DELTA_THRESHOLD);

		assertEquals(0.812, function.getUtility(StateImpl.createState(3, 1)), DELTA_THRESHOLD);
		assertEquals(0.868, function.getUtility(StateImpl.createState(3, 2)), DELTA_THRESHOLD);
		assertEquals(0.918, function.getUtility(StateImpl.createState(3, 3)), DELTA_THRESHOLD);
		assertEquals(1.0, function.getUtility(StateImpl.createState(3, 4)), DELTA_THRESHOLD);
	}

	@Test
	public void testPolicyIteration() {
		final Policy policy = new PolicyIterationImpl(new ModifiedPolicyEvaluator(50)).solve(createMDP());

		assertEquals(new ActionUp(), policy.getActionFor(StateImpl.createState(1, 1)));
		assertEquals(new ActionUp(), policy.getActionFor(StateImpl.createState(2, 1)));
		assertEquals(new ActionRight(), policy.getActionFor(StateImpl.createState(3, 1)));

		assertEquals(new ActionLeft(), policy.getActionFor(StateImpl.createState(1, 2)));
		assertEquals(new ActionRight(), policy.getActionFor(StateImpl.createState(3, 2)));

		assertEquals(new ActionLeft(), // FIXME não deveria ser Up?
				policy.getActionFor(StateImpl.createState(1, 3)));
		assertEquals(new ActionUp(), policy.getActionFor(StateImpl.createState(2, 3)));
		assertEquals(new ActionRight(), policy.getActionFor(StateImpl.createState(3, 3)));

		assertEquals(new ActionLeft(), policy.getActionFor(StateImpl.createState(1, 4)));
		assertNull(policy.getActionFor(StateImpl.createState(2, 4)));
		assertNull(policy.getActionFor(StateImpl.createState(3, 4)));
	}

	private static MDP createMDP() {
		return new MDP() {
			private Set<State> states;
			private final Map<State,Set<Action>> actionsByState = new LinkedHashMap<>();
			private final Set<Action> actions = newHashSet(new ActionUp(), new ActionDown(), new ActionLeft(), new ActionRight(), new ActionNone());

			@Override
			public Set<Action> getAllActions() {
				return Collections.unmodifiableSet(actions);
			}

			@Override
			public Set<State> getStates() {
				if ( states == null ) {
					states = new LinkedHashSet<>();
					for ( int j = 1; j <= 4; j++ ) {
						for ( int i = 1; i <= 3; i++ ) {
							if ( i == 2 & j == 2 ) {
								continue;
							}
							states.add(StateImpl.createState(i, j));
						}
					}
					states = Collections.unmodifiableSet(states);
				}
				return states;
			}

			@Override
			public double getRewardFor(final State state) {
				final StateImpl s = (StateImpl) state;
				if ( s.i == 3 && s.j == 4 ) {
					return 1;
				}
				if ( s.i == 2 && s.j == 4 ) {
					return -1;
				}
				return -0.04;
			}

			@Override
			public TransitionProbability getPossibleStatesAndProbability(final State initialState,
					final Action action) {
				if ( action instanceof ActionUp ) {
					return TransitionProbability.createSimple(initialState, action, ( (ActionUp) action ).applyOver(initialState));
				}
				if ( action instanceof ActionDown ) {
					return TransitionProbability.createSimple(initialState, action, ( (ActionDown) action ).applyOver(initialState));
				}
				if ( action instanceof ActionLeft ) {
					return TransitionProbability.createSimple(initialState, action, ( (ActionLeft) action ).applyOver(initialState));
				}
				if ( action instanceof ActionRight ) {
					return TransitionProbability.createSimple(initialState, action, ( (ActionRight) action ).applyOver(initialState));
				}
				if ( action instanceof ActionNone ) {
					return TransitionProbability.createSimple(initialState, action, ( (ActionNone) action ).applyOver(initialState));
				}
				return TransitionProbability.empty(initialState, action);
			}

			@Override
			public double getDiscountFactor() {
				return 1;
			}

			@Override
			public Set<Action> getActionsFor(final State state) {
				Set<Action> cachedActions = actionsByState.get(state);
				if ( cachedActions == null ) {
					cachedActions = new HashSet<>();
					for ( final Action action : this.actions ) {
						if ( action.isApplicableTo(state) ) {
							cachedActions.add(action);
						}
					}
					actionsByState.put(state, Collections.unmodifiableSet(cachedActions));
				}
				return cachedActions;
			}
		};
	}
}
