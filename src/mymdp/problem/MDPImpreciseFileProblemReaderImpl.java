package mymdp.problem;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.ImmutableList.copyOf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mymdp.core.MDPIP;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Range;

public final class MDPImpreciseFileProblemReaderImpl {
    private static Logger log = LogManager.getLogger(MDPImpreciseFileProblemReaderImpl.class);

    private boolean readingStates;
    private final Set<String> allStates;
    private final Map<String, Set<String[]>> transitions;
    private final Map<String, Double> rewards;

    private String actualAction;
    private boolean readingRewards;

    private boolean readingCosts;
    private final Map<String, Double> costs;
    private double discountFactor;
    private String initialState;
    private String goalState;
    private boolean readingInitialState;
    private boolean readingGoalState;
    private final ImprecisionGenerator generator;

    public MDPImpreciseFileProblemReaderImpl(final ImprecisionGenerator generator) {
	this.generator = generator;
	readingStates = false;
	allStates = new HashSet<>();
	actualAction = null;
	transitions = new HashMap<>();
	rewards = new HashMap<>();
	readingRewards = false;
	readingCosts = false;
	costs = new HashMap<>();
	discountFactor = -1.0;
	initialState = null;
	readingInitialState = false;
	goalState = null;
	readingGoalState = false;
    }

    private void init() {
	readingStates = false;
	allStates.clear();
	transitions.clear();
	actualAction = null;
	rewards.clear();
	readingRewards = false;
	readingCosts = false;
	costs.clear();
	discountFactor = -1.0;
	initialState = null;
	readingInitialState = false;
	goalState = null;
	readingGoalState = false;
    }

    public MDPIP readFromFile(final String absoluteFilepath) {
	init();
	try (BufferedReader reader = new BufferedReader(new FileReader(absoluteFilepath))) {
	    String line;
	    while ((line = reader.readLine()) != null) {
		log.trace(line);
		final String trimmedLine = line.trim();
		readStates(trimmedLine);
		readActions(trimmedLine);
		readRewards(trimmedLine);
		readCosts(trimmedLine);
		readDiscountFactor(trimmedLine);
		readInitialState(trimmedLine);
		readGoalState(trimmedLine);
	    }
	} catch (final IOException e) {
	    log.fatal(e);
	}

	log.trace(allStates);
	log.trace(transitions);
	log.trace(rewards);
	log.trace(costs);
	log.trace(discountFactor);
	log.trace(initialState);
	log.trace(goalState);
	final MDPIPBuilder builder = new MDPIPBuilder();
	builder.states(allStates).reward(rewards);
	for (final Entry<String, Set<String[]>> entry : transitions.entrySet()) {
	    builder.actions(entry.getKey(), entry.getValue());
	}
	builder.discountRate(discountFactor);
	return builder.build();
    }

    private void readDiscountFactor(final String trimmedLine) {
	if (trimmedLine.startsWith("discount factor")) {
	    discountFactor = Double.parseDouble(trimmedLine.replace("discount factor", "").trim());
	}
    }

    private void readInitialState(final String trimmedLine) {
	if (trimmedLine.startsWith("initialstate")) {
	    readingInitialState = true;
	    return;
	}

	if (readingInitialState) {
	    if (trimmedLine.equals("endinitialstate")) {
		readingInitialState = false;
		return;
	    }

	    initialState = trimmedLine;
	    return;
	}
    }

    private void readGoalState(final String trimmedLine) {
	if (trimmedLine.startsWith("goalstate")) {
	    readingGoalState = true;
	    return;
	}

	if (readingGoalState) {
	    if (trimmedLine.equals("endgoalstate")) {
		readingGoalState = false;
		return;
	    }

	    checkState(allStates.contains(trimmedLine));
	    goalState = trimmedLine;
	    return;
	}
    }

    private void readActions(final String trimmedLine) {
	if (trimmedLine.startsWith("action")) {
	    actualAction = trimmedLine.replace("action", "").trim();
	    return;
	}

	if (actualAction != null) {
	    if (trimmedLine.equals("endaction")) {
		actualAction = null;
		return;
	    }

	    final String[] actionTransitions = trimmedLine.split(" ");
	    checkState(allStates.contains(actionTransitions[0]));
	    checkState(allStates.contains(actionTransitions[1]));
	    final double prob = Double.parseDouble(actionTransitions[2]);
	    final Range<Double> range = generator.generateRange(actionTransitions[0], actualAction, actionTransitions[1], prob);
	    Set<String[]> set = transitions.get(actualAction);
	    if (set == null) {
		set = new HashSet<>();
		transitions.put(actualAction, set);
	    }

	    set.add(new String[] { actionTransitions[0], actionTransitions[1], range.lowerEndpoint().toString(),
		    range.upperEndpoint().toString() });
	    return;
	}
    }

    private void readRewards(final String trimmedLine) {
	if (trimmedLine.equals("reward")) {
	    readingRewards = true;
	    return;
	}

	if (readingRewards) {
	    if (trimmedLine.equals("endreward")) {
		readingRewards = false;
		return;
	    }

	    final String[] stateReward = trimmedLine.split(" ");
	    checkState(allStates.contains(stateReward[0]));
	    rewards.put(stateReward[0], Double.parseDouble(stateReward[1]));
	    return;
	}
    }

    private void readCosts(final String trimmedLine) {
	if (trimmedLine.equals("cost")) {
	    readingCosts = true;
	    return;
	}

	if (readingCosts) {
	    if (trimmedLine.equals("endcost")) {
		readingCosts = false;
		return;
	    }

	    final String[] actionCost = trimmedLine.split(" ");
	    checkState(transitions.containsKey(actionCost[0]),
		    "Action " + actionCost[0] + " undefined. Possible actions = " + transitions.keySet());
	    costs.put(actionCost[0], Double.parseDouble(actionCost[1]));
	    return;
	}
    }

    private void readStates(final String trimmedLine) {
	if (readingStates) {
	    if (trimmedLine.equals("endstates")) {
		readingStates = false;
	    } else {
		allStates.addAll(copyOf(trimmedLine.replace(" ", "").split(",")));
	    }
	} else if (trimmedLine.equals("states")) {
	    readingStates = true;
	}
    }
}
