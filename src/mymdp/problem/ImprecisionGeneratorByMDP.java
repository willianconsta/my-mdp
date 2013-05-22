package mymdp.problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;

import com.google.common.collect.Range;

public class ImprecisionGeneratorByMDP implements ImprecisionGenerator {
    private final Map<String, Map<String, Map<String, Range<Double>>>> transitions;
    private final double stepVariation;

    public ImprecisionGeneratorByMDP(final MDP mdp, final double maxVariation, final double stepVariation) {
	this.stepVariation = stepVariation;
	this.transitions = new HashMap<>();
	for (final State s : mdp.getStates()) {
	    for (final Action a : mdp.getActionsFor(s)) {
		for (final Entry<State, Double> entry : mdp.getPossibleStatesAndProbability(s, a).entrySet()) {
		    Map<String, Map<String, Range<Double>>> map = transitions.get(s.toString());
		    if (map == null) {
			map = new HashMap<>();
			transitions.put(s.toString(), map);
		    }
		    Map<String, Range<Double>> probs = map.get(a.toString());
		    if (probs == null) {
			probs = new HashMap<>();
			map.put(a.toString(), probs);
		    }
		    probs.put(
			    entry.getKey().toString(),
			    Range.closed(0.0, 1.0).intersection(
				    Range.closed(entry.getValue() - maxVariation, entry.getValue() + maxVariation)));
		}
	    }
	}
    }

    @Override
    public Range<Double> generateRange(final String initial, final String action, final String next, final double actualProb) {
	final Range<Double> prob = transitions.get(initial).get(action).get(next);
	return prob.intersection(Range.closed(actualProb - stepVariation, actualProb + stepVariation));
    }
}
