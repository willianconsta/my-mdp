package mymdp.dual;

import static com.google.common.base.Preconditions.checkState;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Range;

import mymdp.core.Action;
import mymdp.core.MDPIP;
import mymdp.core.State;
import mymdp.core.TransitionProbability;
import mymdp.core.UtilityFunctionWithProbImpl;

public class PreciseProblemGenerator
{
	private static final Logger log = LogManager.getLogger(PreciseProblemGenerator.class);

	private final UtilityFunctionWithProbImpl result;
	private final MDPIP mdpip;
	private final MDPIP fullMdpip;

	public PreciseProblemGenerator(final UtilityFunctionWithProbImpl result, final MDPIP mdpip, final MDPIP fullMdpip) {
		this.result = result;
		this.mdpip = mdpip;
		this.fullMdpip = fullMdpip;
	}

	public void writeToFile(final String path, final State initialState, final Set<State> goalStates) {
		try ( FileWriter fileWriter = new FileWriter(path) ) {
			writeStates(fileWriter);
			writeActions(fileWriter);
			writeRewards(fileWriter);
			writeCosts(fileWriter);
			writeDiscountRate(fileWriter);
			writeInitialState(fileWriter, initialState);
			writeGoalState(fileWriter, goalStates);
		} catch ( final IOException e ) {
			log.fatal(e);
		}
	}

	private static void writeGoalState(final FileWriter fileWriter, final Set<State> goalStates) throws IOException {
		fileWriter.write("goalstate\n");
		for ( final State s : goalStates ) {
			fileWriter.write("\t" + s.name() + "\n");
		}
		fileWriter.write("endgoalstate\n\n");
	}

	private static void writeInitialState(final FileWriter fileWriter, final State initialState) throws IOException {
		fileWriter.write("initialstate\n");
		fileWriter.write("\t" + initialState.name() + "\n");
		fileWriter.write("endinitialstate\n\n");
	}

	private void writeDiscountRate(final FileWriter fileWriter) throws IOException {
		fileWriter.write("discount factor " + String.format(Locale.US, "%.10f", fullMdpip.getDiscountFactor()) + "\n\n");
	}

	private void writeRewards(final FileWriter fileWriter) throws IOException {
		fileWriter.write("reward\n");
		for ( final State s : fullMdpip.getStates() ) {
			fileWriter.write("\t" + s.name() + " " + String.format(Locale.US, "%.10f", fullMdpip.getRewardFor(s)) + "\n");
		}
		fileWriter.write("endreward\n\n");
	}

	private void writeCosts(final FileWriter fileWriter) throws IOException {
		fileWriter.write("cost\n");
		for ( final Action a : fullMdpip.getAllActions() ) {
			fileWriter.write("\t" + a.name() + " " + String.format(Locale.US, "%.10f", 0.0) + "\n");
		}
		fileWriter.write("endcost\n\n");
	}

	private void writeActions(final FileWriter fileWriter) throws IOException {
		for ( final Action a : fullMdpip.getAllActions() ) {
			fileWriter.write("action " + a.name() + "\n");
			for ( final State s : fullMdpip.getStates() ) {
				if ( a.isApplicableTo(s) ) {
					double sumShouldBeOne = 0.0;
					final TransitionProbability specificTransitions = mdpip.getPossibleStatesAndProbability(s, a, result);
					if ( !specificTransitions.isEmpty() ) {
						for ( final Entry<State,Double> entry : specificTransitions ) {
							sumShouldBeOne += entry.getValue();
							fileWriter.write("\t" + s.name() + " " + entry.getKey() + " "
									+ String.format(Locale.US, "%.10f", entry.getValue()) + "\n");
						}
					} else {
						for ( final Entry<State,Double> entry : fullMdpip.getPossibleStatesAndProbability(s, a, result) ) {
							sumShouldBeOne += entry.getValue();
							fileWriter.write("\t" + s.name() + " " + entry.getKey() + " "
									+ String.format(Locale.US, "%.10f", entry.getValue()) + "\n");
						}
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
		for ( final Iterator<State> it = fullMdpip.getStates().iterator(); it.hasNext(); ) {
			final State s = it.next();
			fileWriter.write(s.name());
			if ( it.hasNext() ) {
				fileWriter.write(",");
			}
		}
		fileWriter.write("\nendstates\n\n");
	}

}
