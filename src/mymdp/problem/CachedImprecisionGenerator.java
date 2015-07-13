package mymdp.problem;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Range;
import com.google.common.math.DoubleMath;

import mymdp.util.Trio;

/**
 * A {@code ImprecisionGenerator} implementation that maintains a internal cache of the returned ranges for each trio of initial state,
 * action and next state.
 * Values returned before will be returned again, even if the current probability is different than the probability which originated the
 * range.
 * 
 * @author Willian
 *
 */
public class CachedImprecisionGenerator
	implements
		ImprecisionGenerator
{
	private static final Logger logger = LogManager.getLogger(CachedImprecisionGenerator.class);
	private final ImprecisionGenerator generator;
	private final Map<Trio<String,String,String>,Range<Double>> cache = new HashMap<>();

	public CachedImprecisionGenerator(final ImprecisionGenerator generator) {
		this.generator = checkNotNull(generator);
	}

	@Override
	public Range<Double> generateRange(final String initial, final String action, final String next, final double actualProb) {
		final Trio<String,String,String> stateActionNext = Trio.of(initial, action, next);
		final Range<Double> cachedRange = cache.get(stateActionNext);
		if ( cachedRange != null ) {
			logger.info("Cached Trio {},{},{} for prob {} range is {}", initial, action, next, actualProb, cachedRange);
			checkState(cachedRange.contains(actualProb)
					|| DoubleMath.fuzzyEquals(cachedRange.upperEndpoint(), actualProb, 0.00001)
					|| DoubleMath.fuzzyEquals(cachedRange.lowerEndpoint(), actualProb, 0.00001),
					"Original range %s is expected to contain %s.", cachedRange, actualProb);
			return cachedRange;
		}
		final Range<Double> generatedRange = generator.generateRange(initial, action, next, actualProb);
		cache.put(stateActionNext, generatedRange);
		logger.info("Cached Trio {},{},{} for prob {} range is {}", initial, action, next, actualProb, generatedRange);
		return generatedRange;
	}

}
