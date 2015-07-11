package mymdp.dual;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunction;
import mymdp.util.Pair;

public class DelegateMDP
	implements
		MDP
{
	private final MDPIP mdpip;
	private final Map<Pair<State,Action>,TransitionProbability> transitions = new LinkedHashMap<>();

	public DelegateMDP(final MDPIP mdpip, final UtilityFunction function) {
		this.mdpip = mdpip;
		for ( final State s : mdpip.getStates() ) {
			for ( final Action a : mdpip.getAllActions() ) {
				transitions.put(Pair.of(s, a), mdpip.getPossibleStatesAndProbability(s, a, function));
			}
		}
	}

	@Override
	public Set<State> getStates() {
		return mdpip.getStates();
	}

	@Override
	public Set<Action> getAllActions() {
		return mdpip.getAllActions();
	}

	@Override
	public Set<Action> getActionsFor(final State state) {
		return mdpip.getActionsFor(state);
	}

	@Override
	public TransitionProbability getPossibleStatesAndProbability(final State initialState, final Action action) {
		return transitions.get(Pair.of(initialState, action));
	}

	@Override
	public double getRewardFor(final State state) {
		return mdpip.getRewardFor(state);
	}

	@Override
	public double getDiscountFactor() {
		return mdpip.getDiscountFactor();
	}

	@Override
	public int hashCode() {
		return mdpip.hashCode();
	}

	@Override
	public boolean equals(final @Nullable Object obj) {
		if ( obj == this ) {
			return true;
		}
		if ( !( obj instanceof DelegateMDP ) ) {
			return false;
		}
		final MDP other = (MDP) obj;
		return equal(this.getStates(), other.getStates())
				&& equal(this.getAllActions(), other.getAllActions())
				&& equal(this.getDiscountFactor(), other.getDiscountFactor())
				// TODO compare transitions
				;
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("states", mdpip.getStates())
				.add("transitions", transitions)
				.add("discountRate", mdpip.getDiscountFactor())
				.toString();
	}
}
