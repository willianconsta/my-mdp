package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import mymdp.core.Action;
import mymdp.core.State;

final class ActionImpl implements Action {
	private final Set<State> appliableStates;
	private final String name;

	ActionImpl(final String name, final Set<State> appliableStates) {
		checkArgument(name != null && !name.isEmpty());
		checkNotNull(appliableStates);
		this.name = name;
		this.appliableStates = appliableStates;
	}

	@Override
	public boolean isApplicableTo(final State state) {
		return appliableStates.contains(state);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Action)) {
			return false;
		}
		return name.equals(((Action) obj).name());
	}

	@Override
	public String toString() {
		return name;
	}
}