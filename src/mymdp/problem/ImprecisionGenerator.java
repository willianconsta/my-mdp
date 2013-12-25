package mymdp.problem;

import com.google.common.collect.Range;

public interface ImprecisionGenerator {
	Range<Double> generateRange(String initial, String action, String next, double actualProb);
}