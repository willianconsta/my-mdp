package mymdp.xml;

import static com.google.common.base.Preconditions.checkState;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import mymdp.core.MDP;
import mymdp.problem.MDPBuilder;
import mymdp.xml.Mdp.States;
import mymdp.xml.Mdp.Transitions.TransitionFromTo;

public class MdpXmlReader
{
	public MDP read(final InputStream stream) throws JAXBException {
		return convertToModel(readFromXml(stream));
	}

	private static Mdp readFromXml(final InputStream stream) throws JAXBException {
		final JAXBContext jc = JAXBContext.newInstance("mymdp.xml");
		final Unmarshaller u = jc.createUnmarshaller();
		final Mdp readMdp = ( (JAXBElement<Mdp>) u.unmarshal(stream) ).getValue();
		return readMdp;
	}

	private static MDP convertToModel(final Mdp readMdp) {
		final MDPBuilder builder = MDPBuilder.newBuilder()
				.discountRate(readMdp.getDiscountFactor().getValue())
				.states(convertStates(readMdp.getStates()))
				.rewards(readMdp.getRewards().getReward().stream().collect(Collectors.toMap(Reward::getState, Reward::getValue)));
		convertActionsAndTransitions(readMdp, builder);
		return builder.build();
	}

	private static Set<String> convertStates(final States readStates) {
		final Set<String> states = new HashSet<>();
		for ( final State readState : readStates.getState() ) {
			states.add(readState.getName());
		}
		return states;
	}

	private static void convertActionsAndTransitions(final Mdp readMdp, final MDPBuilder builder) {
		final Map<String,List<TransitionFromTo>> actionToTransitions = readMdp.getTransitions().getTransitionFromTo().stream()
				.collect(Collectors.groupingBy(TransitionFromTo::getExecuting));

		for ( final Action action : readMdp.getActions().getAction() ) {
			final List<TransitionFromTo> transitions = actionToTransitions.get(action.getName());
			checkState(transitions != null && !transitions.isEmpty());
			builder.action(action.getName(), transitions.stream()
					.map((final TransitionFromTo input) -> new String[]{input.getCurrentState(), input.getNextState(),
							String.valueOf(input.getProbability())})
					.collect(Collectors.toSet()));
		}
	}
}
