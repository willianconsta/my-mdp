package mymdp.problem;

import static com.google.common.base.Preconditions.checkState;
import static mymdp.util.Pair.newPair;

import com.google.common.collect.Range;
import com.google.common.math.DoubleMath;

public class ImprecisionGeneratorByRanges
	implements ImprecisionGenerator {
    private final ImprecisionGeneratorImpl generator;
    private final double stepVariation;

    public ImprecisionGeneratorByRanges(final ImprecisionGeneratorImpl generator, final double stepVariation) {
	this.generator = generator;
	this.stepVariation = stepVariation;
    }

    @Override
    public Range<Double> generateRange(final String initial, final String action, final String next, final double actualProb) {
	final Range<Double> originalRange = generator.cache.get(newPair(initial, action)).get(next);
	checkState(
		DoubleMath.fuzzyEquals(actualProb, originalRange.lowerEndpoint(), 0.000001)
			|| DoubleMath.fuzzyEquals(actualProb, originalRange.upperEndpoint(), 0.000001)
			|| originalRange.contains(actualProb), actualProb + " is not in " + originalRange);
	if (DoubleMath.fuzzyEquals(actualProb, originalRange.lowerEndpoint(), 0.000001)) {
	    return Range.closed(originalRange.lowerEndpoint(), actualProb + stepVariation);
	}
	if (DoubleMath.fuzzyEquals(actualProb, originalRange.upperEndpoint(), 0.000001)) {
	    return Range.closed(actualProb - stepVariation, originalRange.upperEndpoint());
	}
	return Range.closed(actualProb - stepVariation, actualProb + stepVariation).intersection(
		originalRange);
    }

}
