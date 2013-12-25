package mymdp.solver;

import static mymdp.solver.BellmanUtils.calculateUtility;
import static mymdp.solver.BellmanUtils.getGreedyActionForState;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class RTDPImpl implements RTDP {

	@Override
	public UtilityFunction solve(final MDP mdp) {
		final int size = randomSeed.nextInt(mdp.getStates().size());
		final Set<State> initials = Sets.newLinkedHashSetWithExpectedSize(size);
		for (int i = 0; i < size; i++) {
			initials.add(Iterables.get(mdp.getStates(), i));
		}
		return solve(mdp, initials, new UtilityFunctionImpl(mdp.getStates(),
				Double.MAX_VALUE));
	}

	@Override
	public UtilityFunction solve(final MDP mdp, final Set<State> initials) {
		return solve(mdp, initials, new UtilityFunctionImpl(mdp.getStates(),
				Double.MAX_VALUE));
	}

	@Override
	public UtilityFunction solve(final MDP mdp, final Set<State> initials,
			final long maxDepth) {
		return solve(mdp, initials, Collections.<State> emptySet(), maxDepth,
				new UtilityFunctionImpl(mdp.getStates(), 1.0));
	}

	@Override
	public UtilityFunction solve(final MDP mdp, final Set<State> initials,
			final UtilityFunction initialValues) {
		return solve(mdp, initials, Collections.<State> emptySet(),
				initialValues);
	}

	@Override
	public UtilityFunction solve(final MDP mdp, final Set<State> initials,
			final Set<State> goals, final UtilityFunction initialValues) {
		return solve(mdp, initials, goals, Long.MAX_VALUE, initialValues);
	}

	@Override
	public UtilityFunction solve(final MDP mdp, final Set<State> initials,
			final Set<State> goals, final long maxDepth,
			final UtilityFunction initialValues) {
		UtilityFunction actualUpper = new UtilityFunctionImpl(initialValues);
		while (!convergencyCriteria.hasConverged()) {
			long depth = 0;
			final Deque<State> visitedStates = Lists.newLinkedList();
			State s = pickOneAtRandom(initials);
			while (s != null && depth < maxDepth && !goals.contains(s)) {
				depth++;
				visitedStates.push(s);
				actualUpper = backup(actualUpper, s, mdp);
				final Action a = getGreedyActionForState(s, actualUpper, mdp).first;
				assert a != null;
				s = chooseNextState(s, a, mdp);
			}

			for (final Iterator<State> it = visitedStates.descendingIterator(); it
					.hasNext();) {
				final State state = it.next();
				actualUpper = backup(actualUpper, state, mdp);
			}
		}
		return actualUpper;
	}

	private final ConvergencyCriteria convergencyCriteria;
	private static final Random randomSeed = new Random(1234);

	public RTDPImpl(final ConvergencyCriteria criteria) {
		this.convergencyCriteria = criteria;
	}

	private State chooseNextState(final State s, final Action a, final MDP mdp) {
		final double random = randomSeed.nextDouble();
		double accumulated = 0.0;
		for (final Entry<State, Double> entry : mdp.getPossibleStatesAndProbability(s, a)) {
			if (random < accumulated + entry.getValue().doubleValue()) {
				return entry.getKey();
			}
			accumulated += entry.getValue().doubleValue();
		}
		return null;
	}

	private UtilityFunction backup(final UtilityFunction actualUpper,
			final State s, final MDP mdp) {
		final UtilityFunction updated = new UtilityFunctionImpl(actualUpper);
		updated.updateUtility(s, calculateUtility(mdp, s, updated));
		return updated;
	}

	private static <T> T pickOneAtRandom(final Set<T> set) {
		return Iterables.get(set, 0);
	}
}
