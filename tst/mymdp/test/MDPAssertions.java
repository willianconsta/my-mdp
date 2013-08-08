package mymdp.test;

import mymdp.core.Policy;

public class MDPAssertions {
    public static PolicyAssert assertThat(final Policy policy) {
	return new PolicyAssert(policy);
    }
}
