package mymdp.problem;

import static com.google.common.base.MoreObjects.toStringHelper;
import static mymdp.solver.ProbLinearSolver.solve;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunction;
import mymdp.exception.InvalidProbabilityFunctionException;
import mymdp.solver.ProbLinearSolver;
import mymdp.util.Trio;

final class MDPIPImpl
	implements
		MDPIP
{
	private final Set<State> states;
	private final Map<State,Double> rewards;
	private final Map<Action,Map<State,Map<State,String>>> transitions;
	private final Set<String> restrictions;
	private final Map<Trio<State,Action,State>,String> vars;
	private final Map<Object,TransitionProbability> cache;
	private final double discountFactor;

	MDPIPImpl(
			final Collection<? extends State> states,
			final Map<? extends State,Double> rewards,
			final Map<Action,Map<State,Map<State,String>>> transitions,
			final Set<String> restrictions,
			final Map<Trio<State,Action,State>,String> vars,
			final double discountFactor) {
		this.states = ImmutableSet.copyOf(states);
		this.rewards = ImmutableMap.copyOf(rewards);
		this.transitions = ImmutableMap.copyOf(transitions);
		this.restrictions = ImmutableSet.copyOf(restrictions);
		this.vars = ImmutableMap.copyOf(vars);
		this.discountFactor = discountFactor;
		this.cache = new LinkedHashMap<>();
	}

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
		for ( final Entry<Action,Map<State,Map<State,String>>> entry : transitions.entrySet() ) {
			if ( entry.getValue().containsKey(state) && !entry.getValue().get(state).isEmpty() ) {
				appliableActions.add(entry.getKey());
			}
		}
		return appliableActions;
	}

	@Override
	public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action,
			final UtilityFunction function) {
		final Map<State,Map<State,String>> actionMap = transitions.get(action);
		if ( actionMap == null ) {
			return TransitionProbability.empty(initialState, action);
		}
		final Map<State,String> probabilityFunction = actionMap.get(initialState);
		if ( probabilityFunction == null ) {
			return TransitionProbability.empty(initialState, action);
		}

		TransitionProbability result;
		final ImmutableList<Object> key = ImmutableList.of(
				ProbLinearSolver.getMode(),
				initialState,
				action,
				actionMap.get(initialState).keySet(),
				getValues(actionMap.get(initialState).keySet(), function));

		if ( cache.containsKey(key) ) {
			result = cache.get(key);

			// FIXME: testar melhor URGENTEMENTE
			assert TransitionProbability.createSimple(
					initialState, action,
					solve(probabilityFunction, getRewardFor(initialState),
							function, vars.values(), restrictions))
					.equals(result);
		} else {
			final Map<State,Double> minProb = solve(probabilityFunction, getRewardFor(initialState), function, vars.values(),
					restrictions);
			try {
				result = TransitionProbability.createSimple(initialState, action, minProb);
			} catch ( final InvalidProbabilityFunctionException e ) {
				throw new IllegalStateException("Problem evaluating state " + initialState + " and action " + action + ". Log: "
						+ ProbLinearSolver.getLastFullLog(), e);
			}
			cache.put(key, result);
		}
		return result;
	}

	/**
	 * Gets the variable names associated with all transitions <currentState,action,nextState>.
	 * 
	 * @return
	 */
	Map<Trio<State,Action,State>,String> getVars() {
		return Collections.unmodifiableMap(vars);
	}

	/**
	 * 
	 * @return
	 */
	public Map<Action,Map<State,Map<State,String>>> getTransitions() {
		return Collections.unmodifiableMap(transitions);
	}

	/**
	 * Gets the restrictions applied over the transition variables.
	 * 
	 * @return
	 * @see #getVars()
	 */
	Set<String> getRestrictions() {
		return Collections.unmodifiableSet(restrictions);
	}

	private static Set<Double> getValues(final Set<State> states, final UtilityFunction func) {
		final Set<Double> values = new HashSet<>(states.size());
		for ( final State state : states ) {
			values.add(func.getUtility(state));
		}
		return values;
	}

	@Override
	public double getRewardFor(final State state) {
		return rewards.get(state);
	}

	@Override
	public double getDiscountFactor() {
		return discountFactor;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("states", states.stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList()).toString()
						.replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t").replaceFirst("\\]", "\n\\]"))
				.add("transitions", transitions.entrySet().stream().sorted(Comparator.comparing(Object::toString))
						.collect(Collectors.toList()).toString().replaceAll("}, ", "},\n\t").replaceFirst("\\[", "\\[\n\t")
						.replaceFirst("\\]", "\n\\]"))
				.add("discountFactor", discountFactor)
				.add("vars", this.vars.entrySet().stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.toList())
						.toString().replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t").replaceFirst("\\]", "\n\\]")
						.replaceFirst("\\{", "\\{\n\t").replaceFirst("\\}", "\n\\}"))
				.add("restrictions", restrictions.stream().sorted(String::compareTo).collect(Collectors.toList()).toString()
						.replaceAll(", ", ",\n\t").replaceFirst("\\[", "\\[\n\t").replaceFirst("\\]", "\n\\]"))
				.toString();
	}
}