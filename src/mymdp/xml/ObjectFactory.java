//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.8-b130911.1802 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2015.07.06 às 01:02:42 AM BRT 
//


package mymdp.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mymdp.xml package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Mdp_QNAME = new QName("https://github.com/willianconsta/my-mdp", "mdp");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mymdp.xml
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Mdp }
     * 
     */
    public Mdp createMdp() {
        return new Mdp();
    }

    /**
     * Create an instance of {@link Mdp.Transitions }
     * 
     */
    public Mdp.Transitions createMdpTransitions() {
        return new Mdp.Transitions();
    }

    /**
     * Create an instance of {@link Reward }
     * 
     */
    public Reward createReward() {
        return new Reward();
    }

    /**
     * Create an instance of {@link Action }
     * 
     */
    public Action createAction() {
        return new Action();
    }

    /**
     * Create an instance of {@link State }
     * 
     */
    public State createState() {
        return new State();
    }

    /**
     * Create an instance of {@link Mdp.DiscountFactor }
     * 
     */
    public Mdp.DiscountFactor createMdpDiscountFactor() {
        return new Mdp.DiscountFactor();
    }

    /**
     * Create an instance of {@link Mdp.States }
     * 
     */
    public Mdp.States createMdpStates() {
        return new Mdp.States();
    }

    /**
     * Create an instance of {@link Mdp.Actions }
     * 
     */
    public Mdp.Actions createMdpActions() {
        return new Mdp.Actions();
    }

    /**
     * Create an instance of {@link Mdp.Rewards }
     * 
     */
    public Mdp.Rewards createMdpRewards() {
        return new Mdp.Rewards();
    }

    /**
     * Create an instance of {@link Mdp.Transitions.TransitionFromTo }
     * 
     */
    public Mdp.Transitions.TransitionFromTo createMdpTransitionsTransitionFromTo() {
        return new Mdp.Transitions.TransitionFromTo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mdp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://github.com/willianconsta/my-mdp", name = "mdp")
    public JAXBElement<Mdp> createMdp(Mdp value) {
        return new JAXBElement<>(_Mdp_QNAME, Mdp.class, null, value);
    }

}
