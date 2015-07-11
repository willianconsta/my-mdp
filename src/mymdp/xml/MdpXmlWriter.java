package mymdp.xml;

import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import mymdp.core.Action;
import mymdp.core.MDP;
import mymdp.core.State;
import mymdp.xml.Mdp.Actions;
import mymdp.xml.Mdp.DiscountFactor;
import mymdp.xml.Mdp.Rewards;
import mymdp.xml.Mdp.States;
import mymdp.xml.Mdp.Transitions;
import mymdp.xml.Mdp.Transitions.TransitionFromTo;

public class MdpXmlWriter
{
	public void writeTo(final OutputStream stream, final MDP mdp) throws JAXBException {
		final JAXBContext jc = JAXBContext.newInstance("mymdp.xml");
		final Marshaller m = jc.createMarshaller();
		m.marshal(convertToXml(mdp), stream);
	}

	private static Mdp convertToXml(final MDP mdp) {
		final Mdp xmlMdp = new Mdp();
		xmlMdp.setActions(convertActions(mdp.getAllActions()));
		xmlMdp.setDiscountFactor(convertDiscountFactor(mdp.getDiscountFactor()));
		xmlMdp.setRewards(convertRewards(mdp));
		xmlMdp.setStates(convertStates(mdp.getStates()));
		xmlMdp.setTransitions(convertTransitions(mdp));
		return xmlMdp;
	}

	private static Actions convertActions(final Set<Action> allActions) {
		final Actions xmlActions = new Actions();
		final List<mymdp.xml.Action> actions = xmlActions.getAction();
		for ( final Action action : allActions ) {
			final mymdp.xml.Action xmlAction = new mymdp.xml.Action();
			xmlAction.setName(action.name());
			actions.add(xmlAction);
		}
		return xmlActions;
	}

	private static DiscountFactor convertDiscountFactor(final double discountFactor) {
		final DiscountFactor xmlDiscountFactor = new DiscountFactor();
		xmlDiscountFactor.setValue(discountFactor);
		return xmlDiscountFactor;
	}

	private static Rewards convertRewards(final MDP mdp) {
		final Rewards xmlRewards = new Rewards();
		for ( final State state : mdp.getStates() ) {
			final Reward reward = new Reward();
			reward.setState(state.name());
			reward.setValue(mdp.getRewardFor(state));
			xmlRewards.getReward().add(reward);
		}
		return xmlRewards;
	}

	private static States convertStates(final Set<State> states) {
		final States xmlStates = new States();
		for ( final State state : states ) {
			final mymdp.xml.State xmlState = new mymdp.xml.State();
			xmlState.setName(state.name());
			xmlStates.getState().add(xmlState);
		}
		return xmlStates;
	}

	private static Transitions convertTransitions(final MDP mdp) {
		final Transitions xmlTransitions = new Transitions();
		xmlTransitions.getTransitionFromTo();
		for ( final State currentState : mdp.getStates() ) {
			for ( final Action action : mdp.getActionsFor(currentState) ) {
				for ( final Entry<State,Double> transition : mdp.getPossibleStatesAndProbability(currentState, action) ) {
					final TransitionFromTo xmlFromTo = new TransitionFromTo();
					xmlFromTo.setCurrentState(currentState.name());
					xmlFromTo.setExecuting(action.name());
					xmlFromTo.setNextState(transition.getKey().name());
					xmlFromTo.setProbability(transition.getValue());
					xmlTransitions.getTransitionFromTo().add(xmlFromTo);
				}
			}
		}
		return xmlTransitions;
	}
}
