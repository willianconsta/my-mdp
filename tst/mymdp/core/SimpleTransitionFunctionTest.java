package mymdp.core;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.testing.EqualsTester;
import com.google.common.testing.NullPointerTester;
import com.google.common.testing.NullPointerTester.Visibility;

import mymdp.exception.InvalidProbabilityFunctionException;

@RunWith(MockitoJUnitRunner.class)
public class SimpleTransitionFunctionTest
{
	@Test(expected = InvalidProbabilityFunctionException.class)
	public void createShouldFailWhenNoDistributionsGiven() {
		SimpleTransitionFunction.create(mock(State.class), mock(Action.class), emptyMap());
	}

	@Test(expected = InvalidProbabilityFunctionException.class)
	public void createShouldFailWhenSumLessThanOne() {
		SimpleTransitionFunction.create(mock(State.class), mock(Action.class), singletonMap(mock(State.class), 0.5));
	}

	@Test(expected = InvalidProbabilityFunctionException.class)
	public void createShouldFailWhenSumMoreThanOne() {
		SimpleTransitionFunction.create(mock(State.class), mock(Action.class), singletonMap(mock(State.class), 1.5));
	}

	@Test
	public void nullTests() {
		final NullPointerTester tester = new NullPointerTester();
		tester.testStaticMethods(SimpleTransitionFunction.class, Visibility.PACKAGE);
		tester.testAllPublicInstanceMethods(SimpleTransitionFunction.empty(mock(State.class), mock(Action.class)));
	}

	@Test
	public void equalsTests() {
		final State state = mock(State.class);
		final Action action = mock(Action.class);
		new EqualsTester()
				.addEqualityGroup(
						SimpleTransitionFunction.create(state, action, singletonMap(state, 1.0)),
						SimpleTransitionFunction.create(state, action, singletonMap(state, 1.0)))
				.addEqualityGroup(
						SimpleTransitionFunction.empty(state, action),
						SimpleTransitionFunction.empty(state, action))
				.testEquals();
	}

	@Test
	public void getProbabilityForShouldReturnValueWhenHasState() {
		final State state = mock(State.class);
		final TransitionProbability subject = SimpleTransitionFunction.create(state, mock(Action.class), singletonMap(state, 1.0));
		final double result = subject.getProbabilityFor(state);
		assertThat(result).isEqualTo(1.0);
	}

	@Test
	public void getProbabilityForShouldReturnZeroWhenNotHasState() {
		final TransitionProbability subject = SimpleTransitionFunction.create(mock(State.class), mock(Action.class),
				singletonMap(mock(State.class), 1.0));
		final double result = subject.getProbabilityFor(mock(State.class));
		assertThat(result).isEqualTo(0.0);
	}
}
