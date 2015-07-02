package mymdp.util;

import static com.google.common.base.Objects.equal;
import static java.util.Objects.hash;

public final class Pair<F, S>
{
	private final F first;
	private final S second;

	private Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F,S> of(final F first, final S second) {
		return new Pair<F,S>(first, second);
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public int hashCode() {
		return hash(getFirst(), getSecond());
	}

	@Override
	public boolean equals(final Object obj) {
		if ( obj == null || !( obj instanceof Pair<?,?> ) ) {
			return false;
		}
		@SuppressWarnings("unchecked") final Pair<F,S> other = (Pair<F,S>) obj;
		return equal(getFirst(), other.getFirst()) && equal(getSecond(), other.getSecond());
	}

	@Override
	public String toString() {
		return "Pair:(" + String.valueOf(getFirst()) + "," + String.valueOf(getSecond())
				+ ")";
	}
}
