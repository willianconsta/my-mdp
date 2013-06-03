package mymdp.core;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.solver.RTDP.ConvergencyCriteria;
import mymdp.solver.RTDPImpl;
import mymdp.solver.ValueIterationImpl;

import org.junit.Test;

public class TestAIMAExample1 {
    public static final double DELTA_THRESHOLD = 1e-3;

    private static class StateImpl implements State {
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
	    return "(x=" + i + ",y=" + j + ")";
	}

	@Override
	public int hashCode() {
	    return i * 3 + j * 4;
	}

	@Override
	public boolean equals(final Object obj) {
	    if (obj == null) {
		return false;
	    }

	    final StateImpl other = (StateImpl) obj;
	    return i == other.i && j == other.j;
	}

	private static Map<State, State> allStates = new LinkedHashMap<>();

	static State createState(final int i, final int j) {
	    if (i == 2 && j == 2) {
		return null;
	    }
	    final StateImpl s = new StateImpl(min(max(i, 1), 3), min(max(j, 1), 4));
	    if (!allStates.containsKey(s)) {
		allStates.put(s, s);
	    }
	    return allStates.get(s);
	}
    }

    private static class ActionUp implements Action {
	@Override
	public boolean isApplicableTo(final State state) {
	    final StateImpl s = (StateImpl) state;
	    return !(s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4);
	}

	@Override
	public String name() {
	    return "ActionUp";
	}

	Map<State, Double> applyOver(final State state) {
	    if (!isApplicableTo(state)) {
		return Collections.emptyMap();
	    }

	    final StateImpl s = (StateImpl) state;
	    final Map<State, Double> states = new LinkedHashMap<>(4);
	    State next1 = StateImpl.createState(s.i + 1, s.j);
	    if (next1 == null) {
		next1 = state;
	    }
	    State next2 = StateImpl.createState(s.i, s.j + 1);
	    if (next2 == null) {
		next2 = state;
	    }
	    State next3 = StateImpl.createState(s.i, s.j - 1);
	    if (next3 == null) {
		next3 = state;
	    }
	    states.put(next1, 0.8);
	    Double old = states.put(next2, 0.1);
	    if (old != null) {
		states.put(next2, 0.1 + old);
	    }
	    old = states.put(next3, 0.1);
	    if (old != null) {
		states.put(next3, 0.1 + old);
	    }
	    return Collections.unmodifiableMap(states);
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj instanceof ActionUp;
	}
    }

    private static class ActionDown implements Action {
	@Override
	public boolean isApplicableTo(final State state) {
	    final StateImpl s = (StateImpl) state;
	    return !(s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4);
	}

	@Override
	public String name() {
	    return "ActionDown";
	}

	Map<State, Double> applyOver(final State state) {
	    if (!isApplicableTo(state)) {
		return Collections.emptyMap();
	    }

	    final StateImpl s = (StateImpl) state;
	    final Map<State, Double> states = new LinkedHashMap<>(4);
	    State next1 = StateImpl.createState(s.i - 1, s.j);
	    if (next1 == null) {
		next1 = state;
	    }
	    State next2 = StateImpl.createState(s.i, s.j + 1);
	    if (next2 == null) {
		next2 = state;
	    }
	    State next3 = StateImpl.createState(s.i, s.j - 1);
	    if (next3 == null) {
		next3 = state;
	    }
	    states.put(next1, 0.8);
	    Double old = states.put(next2, 0.1);
	    if (old != null) {
		states.put(next2, 0.1 + old);
	    }
	    old = states.put(next3, 0.1);
	    if (old != null) {
		states.put(next3, 0.1 + old);
	    }
	    return Collections.unmodifiableMap(states);
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj instanceof ActionDown;
	}
    }

    private static class ActionLeft implements Action {
	@Override
	public boolean isApplicableTo(final State state) {
	    final StateImpl s = (StateImpl) state;
	    return !(s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4);
	}

	@Override
	public String name() {
	    return "ActionLeft";
	}

	Map<State, Double> applyOver(final State state) {
	    if (!isApplicableTo(state)) {
		return Collections.emptyMap();
	    }

	    final StateImpl s = (StateImpl) state;
	    final Map<State, Double> states = new LinkedHashMap<>(4);
	    State next1 = StateImpl.createState(s.i, s.j - 1);
	    if (next1 == null) {
		next1 = state;
	    }
	    State next2 = StateImpl.createState(s.i + 1, s.j);
	    if (next2 == null) {
		next2 = state;
	    }
	    State next3 = StateImpl.createState(s.i - 1, s.j);
	    if (next3 == null) {
		next3 = state;
	    }
	    states.put(next1, 0.8);
	    Double old = states.put(next2, 0.1);
	    if (old != null) {
		states.put(next2, 0.1 + old);
	    }
	    old = states.put(next3, 0.1);
	    if (old != null) {
		states.put(next3, 0.1 + old);
	    }
	    return Collections.unmodifiableMap(states);
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj instanceof ActionLeft;
	}
    }

    private static class ActionRight implements Action {
	@Override
	public boolean isApplicableTo(final State state) {
	    final StateImpl s = (StateImpl) state;
	    return !(s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4);
	}

	@Override
	public String name() {
	    return "ActionRight";
	}

	Map<State, Double> applyOver(final State state) {
	    if (!isApplicableTo(state)) {
		return Collections.emptyMap();
	    }

	    final StateImpl s = (StateImpl) state;
	    final Map<State, Double> states = new LinkedHashMap<>(4);
	    State next1 = StateImpl.createState(s.i, s.j + 1);
	    if (next1 == null) {
		next1 = state;
	    }
	    State next2 = StateImpl.createState(s.i + 1, s.j);
	    if (next2 == null) {
		next2 = state;
	    }
	    State next3 = StateImpl.createState(s.i - 1, s.j);
	    if (next3 == null) {
		next3 = state;
	    }
	    states.put(next1, 0.8);
	    Double old = states.put(next2, 0.1);
	    if (old != null) {
		states.put(next2, 0.1 + old);
	    }
	    old = states.put(next3, 0.1);
	    if (old != null) {
		states.put(next3, 0.1 + old);
	    }
	    return Collections.unmodifiableMap(states);
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj instanceof ActionRight;
	}
    }

    private static class ActionNone implements Action {
	@Override
	public boolean isApplicableTo(final State state) {
	    final StateImpl s = (StateImpl) state;
	    return !(s.i == 3 && s.j == 4 || s.i == 2 && s.j == 4);
	}

	@Override
	public String name() {
	    return "ActionNone";
	}

	Map<State, Double> applyOver(final State state) {
	    if (!isApplicableTo(state)) {
		return Collections.emptyMap();
	    }

	    return Collections.singletonMap(state, 1.0);
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj instanceof ActionNone;
	}
    }

    @Test
    public void testValueIteration() {
	final UtilityFunction function = new ValueIterationImpl().solve(createMDP(), 0.001);

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

    private MDP createMDP() {
	return new MDP() {
	    private Set<State> states;
	    private final Map<State, Set<Action>> actionsByState = new LinkedHashMap<>();
	    private final Set<Action> actions = newHashSet(new ActionUp(), new ActionDown(), new ActionLeft(), new ActionRight(),
		    new ActionNone());

	    @Override
	    public Set<Action> getAllActions() {
		return Collections.unmodifiableSet(actions);
	    }

	    @Override
	    public Set<State> getStates() {
		if (states == null) {
		    states = new LinkedHashSet<>();
		    for (int j = 1; j <= 4; j++) {
			for (int i = 1; i <= 3; i++) {
			    if (i == 2 & j == 2) {
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
		if (s.i == 3 && s.j == 4) {
		    return 1;
		}
		if (s.i == 2 && s.j == 4) {
		    return -1;
		}
		return -0.04;
	    }

	    @Override
	    public Map<State, Double> getPossibleStatesAndProbability(final State initialState, final Action action) {
		if (action instanceof ActionUp) {
		    return ((ActionUp) action).applyOver(initialState);
		}
		if (action instanceof ActionDown) {
		    return ((ActionDown) action).applyOver(initialState);
		}
		if (action instanceof ActionLeft) {
		    return ((ActionLeft) action).applyOver(initialState);
		}
		if (action instanceof ActionRight) {
		    return ((ActionRight) action).applyOver(initialState);
		}
		if (action instanceof ActionNone) {
		    return ((ActionNone) action).applyOver(initialState);
		}
		return Collections.emptyMap();
	    }

	    @Override
	    public double getDiscountFactor() {
		return 1;
	    }

	    @Override
	    public Set<Action> getActionsFor(final State state) {
		Set<Action> actions = actionsByState.get(state);
		if (actions == null) {
		    actions = new HashSet<>();
		    for (final Action action : this.actions) {
			if (action.isApplicableTo(state)) {
			    actions.add(action);
			}
		    }
		    actionsByState.put(state, Collections.unmodifiableSet(actions));
		}
		return actions;
	    }
	};
    }
}
