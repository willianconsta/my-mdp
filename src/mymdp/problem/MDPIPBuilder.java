package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static mymdp.solver.ProbLinearSolver.solve;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.util.Trio;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;

public class MDPIPBuilder {
    @VisibleForTesting
    static class StateImpl implements State {
	private final String name;
	private double reward;

	StateImpl(final String name) {
	    checkArgument(name != null && !name.isEmpty());
	    this.name = name;
	}

	@Override
	public String getName() {
	    return name;
	}

	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
	    if (!(obj instanceof State)) {
		return false;
	    }
	    return name.equals(((State) obj).getName());
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    @VisibleForTesting
    static class ActionImpl implements Action {
	private final Set<State> appliableStates = new LinkedHashSet<>();
	private final String name;

	ActionImpl(final String name) {
	    checkArgument(name != null && !name.isEmpty());
	    this.name = name;
	}

	@Override
	public boolean isApplyableTo(final State state) {
	    return appliableStates.contains(state);
	}

	@Override
	public int hashCode() {
	    return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
	    if (!(obj instanceof ActionImpl)) {
		return false;
	    }
	    return name.equals(((ActionImpl) obj).name);
	}

	@Override
	public String toString() {
	    return name;
	}
    }

    private final Map<String, StateImpl> states;
    private final Map<Action, Map<State, Map<State, String>>> transitions;
    private double discountRate;
    private Set<String> restrictions;
    private final Map<Trio<State, Action, State>, String> vars;
    private int i = 0;

    public MDPIPBuilder() {
	states = new LinkedHashMap<>();
	transitions = new LinkedHashMap<>();
	vars = new LinkedHashMap<>();
	restrictions = new LinkedHashSet<>();
    }

    public MDPIPBuilder states(final Set<String> stateDefs) {
	for (final String stateDef : stateDefs) {
	    states.put(stateDef, new StateImpl(stateDef));
	}
	return this;
    }

    public MDPIPBuilder actions(final String actionName, final Set<String[]> transitionDefs) {
	final Action action = new ActionImpl(actionName);
	final Map<State, String> sumOnePerState = new LinkedHashMap<>();
	final Map<String, Double> lowestSum = new LinkedHashMap<>();
	final Map<String, Double> greatestSum = new LinkedHashMap<>();

	for (final String[] transitionDef : transitionDefs) {
	    final State state1 = checkNotNull(states.get(transitionDef[0]));
	    ((ActionImpl) action).appliableStates.add(state1);

	    Map<State, Map<State, String>> probs = transitions.get(action);
	    if (probs == null) {
		probs = new LinkedHashMap<>();
		transitions.put(action, probs);
	    }

	    final State state2 = checkNotNull(states.get(transitionDef[1]));
	    Map<State, String> map = probs.get(state1);
	    if (map == null) {
		map = new LinkedHashMap<>();
		probs.put(state1, map);
	    }

	    final String var = "p" + i++;
	    map.put(state2, checkNotNull(var));

	    lowestSum.put(transitionDef[0], (lowestSum.containsKey(transitionDef[0]) ? lowestSum.get(transitionDef[0]) : 0)
		    + Double.parseDouble(transitionDef[2]));
	    greatestSum.put(transitionDef[0], (greatestSum.containsKey(transitionDef[0]) ? greatestSum.get(transitionDef[0]) : 0)
		    + Double.parseDouble(transitionDef[3]));
	    if (transitionDef[2].equals(transitionDef[3])) {
		map.put(state2, transitionDef[3]);
		continue;
	    } else {
		if (!transitionDef[2].equals("0.0")) {
		    restrictions.add(var + " >= " + transitionDef[2]);
		}
		if (!transitionDef[3].equals("1.0")) {
		    restrictions.add(var + " <= " + transitionDef[3]);
		}
	    }
	    String restrOne = sumOnePerState.get(state1);
	    if (restrOne != null) {
		restrOne += "+";
	    } else {
		restrOne = "";
	    }
	    restrOne += var;
	    sumOnePerState.put(state1, restrOne);
	    vars.put(Trio.newTrio(state1, action, state2), var);
	}

	checkState(Collections.max(lowestSum.values()) <= 1, "Expected leq than 1, got " + lowestSum);
	checkState(Collections.min(greatestSum.values()) >= 1, "Expected geq than 1, got " + greatestSum);

	// restrictions of sum one
	for (final Entry<State, String> sumOne : sumOnePerState.entrySet()) {
	    if (sumOne.getValue().indexOf('p') < sumOne.getValue().lastIndexOf('p')) {
		// if there is more than one variable force their sum to be one.
		restrictions.add(sumOne.getValue() + "=1");
	    } else {
		// if there is only one variable it must be one.
		final String var = sumOne.getValue();
		checkState(vars.values().remove(var));
		// removes other restrictions over the variable being removed
		for (final Iterator<String> it = restrictions.iterator(); it.hasNext();) {
		    final String restriction = it.next();
		    if (restriction.matches(".*?" + var + "(\\b|[^\\d].*?)")) {
			it.remove();
		    }
		}
		// force it to be one.
		getOnlyElement(transitions.get(action).get(sumOne.getKey()).entrySet()).setValue("1");
	    }
	}
	return this;
    }

    public MDPIPBuilder restrictions(final Set<String> restrictions) {
	this.restrictions = restrictions;
	return this;
    }

    public MDPIPBuilder reward(final Map<String, Double> rewards) {
	for (final Entry<String, Double> entry : rewards.entrySet()) {
	    states.get(entry.getKey()).reward = entry.getValue();
	}
	return this;
    }

    public MDPIPBuilder discountRate(final double rate) {
	checkArgument(Range.closed(0.0, 1.0).contains(rate));
	this.discountRate = rate;
	return this;
    }

    public MDPIP build() {
	return new MDPIP() {
	    private final Set<State> states = ImmutableSet.<State> copyOf(MDPIPBuilder.this.states.values());
	    private final Map<Action, Map<State, Map<State, String>>> transitions = ImmutableMap.copyOf(MDPIPBuilder.this.transitions);
	    private final Set<String> restrictions = ImmutableSet.<String> copyOf(MDPIPBuilder.this.restrictions);
	    private final Map<Trio<State, Action, State>, String> vars = ImmutableMap
		    .<Trio<State, Action, State>, String> copyOf(MDPIPBuilder.this.vars);

	    @Override
	    public Set<State> getStates() {
		return states;
	    }

	    @Override
	    public Set<Action> getAllActions() {
		return transitions.keySet();
	    }

	    @Override
	    public Set<Action> getActionsFor(final State state) {
		final Set<Action> appliableActions = new LinkedHashSet<>();
		for (final Entry<Action, Map<State, Map<State, String>>> entry : transitions.entrySet()) {
		    if (entry.getValue().containsKey(state) && !entry.getValue().get(state).isEmpty()) {
			appliableActions.add(entry.getKey());
		    }
		}
		return appliableActions;
	    }

	    @Override
	    public Map<State, Double> getPossibleStatesAndProbability(final State initialState, final Action action,
		    final UtilityFunction function) {
		final Map<State, String> probabilityFunction = transitions.get(action).get(initialState);
		if (probabilityFunction == null) {
		    return Collections.emptyMap();
		}

		final Map<State, Double> minProb = solve(probabilityFunction, getRewardFor(initialState), function,
			vars.values(), restrictions);
		return minProb;
	    }

	    @Override
	    public double getRewardFor(final State state) {
		return ((StateImpl) state).reward;
	    }

	    @Override
	    public double getDiscountFactor() {
		return discountRate;
	    }

	    @Override
	    public String toString() {
		final Map<Action, Map<State, Map<State, String>>> transitions = new TreeMap<>(Ordering.usingToString());
		transitions.putAll(this.transitions);

		final Set<State> states = new TreeSet<>(Ordering.usingToString());
		states.addAll(this.states);

		final Map<Trio<State, Action, State>, String> vars = new TreeMap<>(Ordering.usingToString());
		vars.putAll(this.vars);

		final Set<String> restrictions = new TreeSet<>(this.restrictions);
		restrictions.addAll(this.restrictions);

		return Objects.toStringHelper(this)
			.add("states",
				states.toString().replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t").replaceFirst("\\]", "\n\\]"))
			.add("transitions",
				transitions.toString().replaceAll("}, ", "},\n\t").replaceFirst("\\[", "\\[\n\t")
					.replaceFirst("\\]", "\n\\]"))
			.add("discountRate", discountRate)
			.add("vars", vars.toString().replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t")
				.replaceFirst("\\]", "\n\\]").replaceFirst("\\{", "\\{\n\t")
				.replaceFirst("\\}", "\n\\}"))
			.add("restrictions", restrictions.toString().replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t")
				.replaceFirst("\\]", "\n\\]"))
			.toString();
	    }
	};
    }
}
