package mymdp.util;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static java.util.Objects.hash;

/**
 * A trio of elements.
 * 
 * @author Willian
 * 
 * @param <F>
 *            the first element
 * @param <S>
 *            the second element
 * @param <T>
 *            the third element
 */
public final class Trio<F, S, T>
{
	public final F first;
	public final S second;
	public final T third;

	private Trio(final F first, final S second, final T third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	/**
	 * Creates a new {@link Trio}.
	 * 
	 * @param first
	 * @param second
	 * @param third
	 * @return
	 */
	public static <F, S, T> Trio<F,S,T> of(final F first,
			final S second, final T third) {
		return new Trio<>(first, second, third);
	}

	@Override
	public int hashCode() {
		return hash(first, second, third);
	}

	@Override
	public boolean equals(final Object obj) {
		if ( obj == null || !( obj instanceof Trio<?,?,?> ) ) {
			return false;
		}
		@SuppressWarnings("unchecked") final Trio<F,S,T> other = (Trio<F,S,T>) obj;
		return equal(first, other.first)
				&& equal(second, other.second)
				&& equal(third, other.third);
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("first", first)
				.add("second", second)
				.add("third", third)
				.toString();
	}
}
