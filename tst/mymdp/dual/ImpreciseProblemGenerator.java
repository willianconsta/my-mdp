package mymdp.dual;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.Policy;
import mymdp.core.State;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImpreciseProblemGenerator {
    private static final Logger log = LogManager.getLogger(ImpreciseProblemGenerator.class);

    private final Policy result;
    private final MDP mdp;

    public ImpreciseProblemGenerator(final Policy result, final MDP mdp) {
	this.result = result;
	this.mdp = mdp;
    }

    public void writeToFile(final String path, final State initialState, final Set<State> goalStates) {
	try (FileWriter fileWriter = new FileWriter(path)) {
	    writeStates(fileWriter);
	    writeActions(fileWriter);
	    writeRewards(fileWriter);
	    writeCosts(fileWriter);
	    writeDiscountRate(fileWriter);
	    writeInitialState(fileWriter, initialState);
	    writeGoalState(fileWriter, goalStates);
	} catch (final IOException e) {
	    log.fatal(e);
	}
    }

    private void writeGoalState(final FileWriter fileWriter, final Set<State> goalStates) throws IOException {
	fileWriter.write("goalstate\n");
	for (final State s : goalStates) {
	    fileWriter.write("\t" + s.toString() + "\n");
	}
	fileWriter.write("endgoalstate\n\n");
    }

    private void writeInitialState(final FileWriter fileWriter, final State initialState) throws IOException {
	fileWriter.write("initialstate\n");
	fileWriter.write("\t" + initialState.toString() + "\n");
	fileWriter.write("endinitialstate\n\n");
    }

    private void writeDiscountRate(final FileWriter fileWriter) throws IOException {
	fileWriter.write("discount factor " + String.format(Locale.US, "%.8f", mdp.getDiscountFactor()) + "\n\n");
    }

    private void writeRewards(final FileWriter fileWriter) throws IOException {
	fileWriter.write("reward\n");
	for (final State s : mdp.getStates()) {
	    fileWriter.write("\t" + s.toString() + " " + String.format(Locale.US, "%.8f", mdp.getRewardFor(s)) + "\n");
	}
	fileWriter.write("endreward\n\n");
    }

    private void writeCosts(final FileWriter fileWriter) throws IOException {
	fileWriter.write("cost\n");
	final Set<Action> possibleActions = new HashSet<>();
	for (final State s : mdp.getStates()) {
	    possibleActions.add(result.getActionFor(s));
	}

	for (final Action a : possibleActions) {
	    fileWriter.write("\t" + a.toString() + " " + String.format(Locale.US, "%.8f", 0.0) + "\n");
	}
	fileWriter.write("endcost\n\n");
    }

    private void writeActions(final FileWriter fileWriter) throws IOException {
	for (final Action a : mdp.getAllActions()) {
	    fileWriter.write("action " + a.toString() + "\n");
	    for (final State s : mdp.getStates()) {
		if (a.isApplyableTo(s) && result.getActionFor(s).equals(a)) {
		    for (final Entry<State, Double> entry : mdp.getPossibleStatesAndProbability(s, a).entrySet()) {
			fileWriter.write("\t" + s.toString() + " " + entry.getKey() + " "
				+ String.format(Locale.US, "%.8f", entry.getValue()) + "\n");
		    }
		}
	    }
	    fileWriter.write("endaction\n\n");
	}
    }

    private void writeStates(final FileWriter fileWriter) throws IOException {
	fileWriter.write("states\n\t");
	for (final Iterator<State> it = mdp.getStates().iterator(); it.hasNext();) {
	    final State s = it.next();
	    fileWriter.write(s.toString());
	    if (it.hasNext()) {
		fileWriter.write(",");
	    }
	}
	fileWriter.write("\nendstates\n\n");
    }

}
