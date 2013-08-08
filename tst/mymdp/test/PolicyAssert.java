package mymdp.test;

import mymdp.core.Action;
import mymdp.core.Policy;

import org.fest.assertions.GenericAssert;
import org.fest.util.Objects;

import com.google.common.base.Optional;

public final class PolicyAssert extends GenericAssert<PolicyAssert, Policy> {

    PolicyAssert(final Policy actual) {
	super(PolicyAssert.class, actual);
    }

    public PolicyAssert stateHasAction(final String stateName, final String actionName) {
	final String policyActionName = Optional.fromNullable(actual.getActionFor(stateName)).transform(Action.toName).orNull();
	if (Objects.areEqual(policyActionName, actionName)) {
	    return this;
	}
	failIfCustomMessageIsSet();
	throw failure("Policy has not the expected action for the state. State = " + stateName + ", Expected Action =" + actionName
		+ ", Actual Action = " + policyActionName);
    }
}
