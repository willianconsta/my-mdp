package mymdp.problem;

import java.util.Map;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.util.Pair;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

public final class ProbabilityRestrictionUtils {
	private ProbabilityRestrictionUtils() {
		throw new UnsupportedOperationException();
	}

	public static Pair<Set<String>, Set<String>> readProbabilityRestrictions(final MDPIP mdpip) {
		final MDPIPImpl impl = (MDPIPImpl) mdpip;
		return Pair.<Set<String>, Set<String>> of(ImmutableSet.copyOf(impl.getVars().values()), impl.getRestrictions());
	}

	public static Multimap<Action, State> nextStates(final MDPIP mdpip, final State state) {
		final MDPIPImpl impl = (MDPIPImpl) mdpip;
		final Set<Action> actionsFor = impl.getActionsFor(state);

		final Multimap<Action, State> result = HashMultimap.create();
		for (final Action action : actionsFor) {
			final Map<State, Map<State, String>> currentToNext = impl.getTransitions().get(action);
			result.putAll(action, currentToNext.get(state).keySet());
		}

		return result;
	}

	public static String transitionVariable(final MDPIP mdpip, final State current, final Action action, final State next) {
		final MDPIPImpl impl = (MDPIPImpl) mdpip;
		return impl.getTransitions().get(action).get(current).get(next);
	}

	public static Set<String> getAllVars(final MDPIP mdpip) {
		final MDPIPImpl impl = (MDPIPImpl) mdpip;
		return ImmutableSet.copyOf(impl.getVars().values());
	}
}
