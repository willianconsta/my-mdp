package mymdp.test;

import static org.assertj.core.api.Assertions.offset;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.data.Offset;

import com.google.common.math.DoubleMath;

import mymdp.core.State;
import mymdp.core.UtilityFunction;

public class ValueAssert
	extends
		AbstractAssert<ValueAssert,UtilityFunction>
{

	ValueAssert(final UtilityFunction actual) {
		super(actual, ValueAssert.class);
	}

	public ValueAssert stateHasValue(final State state, final double expectedValue, final double delta) {
		return stateHasValue(state.name(), expectedValue, offset(delta));
	}

	public ValueAssert stateHasValue(final String stateName, final double expectedValue, final Offset<Double> delta) {
		final double actualValue = actual.getUtility(stateName);
		if ( !DoubleMath.fuzzyEquals(expectedValue, actualValue, delta.value) ) {
			failWithMessage(
					"Utility Function has not the expected value for the state. State = %s, Expected Value = %s, Actual Value = %s",
					stateName, expectedValue, actualValue);
		}
		return this;
	}
}
