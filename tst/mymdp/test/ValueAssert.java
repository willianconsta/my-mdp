package mymdp.test;

import static java.lang.Math.abs;
import mymdp.core.State;
import mymdp.core.UtilityFunction;

import org.fest.assertions.Delta;
import org.fest.assertions.GenericAssert;

public class ValueAssert extends GenericAssert<ValueAssert, UtilityFunction> {

	ValueAssert(final UtilityFunction actual) {
		super(ValueAssert.class, actual);
	}

	public ValueAssert stateHasValue(final State state, final double expectedValue, final Delta delta) {
		return stateHasValue(state.name(), expectedValue, delta);
	}

	public ValueAssert stateHasValue(final String stateName, final double expectedValue, final Delta delta) {
		final double actualValue = actual.getUtility(stateName);
		if (equals(expectedValue, actualValue, delta)) {
			return this;
		}
		failIfCustomMessageIsSet();
		throw failure("Utility Function has not the expected value for the state. State = " + stateName +
				", Expected Value =" + expectedValue + ", Actual Value = " + actualValue);
	}

	private boolean equals(final double e, final double a, final Delta delta) {
		if (Double.compare(e, a) == 0) {
			return true;
		}
		return abs(e - a) <= delta.doubleValue();
	}
}
