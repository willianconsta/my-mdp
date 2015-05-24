package mymdp.exception;

public final class InvalidProbabilityFunctionException
	extends
		RuntimeException
{
	private static final long serialVersionUID = 1L;

	public InvalidProbabilityFunctionException(final String message) {
		super(message);
	}
}
