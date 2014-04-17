package mymdp.core;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Solution of a MDP/MDPIP, contains the policy and the value function.
 * 
 * @author Willian
 */
public final class SolutionReport {
	private final Policy policyResult;
	private final UtilityFunction valueResult;

	public SolutionReport(final Policy policyResult, final UtilityFunction valueResult) {
		this.policyResult = checkNotNull(policyResult);
		this.valueResult = checkNotNull(valueResult);
	}

	/**
	 * Gets the value function of the solution of a MDP/MDPIP.
	 * 
	 * @return the value function
	 */
	public UtilityFunction getValueResult() {
		return valueResult;
	}

	/**
	 * Gets the policy which is solution of a MDP/MDPIP.
	 * 
	 * @return the policy
	 */
	public Policy getPolicyResult() {
		return policyResult;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(policyResult, valueResult);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SolutionReport)) {
			return false;
		}

		final SolutionReport other = (SolutionReport) obj;
		return equal(this.policyResult, other.policyResult)
				&& equal(this.valueResult, other.valueResult);
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("policyResult", policyResult)
				.add("valueResult", valueResult)
				.toString();
	}
}
