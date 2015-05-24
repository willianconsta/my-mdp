package mymdp.test;

import static com.google.common.base.Objects.equal;

import org.assertj.core.api.AbstractAssert;

import com.google.common.base.Optional;

import mymdp.core.Action;
import mymdp.core.Policy;

public final class PolicyAssert
	extends
		AbstractAssert<PolicyAssert,Policy>
{

	PolicyAssert(final Policy actual) {
		super(actual, PolicyAssert.class);
	}

	public PolicyAssert stateHasAction(final String stateName, final String actionName) {
		final String policyActionName = Optional.fromNullable(actual.getActionFor(stateName)).transform(Action::name)
				.orNull();
		if ( !equal(policyActionName, actionName) ) {
			failWithMessage(
					"Policy has not the expected action for the state. State = %s, Expected Action = %s, Actual Action = %s.",
					stateName, actionName, policyActionName);
		}
		return this;
	}
}
