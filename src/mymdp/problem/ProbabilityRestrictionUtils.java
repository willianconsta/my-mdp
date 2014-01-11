package mymdp.problem;

import java.util.Set;

import mymdp.core.MDPIP;
import mymdp.util.Pair;

import com.google.common.collect.ImmutableSet;

public final class ProbabilityRestrictionUtils {
	private ProbabilityRestrictionUtils() {
		throw new UnsupportedOperationException();
	}

	public static Pair<Set<String>, Set<String>> readProbabilityRestrictions(final MDPIP mdpip) {
		final MDPIPImpl impl = (MDPIPImpl) mdpip;
		return Pair.<Set<String>, Set<String>> of(ImmutableSet.copyOf(impl.getVars().values()), impl.getRestrictions());
	}
}
