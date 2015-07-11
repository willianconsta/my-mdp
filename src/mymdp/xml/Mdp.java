//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2015.07.06 às 01:02:42 AM BRT 
//

package mymdp.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java de mdp complex type.
 * 
 * <p>
 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="mdp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="discountFactor">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="states">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="state" type="{https://github.com/willianconsta/my-mdp}state" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="actions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="action" type="{https://github.com/willianconsta/my-mdp}action" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="rewards">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="reward" type="{https://github.com/willianconsta/my-mdp}reward" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="transitions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element name="transitionFromTo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="currentState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="executing" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="nextState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="probability" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mdp", propOrder = {

})
public class Mdp
{

	@XmlElement(required = true)
	protected Mdp.DiscountFactor discountFactor;
	@XmlElement(required = true)
	protected Mdp.States states;
	@XmlElement(required = true)
	protected Mdp.Actions actions;
	@XmlElement(required = true)
	protected Mdp.Rewards rewards;
	@XmlElement(required = true)
	protected Mdp.Transitions transitions;

	/**
	 * Obtém o valor da propriedade discountFactor.
	 * 
	 * @return possible object is {@link Mdp.DiscountFactor }
	 * 
	 */
	public Mdp.DiscountFactor getDiscountFactor() {
		return discountFactor;
	}

	/**
	 * Define o valor da propriedade discountFactor.
	 * 
	 * @param value
	 *            allowed object is {@link Mdp.DiscountFactor }
	 * 
	 */
	public void setDiscountFactor(final Mdp.DiscountFactor value) {
		this.discountFactor = value;
	}

	/**
	 * Obtém o valor da propriedade states.
	 * 
	 * @return possible object is {@link Mdp.States }
	 * 
	 */
	public Mdp.States getStates() {
		return states;
	}

	/**
	 * Define o valor da propriedade states.
	 * 
	 * @param value
	 *            allowed object is {@link Mdp.States }
	 * 
	 */
	public void setStates(final Mdp.States value) {
		this.states = value;
	}

	/**
	 * Obtém o valor da propriedade actions.
	 * 
	 * @return possible object is {@link Mdp.Actions }
	 * 
	 */
	public Mdp.Actions getActions() {
		return actions;
	}

	/**
	 * Define o valor da propriedade actions.
	 * 
	 * @param value
	 *            allowed object is {@link Mdp.Actions }
	 * 
	 */
	public void setActions(final Mdp.Actions value) {
		this.actions = value;
	}

	/**
	 * Obtém o valor da propriedade rewards.
	 * 
	 * @return possible object is {@link Mdp.Rewards }
	 * 
	 */
	public Mdp.Rewards getRewards() {
		return rewards;
	}

	/**
	 * Define o valor da propriedade rewards.
	 * 
	 * @param value
	 *            allowed object is {@link Mdp.Rewards }
	 * 
	 */
	public void setRewards(final Mdp.Rewards value) {
		this.rewards = value;
	}

	/**
	 * Obtém o valor da propriedade transitions.
	 * 
	 * @return possible object is {@link Mdp.Transitions }
	 * 
	 */
	public Mdp.Transitions getTransitions() {
		return transitions;
	}

	/**
	 * Define o valor da propriedade transitions.
	 * 
	 * @param value
	 *            allowed object is {@link Mdp.Transitions }
	 * 
	 */
	public void setTransitions(final Mdp.Transitions value) {
		this.transitions = value;
	}

	/**
	 * <p>
	 * Classe Java de anonymous complex type.
	 * 
	 * <p>
	 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="action" type="{https://github.com/willianconsta/my-mdp}action" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"action"})
	public static class Actions
	{

		@XmlElement(required = true)
		protected List<Action> action;

		/**
		 * Gets the value of the action property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the action property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getAction().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link Action }
		 * 
		 * 
		 */
		public List<Action> getAction() {
			if ( action == null ) {
				action = new ArrayList<>();
			}
			return this.action;
		}

	}

	/**
	 * <p>
	 * Classe Java de anonymous complex type.
	 * 
	 * <p>
	 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "")
	public static class DiscountFactor
	{

		@XmlAttribute(name = "value", required = true)
		protected double value;

		/**
		 * Obtém o valor da propriedade value.
		 * 
		 */
		public double getValue() {
			return value;
		}

		/**
		 * Define o valor da propriedade value.
		 * 
		 */
		public void setValue(final double value) {
			this.value = value;
		}

	}

	/**
	 * <p>
	 * Classe Java de anonymous complex type.
	 * 
	 * <p>
	 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="reward" type="{https://github.com/willianconsta/my-mdp}reward" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"reward"})
	public static class Rewards
	{

		@XmlElement(required = true)
		protected List<Reward> reward;

		/**
		 * Gets the value of the reward property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the reward property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getReward().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link Reward }
		 * 
		 * 
		 */
		public List<Reward> getReward() {
			if ( reward == null ) {
				reward = new ArrayList<>();
			}
			return this.reward;
		}

	}

	/**
	 * <p>
	 * Classe Java de anonymous complex type.
	 * 
	 * <p>
	 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="state" type="{https://github.com/willianconsta/my-mdp}state" maxOccurs="unbounded"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"state"})
	public static class States
	{

		@XmlElement(required = true)
		protected List<State> state;

		/**
		 * Gets the value of the state property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the state property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getState().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link State }
		 * 
		 * 
		 */
		public List<State> getState() {
			if ( state == null ) {
				state = new ArrayList<>();
			}
			return this.state;
		}

	}

	/**
	 * <p>
	 * Classe Java de anonymous complex type.
	 * 
	 * <p>
	 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;choice>
	 *         &lt;element name="transitionFromTo" maxOccurs="unbounded">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;attribute name="currentState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="executing" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="nextState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
	 *                 &lt;attribute name="probability" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
	 *         &lt;/element>
	 *       &lt;/choice>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = {
			"transitionFromTo"})
	public static class Transitions
	{

		protected List<Mdp.Transitions.TransitionFromTo> transitionFromTo;

		/**
		 * Gets the value of the transitionFromTo property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the transitionFromTo property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getTransitionFromTo().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list {@link Mdp.Transitions.TransitionFromTo }
		 * 
		 * 
		 */
		public List<Mdp.Transitions.TransitionFromTo> getTransitionFromTo() {
			if ( transitionFromTo == null ) {
				transitionFromTo = new ArrayList<>();
			}
			return this.transitionFromTo;
		}

		/**
		 * <p>
		 * Classe Java de anonymous complex type.
		 * 
		 * <p>
		 * O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
		 * 
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;attribute name="currentState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="executing" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="nextState" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
		 *       &lt;attribute name="probability" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 * 
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "")
		public static class TransitionFromTo
		{

			@XmlAttribute(name = "currentState", required = true)
			protected String currentState;
			@XmlAttribute(name = "executing", required = true)
			protected String executing;
			@XmlAttribute(name = "nextState", required = true)
			protected String nextState;
			@XmlAttribute(name = "probability", required = true)
			protected double probability;

			/**
			 * Obtém o valor da propriedade currentState.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getCurrentState() {
				return currentState;
			}

			/**
			 * Define o valor da propriedade currentState.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setCurrentState(final String value) {
				this.currentState = value;
			}

			/**
			 * Obtém o valor da propriedade executing.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getExecuting() {
				return executing;
			}

			/**
			 * Define o valor da propriedade executing.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setExecuting(final String value) {
				this.executing = value;
			}

			/**
			 * Obtém o valor da propriedade nextState.
			 * 
			 * @return possible object is {@link String }
			 * 
			 */
			public String getNextState() {
				return nextState;
			}

			/**
			 * Define o valor da propriedade nextState.
			 * 
			 * @param value
			 *            allowed object is {@link String }
			 * 
			 */
			public void setNextState(final String value) {
				this.nextState = value;
			}

			/**
			 * Obtém o valor da propriedade probability.
			 * 
			 */
			public double getProbability() {
				return probability;
			}

			/**
			 * Define o valor da propriedade probability.
			 * 
			 */
			public void setProbability(final double value) {
				this.probability = value;
			}

		}

	}

}
