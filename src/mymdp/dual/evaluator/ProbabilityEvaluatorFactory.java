package mymdp.dual.evaluator;

public final class ProbabilityEvaluatorFactory {
	public static ProbabilityEvaluator getMaxInstance(final String fullFilename) {
		return new MaxProbabilityEvaluator(fullFilename);
	}

	public static ProbabilityEvaluator getMinInstance(final String fullFilename) {
		return new MinProbabilityEvaluator(fullFilename);
	}

	public static ProbabilityEvaluator getAnyFeasibleInstance(final String fullFilename) {
		return new AnyFeasibleProbabilityEvaluator(fullFilename);
	}
}
