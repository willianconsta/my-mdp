package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Range;

public class ImprecisionGeneratorImpl
	implements
		ImprecisionGenerator
{
	private static final Logger logger = LogManager.getLogger(ImprecisionGeneratorImpl.class);
	private static final Range<Double> probabilityRange = Range.closed(0.0, 1.0);
	private final double variation;

	public ImprecisionGeneratorImpl(final double variation) {
		checkArgument(probabilityRange.contains(Double.valueOf(variation)));
		this.variation = variation;
	}

	@Override
	public Range<Double> generateRange(final String initial, final String action, final String next, final double actualProb) {
		final Range<Double> probRange = Range.closed(actualProb - variation, actualProb + variation).intersection(probabilityRange);
		logger.debug("Generated! For {},{},{} actual prob {} a range {}", initial, action, next, actualProb, probRange);
		return probRange;
	}

}
