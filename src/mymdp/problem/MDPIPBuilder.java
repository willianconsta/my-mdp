package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Range;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.util.Trio;

public class MDPIPBuilder
{
	private final Map<String,StateImpl> states = new LinkedHashMap<>();
	private final Map<StateImpl,Double> rewards = new LinkedHashMap<>();
	private final Map<Action,Map<State,Map<State,String>>> transitions = new LinkedHashMap<>();
	private double discountRate;
	private Set<String> restrictions = new LinkedHashSet<>();
	private final Map<Trio<State,Action,State>,String> vars = new LinkedHashMap<>();
	private int i = 0;

	private MDPIPBuilder() {
	}

	public static MDPIPBuilder newBuilder() {
		return new MDPIPBuilder();
	}

	public static MDPIP createFromMDP(final MDP mdp, final ImprecisionGenerator generator) {
		final MDPIPBuilder builder = newBuilder()
				.states(mdp.getStates().stream().map(State::name).collect(Collectors.toSet()))
				.discountRate(mdp.getDiscountFactor())
				.rewards(mdp.getStates().stream().collect(Collectors.toMap(State::name, s -> mdp.getRewardFor(s))));

		for ( final Action a : mdp.getAllActions() ) {
			final Set<String[]> transitionDefs = new HashSet<>();
			for ( final State s : mdp.getStates() ) {
				if ( !a.isApplicableTo(s) ) {
					continue;
				}

				for ( final Entry<State,Double> transition : mdp.getPossibleStatesAndProbability(s, a) ) {
					final Range<Double> range = generator.generateRange(s.name(), a.name(), transition.getKey().name(), transition.getValue());
					transitionDefs.add(new String[]{s.name(), transition.getKey().name(), range.lowerEndpoint().toString(),
							range.upperEndpoint().toString()});
				}
			}
			builder.action(a.name(), transitionDefs);
		}

		return builder.build();
	}

	public MDPIPBuilder states(final Set<String> stateDefs) {
		for ( final String stateDef : stateDefs ) {
			states.put(stateDef, new StateImpl(stateDef));
		}
		return this;
	}

	/**
	 * 
	 * @param actionName
	 * @param transitionDefs
	 *            0th index current state, 1st index next state, 2nd and 3rd indexes are double values related to the probability.
	 *            <ul>
	 *            <li>If 2nd index is equal to 3rd index then the value must be a double and is the value of the probability of transition</li>
	 *            <li>If 2nd index is not equal to "0.0" then a restriction of var >= (value of 2nd index) is created.</li>
	 *            <li>If 3rd index is not equal to "1.0" then a restriction of var <= (value of 3rd index) is created.</li>
	 *            <li>All variables related to a current state must sum one.</li>
	 *            </ul>
	 * @return
	 */
	public MDPIPBuilder action(final String actionName, final Set<String[]> transitionDefs) {
		final Set<State> appliableStates = new LinkedHashSet<>();
		final Action action = new ActionImpl(actionName, appliableStates);
		final Map<State,String> sumOnePerState = new LinkedHashMap<>();
		final Map<String,Double> lowestSum = new LinkedHashMap<>();
		final Map<String,Double> greatestSum = new LinkedHashMap<>();

		for ( final String[] transitionDef : transitionDefs ) {
			final State state1 = checkNotNull(states.get(transitionDef[0]));
			appliableStates.add(state1);

			Map<State,Map<State,String>> probs = transitions.get(action);
			if ( probs == null ) {
				probs = new LinkedHashMap<>();
				transitions.put(action, probs);
			}

			final State state2 = checkNotNull(states.get(transitionDef[1]));
			Map<State,String> map = probs.get(state1);
			if ( map == null ) {
				map = new LinkedHashMap<>();
				probs.put(state1, map);
			}

			final String var = "p" + i++;
			map.put(state2, checkNotNull(var));

			lowestSum.put(transitionDef[0], ( lowestSum.containsKey(transitionDef[0]) ? lowestSum.get(transitionDef[0]) : 0 )
					+ Double.parseDouble(transitionDef[2]));
			greatestSum.put(transitionDef[0], ( greatestSum.containsKey(transitionDef[0]) ? greatestSum.get(transitionDef[0]) : 0 )
					+ Double.parseDouble(transitionDef[3]));
			if ( transitionDef[2].equals(transitionDef[3]) ) {
				map.put(state2, transitionDef[3]);
				continue;
			}

			if ( !transitionDef[2].equals("0.0") ) {
				restrictions.add(var + " >= " + transitionDef[2]);
			}
			if ( !transitionDef[3].equals("1.0") ) {
				restrictions.add(var + " <= " + transitionDef[3]);
			}
			sumOnePerState.merge(state1, var, (oldValue, newValue) -> oldValue + "+" + newValue);
			vars.put(Trio.of(state1, action, state2), var);
		}

		checkState(Collections.max(lowestSum.values()) <= 1, "Expected leq than 1, got " + lowestSum);
		checkState(Collections.min(greatestSum.values()) >= 1, "Expected geq than 1, got " + greatestSum);

		// restrictions of sum one
		for ( final Entry<State,String> sumOne : sumOnePerState.entrySet() ) {
			if ( sumOne.getValue().indexOf('p') < sumOne.getValue().lastIndexOf('p') ) {
				// if there is more than one variable force their sum to be one.
				restrictions.add(sumOne.getValue() + "=1");
			} else {
				// if there is only one variable it must be one.
				final String var = sumOne.getValue();
				checkState(vars.values().remove(var));
				// removes other restrictions over the variable being removed
				for ( final Iterator<String> it = restrictions.iterator(); it.hasNext(); ) {
					final String restriction = it.next();
					if ( restriction.matches(".*?" + var + "(\\b|[^\\d].*?)") ) {
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

	public MDPIPBuilder rewards(final Map<String,Double> rewards) {
		for ( final Entry<String,Double> entry : rewards.entrySet() ) {
			this.rewards.put(states.get(entry.getKey()), entry.getValue());
		}
		return this;
	}

	public MDPIPBuilder discountRate(final double rate) {
		checkArgument(Range.closed(0.0, 1.0).contains(rate));
		this.discountRate = rate;
		return this;
	}

	public MDPIP build() {
		return new MDPIPImpl(states.values(), rewards, transitions, restrictions, vars, discountRate);
	}
}
