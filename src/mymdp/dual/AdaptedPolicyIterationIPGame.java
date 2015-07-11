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
import mymdp.solver.PolicyIterationIPImpl;
import mymdp.solver.ProbLinearSolver;

public class AdaptedPolicyIterationIPGame
	implements
		ProblemSolver<MDPIP,Void>
{
	private static final Logger log = LogManager.getLogger(AdaptedPolicyIterationIPGame.class);

	public AdaptedPolicyIterationIPGame() {
	}

	@Override
	public SolutionReport solve(final Problem<MDPIP,Void> problem) {
		// Reads the MDP's definition from file and turns it to an imprecise
		// problem
		log.info("Current Problem: {}", problem.getName());
		final ModifiedPolicyEvaluatorIP evaluator = new ModifiedPolicyEvaluatorIP(100);
		log.debug("Starting MDPIP");
		ProbLinearSolver.initializeCount();
		final Stopwatch watchMDPIP = Stopwatch.createStarted();
		final Stopwatch watch1 = Stopwatch.createStarted();
		final MDPIP mdpip = problem.getModel();
		final Policy result = new PolicyIterationIPImpl(evaluator).solve(mdpip);
		watchMDPIP.stop();
		log.debug("End of MDPIP: {}ms", watch1.elapsed(TimeUnit.MILLISECONDS));
		log.info("MDPIP: {}", result);

		log.info("Summary:");
		log.info("Number of solver calls in solving = {}", ProbLinearSolver.getNumberOfSolverCalls());
		log.info("Time in MDPIP = {}ms", watchMDPIP.elapsed(TimeUnit.MILLISECONDS));

		log.info("End of problem {}\n\n\n\n\n", problem.getName());
		return new SolutionReport(result, evaluator.policyEvaluation(result, new UtilityFunctionImpl(mdpip.getStates()), mdpip));
	}
}
