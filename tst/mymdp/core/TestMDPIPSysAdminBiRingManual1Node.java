package mymdp.core;

import static mymdp.util.Pair.of;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationIPImpl;
import mymdp.solver.ProbLinearSolver;
import mymdp.solver.ValueIterationIPImpl;
import mymdp.util.Pair;

public class TestMDPIPSysAdminBiRingManual1Node
{

	@Test
	public void testValue() {
		final double DELTA = 0.0001;
		final UtilityFunction result = new ValueIterationIPImpl().solve(createProblem(), DELTA);
		Assert.assertEquals(10.0, result.getUtility(States.C1_ON), DELTA * 10);
		Assert.assertEquals(9.0, result.getUtility(States.C1_OFF), DELTA * 10);
	}

	@Test
	public void testPolicy() {
		final Policy result = new PolicyIterationIPImpl(new ModifiedPolicyEvaluatorIP(10)).solve(createProblem());
		System.out.println(result);
	}

	private static enum States implements State {
		C1_OFF, C1_ON;

		final static Set<State> allStates = ImmutableSet.<State> copyOf(States.values());
	}

	private static enum Actions implements Action {
		NOP, REBOOT1;

		@Override
		public boolean isApplicableTo(final State state) {
			return true;
		}

		final static Set<Action> allActions = ImmutableSet.<Action> copyOf(Actions.values());
	}

	private static MDPIP createProblem() {
		return new MDPIP() {
			@Override
			public Set<State> getStates() {
				return States.allStates;
			}

			@Override
			public Set<Action> getAllActions() {
				return Actions.allActions;
			}

			@Override
			public Set<Action> getActionsFor(final State state) {
				return Actions.allActions;
			}

			@Override
			public double getRewardFor(final State state) {
				switch ( (States) state ) {
					case C1_OFF:
						return 0;
					case C1_ON:
						return 1;
					default:
						throw new IllegalStateException();
				}
			}

			@Override
			public double getDiscountFactor() {
				return 0.90;
			}

			private final Multimap<Pair<? extends State,? extends Action>,Pair<? extends State,String>> probs = ImmutableMultimap
					.<Pair<? extends State,? extends Action>,Pair<? extends State,String>> builder()
					.put(of(States.C1_OFF, Actions.NOP), of(States.C1_OFF, "p2"))
					.put(of(States.C1_OFF, Actions.NOP), of(States.C1_ON, "p4"))
					.put(of(States.C1_OFF, Actions.REBOOT1), of(States.C1_OFF, "0"))
					.put(of(States.C1_OFF, Actions.REBOOT1), of(States.C1_ON, "1"))
					.put(of(States.C1_ON, Actions.NOP), of(States.C1_OFF, "p3"))
					.put(of(States.C1_ON, Actions.NOP), of(States.C1_ON, "p1"))
					.put(of(States.C1_ON, Actions.REBOOT1), of(States.C1_OFF, "0"))
					.put(of(States.C1_ON, Actions.REBOOT1), of(States.C1_ON, "1")).build();

			@Override
			public TransitionProbability getPossibleStatesAndProbability(final State s, final Action a, final UtilityFunction function) {
				final Pair<State,Action> transition = of(s, a);
				final Collection<Pair<? extends State,String>> nextStates = probs.get(transition);
				final Set<String> variables = ImmutableSet.<String> of("p1", "p2", "p3", "p4");
				final List<String> restrictions = ImmutableList.<String> of("p1 >= 0.85 + p2", "p1 <= 0.95", "p2 <= 0.10", "p3 = 1 - p1",
						"p4 = 1 - p2");

				final Map<State,Double> result = ProbLinearSolver.solve(
						nextStates.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
						getRewardFor(s), function, variables, restrictions);
				return TransitionProbability.createSimple(s, a, result);
			}
		};
	}
}
