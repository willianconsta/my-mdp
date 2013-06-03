package mymdp.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static mymdp.util.Pair.newPair;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationIPImpl;
import mymdp.solver.SolveCaller;
import mymdp.solver.ValueIterationIPImpl;
import mymdp.util.Pair;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class TestMDPIPSysAdminBiRingManual1Node {

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

    private MDPIP createProblem() {
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
		switch ((States) state) {
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

	    private final Multimap<Pair<? extends State, ? extends Action>, Pair<? extends State, String>> probs = ImmutableMultimap
		    .<Pair<? extends State, ? extends Action>, Pair<? extends State, String>> builder()
		    .put(newPair(States.C1_OFF, Actions.NOP), newPair(States.C1_OFF, "p2"))
		    .put(newPair(States.C1_OFF, Actions.NOP), newPair(States.C1_ON, "p4"))
		    .put(newPair(States.C1_OFF, Actions.REBOOT1), newPair(States.C1_OFF, "0"))
		    .put(newPair(States.C1_OFF, Actions.REBOOT1), newPair(States.C1_ON, "1"))
		    .put(newPair(States.C1_ON, Actions.NOP), newPair(States.C1_OFF, "p3"))
		    .put(newPair(States.C1_ON, Actions.NOP), newPair(States.C1_ON, "p1"))
		    .put(newPair(States.C1_ON, Actions.REBOOT1), newPair(States.C1_OFF, "0"))
		    .put(newPair(States.C1_ON, Actions.REBOOT1), newPair(States.C1_ON, "1")).build();

	    @Override
	    public Map<State, Double> getPossibleStatesAndProbability(final State s, final Action a, final UtilityFunction function) {
		final Pair<State, Action> transition = newPair(s, a);
		final Map<State, Double> result = Maps.newLinkedHashMap();
		final Collection<Pair<? extends State, String>> nextStates = probs.get(transition);

		for (final Pair<? extends State, String> pair : nextStates) {
		    try {
			final double prob = Double.parseDouble(pair.second);
			result.put(pair.first, prob);
		    } catch (final NumberFormatException e) {
			result.clear();
			break;
		    }
		}
		if (!result.isEmpty()) {
		    return result;
		}

		final List<String> obj = Lists.newArrayList();
		obj.add(String.valueOf(getRewardFor(s)));

		for (final Pair<? extends State, String> pair : nextStates) {
		    if (pair.second.contains("*0.0*") || pair.second.endsWith("*0.0")) {
			continue;
		    }

		    obj.add(pair.second + "*" + function.getUtility(pair.first));
		}

		SolveCaller solveCaller;
		try {
		    solveCaller = new SolveCaller("D:\\Programação\\Mestrado\\amplcml\\");
		} catch (final IOException e) {
		    throw Throwables.propagate(e);
		}
		final List<String> variables = ImmutableList.<String> of("p1", "p2", "p3", "p4");
		solveCaller.saveAMPLFile(obj, variables,
			ImmutableList.<String> of("p1 >= 0.85 + p2", "p1 <= 0.95", "p2 <= 0.10", "p3 = 1 - p1", "p4 = 1 - p2"), false);
		solveCaller.callSolver();
		final Map<String, Double> currentValuesProb = solveCaller.getCurrentValuesProb();
		if (currentValuesProb.isEmpty()) {
		    System.out.println(solveCaller.getLog());
		    throw new IllegalStateException();
		}

		for (final Pair<? extends State, String> pair : nextStates) {
		    final String constr = pair.second;
		    int i = 0;
		    final String[] values = constr.split("[\\*]");
		    double value = currentValuesProb.containsKey(values[i]) ? currentValuesProb.get(values[i]).doubleValue() : Double
			    .parseDouble(values[i]);
		    final String[] ops = constr.split("[^\\*]");
		    for (final String op : ops) {
			if (op.equals("*")) {
			    final double val1 = currentValuesProb.containsKey(values[i]) ? currentValuesProb.get(values[i])
				    .doubleValue() : Double
				    .parseDouble(values[i]);
			    value *= val1;
			    i++;
			}
		    }
		    checkNotNull(value);
		    result.put(pair.first, value);
		}
		return result;
	    }
	};
    }
}
