package mymdp.test;

import mymdp.core.Policy;
import mymdp.core.UtilityFunction;

public class MDPAssertions {
	public static PolicyAssert assertThat(final Policy policy) {
		return new PolicyAssert(policy);
	}

	public static ValueAssert assertThat(final UtilityFunction utilityFunction) {
		return new ValueAssert(utilityFunction);
	}
}
