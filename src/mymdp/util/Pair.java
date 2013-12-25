package mymdp.util;

import static com.google.common.base.Objects.equal;
import static java.util.Objects.hash;

public final class Pair<F, S> {
	public final F first;
	public final S second;

	private Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> newPair(final F first, final S second) {
		return new Pair<F, S>(first, second);
	}

	@Override
	public int hashCode() {
		return hash(first, second);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj instanceof Pair<?, ?>)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final Pair<F, S> other = (Pair<F, S>) obj;
		return equal(first, other.first) && equal(second, other.second);
	}

	@Override
	public String toString() {
		return "Pair:(" + String.valueOf(first) + "," + String.valueOf(second)
				+ ")";
	}
}
