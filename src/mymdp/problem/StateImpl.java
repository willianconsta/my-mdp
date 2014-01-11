package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import mymdp.core.State;

final class StateImpl implements State {
	private final String name;

	StateImpl(final String name) {
		checkArgument(name != null && !name.isEmpty());
		this.name = name;
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
		if (!(obj instanceof State)) {
			return false;
		}
		return name.equals(((State) obj).name());
	}

	@Override
	public String toString() {
		return name;
	}
}