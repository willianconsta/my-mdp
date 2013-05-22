package mymdp.problem;

import static com.google.common.base.Preconditions.checkArgument;
import static mymdp.util.Pair.newPair;

import java.util.HashMap;
import java.util.Map;

import mymdp.util.Pair;

import com.google.common.collect.Range;

public class ImprecisionGeneratorImpl implements ImprecisionGenerator {
    private static final Range<Double> probabilityRange = Range.closed(0.0, 1.0);
    final Map<Pair<String, String>, Map<String, Range<Double>>> cache;
    private final double variation;

    public ImprecisionGeneratorImpl(final double variation) {
	checkArgument(probabilityRange.contains(Double.valueOf(variation)));
	this.variation = variation;
	this.cache = new HashMap<>();
    }

    @Override
    public Range<Double> generateRange(final String initial, final String action, final String next, final double actualProb) {
	final Pair<String, String> stateAction = newPair(initial, action);
	Map<String, Range<Double>> consequences = cache.get(stateAction);
	if (consequences == null) {
	    consequences = new HashMap<>();
	    cache.put(stateAction, consequences);
	}
	final Range<Double> probRange = Range.closed(actualProb - variation, actualProb + variation).intersection(probabilityRange);
	consequences.put(next, probRange);
	return probRange;
    }

}
