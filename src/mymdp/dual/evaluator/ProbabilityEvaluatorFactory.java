package mymdp.dual.evaluator;

public final class ProbabilityEvaluatorFactory
{
	public static ProbabilityEvaluator getMaxInstance() {
		return new MaxProbabilityEvaluator();
	}

	public static ProbabilityEvaluator getMinInstance() {
		return new MinProbabilityEvaluator();
	}

	public static ProbabilityEvaluator getAnyFeasibleInstance() {
		return new AnyFeasibleProbabilityEvaluator();
	}
}
