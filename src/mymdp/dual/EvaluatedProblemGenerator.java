package mymdp.dual;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;

public class EvaluatedProblemGenerator
{
	private static final Logger log = LogManager.getLogger(PreciseProblemGenerator.class);

	private final MDPIP mdpip;

	public EvaluatedProblemGenerator(final MDPIP mdpip) {
		this.mdpip = checkNotNull(mdpip);
	}

	public void writeToFile(final String path, final State initialState, final Set<State> goalStates) {
		try ( final FileWriter fileWriter = new FileWriter(path) ) {
			writeStates(fileWriter);
			writeActions(fileWriter);
			writeRewards(fileWriter);
			writeCosts(fileWriter);
			writeDiscountRate(fileWriter);
			writeInitialState(fileWriter, initialState);
			writeGoalState(fileWriter, goalStates);
		} catch ( final IOException e ) {
			log.fatal("Failed to write to file.", e);
		}
	}

	private void writeGoalState(final FileWriter fileWriter, final Set<State> goalStates) throws IOException {
		fileWriter.write("goalstate\n");
		for ( final State s : goalStates ) {
			fileWriter.write("\t" + s.name() + "\n");
		}
		fileWriter.write("endgoalstate\n\n");
	}

	private void writeInitialState(final FileWriter fileWriter, final State initialState) throws IOException {
		fileWriter.write("initialstate\n");
		fileWriter.write("\t" + initialState.name() + "\n");
		fileWriter.write("endinitialstate\n\n");
	}

	private void writeDiscountRate(final FileWriter fileWriter) throws IOException {
		fileWriter.write("discount factor " + String.format(Locale.US, "%.10f", mdpip.getDiscountFactor()) + "\n\n");
	}

	private void writeRewards(final FileWriter fileWriter) throws IOException {
		fileWriter.write("reward\n");
		for ( final State s : mdpip.getStates() ) {
			fileWriter.write("\t" + s.name() + " " + String.format(Locale.US, "%.10f", mdpip.getRewardFor(s)) + "\n");
		}
		fileWriter.write("endreward\n\n");
	}

	private void writeCosts(final FileWriter fileWriter) throws IOException {
		fileWriter.write("cost\n");
		for ( final Action a : mdpip.getAllActions() ) {
			fileWriter.write("\t" + a.name() + " " + String.format(Locale.US, "%.10f", 0.0) + "\n");
		}
		fileWriter.write("endcost\n\n");
	}

	private void writeActions(final FileWriter fileWriter) throws IOException {
		final UtilityFunction values = new UtilityFunctionImpl(mdpip.getStates());
		for ( final Action a : mdpip.getAllActions() ) {
			fileWriter.write("action " + a.name() + "\n");
			for ( final State s : mdpip.getStates() ) {
				if ( a.isApplicableTo(s) ) {
					double sumShouldBeOne = 0.0;
					for ( final Entry<State,Double> entry : mdpip.getPossibleStatesAndProbability(s, a, values) ) {
						sumShouldBeOne += entry.getValue();
						fileWriter.write("\t" + s.name() + " " + entry.getKey().name() + " "
								+ String.format(Locale.US, "%.10f", entry.getValue()) + "\n");
					}
					checkState(Math.abs(sumShouldBeOne - 1.0) < 0.01,
							"Action %s in state %s has total probability of %s to go some state.",
							a, s, sumShouldBeOne);
					checkState(!mdpip.getActionsFor(s).isEmpty(), "At least one action should be possible for %s", s);
				}
			}
			fileWriter.write("endaction\n\n");
		}
	}

	private void writeStates(final FileWriter fileWriter) throws IOException {
		fileWriter.write("states\n\t");
		for ( final Iterator<State> it = mdpip.getStates().iterator(); it.hasNext(); ) {
			final State s = it.next();
			fileWriter.write(s.name());
			if ( it.hasNext() ) {
				fileWriter.write(",");
			}
		}
		fileWriter.write("\nendstates\n\n");
	}

}
