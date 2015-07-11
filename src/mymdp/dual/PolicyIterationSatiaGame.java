package mymdp.dual;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.SolutionReport;
import mymdp.core.UtilityFunctionImpl;
import mymdp.solver.ModifiedPolicyEvaluatorIP;
import mymdp.solver.PolicyIterationSatia;
import mymdp.solver.ProbLinearSolver;

public class PolicyIterationSatiaGame
	implements
		ProblemSolver<MDPIP,Void>
{
	private static final Logger log = LogManager.getLogger(PolicyIterationSatiaGame.class);

	private final double maxError;

	public PolicyIterationSatiaGame(final double maxError) {
		this.maxError = maxError;
	}

	@Override
	public SolutionReport solve(final Problem<MDPIP,Void> problem) {
		ProbLinearSolver.initializeCount();

		final MDPIP mdpip = problem.getModel();

		log.debug("Starting MDPIP");
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDPIP = Stopwatch.createStarted();
		final Stopwatch watch1 = Stopwatch.createStarted();
		final Policy result = new PolicyIterationSatia(maxError).solve(mdpip);
		watchMDPIP.stop();
		log.debug("End of MDPIP: {}", watch1.elapsed(TimeUnit.MILLISECONDS) + "ms");
		log.info("MDPIP: {}", result);

		log.info("Summary:");
		log.info("Number of solver calls in solving = {}", ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Time in MDPIP = {}ms.", watchMDPIP.elapsed(TimeUnit.MILLISECONDS));

		log.info("Solving done. Generating report...\n");
		try {
			final ModifiedPolicyEvaluatorIP evaluator = new ModifiedPolicyEvaluatorIP(10);
			return new SolutionReport(result,
					evaluator.policyEvaluation(result, new UtilityFunctionImpl(mdpip.getStates()), mdpip));
		} finally {
			log.info("End of problem {}\n\n\n\n\n");
		}
	}
}
