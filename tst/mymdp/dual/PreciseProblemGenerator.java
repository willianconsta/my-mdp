package mymdp.dual;

import static com.google.common.base.Preconditions.checkState;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunctionWithProbImpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Range;

public class PreciseProblemGenerator {
    private static final Logger log = LogManager.getLogger(PreciseProblemGenerator.class);

    private final UtilityFunctionWithProbImpl result;
    private final MDPIP mdpip;

    public PreciseProblemGenerator(final UtilityFunctionWithProbImpl result, final MDPIP mdpip) {
	this.result = result;
	this.mdpip = mdpip;
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
	fileWriter.write("discount factor " + String.format(Locale.US, "%.10f", mdpip.getDiscountFactor()) + "\n\n");
    }

    private void writeRewards(final FileWriter fileWriter) throws IOException {
	fileWriter.write("reward\n");
	for (final State s : mdpip.getStates()) {
	    fileWriter.write("\t" + s.toString() + " " + String.format(Locale.US, "%.10f", mdpip.getRewardFor(s)) + "\n");
	}
	fileWriter.write("endreward\n\n");
    }

    private void writeCosts(final FileWriter fileWriter) throws IOException {
	fileWriter.write("cost\n");
	for (final Action a : mdpip.getAllActions()) {
	    fileWriter.write("\t" + a.toString() + " " + String.format(Locale.US, "%.10f", 0.0) + "\n");
	}
	fileWriter.write("endcost\n\n");
    }

    private void writeActions(final FileWriter fileWriter) throws IOException {
	for (final Action a : mdpip.getAllActions()) {
	    fileWriter.write("action " + a.toString() + "\n");
	    for (final State s : mdpip.getStates()) {
		if (a.isApplyableTo(s)) {
		    double sumShouldBeOne = 0.0;
		    for (final Entry<State, Double> entry : mdpip.getPossibleStatesAndProbability(s, a, result).entrySet()) {
			sumShouldBeOne += entry.getValue();
			fileWriter.write("\t" + s.toString() + " " + entry.getKey() + " "
				+ String.format(Locale.US, "%.10f", entry.getValue()) + "\n");
		    }
		    checkState(Range.closed(0.0, 1.0).contains(sumShouldBeOne), "Action " + a + " in state " + s
			    + " has total probability of " + sumShouldBeOne + " to go some state.");
		}
	    }
	    fileWriter.write("endaction\n\n");
	}
    }

    private void writeStates(final FileWriter fileWriter) throws IOException {
	fileWriter.write("states\n\t");
	for (final Iterator<State> it = mdpip.getStates().iterator(); it.hasNext();) {
	    final State s = it.next();
	    fileWriter.write(s.toString());
	    if (it.hasNext()) {
		fileWriter.write(",");
	    }
	}
	fileWriter.write("\nendstates\n\n");
    }

}
