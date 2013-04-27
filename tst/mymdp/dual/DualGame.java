package mymdp.dual;

import mymdp.core.MDP;
import mymdp.core.MDPIP;
import mymdp.core.Policy;
import mymdp.core.UtilityFunction;
import mymdp.core.UtilityFunctionImpl;
import mymdp.core.UtilityFunctionWithProbImpl;
import mymdp.problem.MDPFileProblemReaderImpl;
import mymdp.problem.MDPIPFileProblemReaderImpl;
import mymdp.solver.ModifiedPolicyEvaluator;
import mymdp.solver.PolicyIterationImpl;
import mymdp.util.UtilityFunctionDistanceEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class DualGame {
    private static final Logger log = LogManager.getLogger(DualGame.class);

    @Test
    public void test() {
	final MDPFileProblemReaderImpl reader = new MDPFileProblemReaderImpl();
	MDP mdp = reader.readFromFile("problems\\navigation01.net");
	final ModifiedPolicyEvaluator evaluator = new ModifiedPolicyEvaluator(5);

	while (true) {
	    log.info("Starting MDP");
	    final Policy result = new PolicyIterationImpl(evaluator).solve(mdp);
	    log.info("End of MDP");
	    log.debug(result);
	    final UtilityFunction result1 = evaluator.policyEvaluation(result, new UtilityFunctionImpl(mdp.getStates()), mdp);
	    log.debug(result1);

	    final ImpreciseProblemGenerator generator = new ImpreciseProblemGenerator(result, mdp);
	    generator.writeToFile("problems\\imprecise_problem.txt", mdp.getStates().iterator().next(), mdp.getStates());
	    final MDPIPFileProblemReaderImpl reader2 = new MDPIPFileProblemReaderImpl(0.01);
	    final MDPIP mdpip = reader2.readFromFile("problems\\imprecise_problem.txt");
	    log.info("Starting MDPIP");
	    final UtilityFunctionWithProbImpl result2 = (UtilityFunctionWithProbImpl) new ValueIterationProbImpl(result1).solve(mdpip, 0.1);
	    log.info("End of MDPIP");
	    log.debug(result2);

	    final double actualError = UtilityFunctionDistanceEvaluator.distanceBetween(result1, result2);
	    log.info("Error between two MDPs = " + actualError);
	    if (actualError < 0.001) {
		break;
	    }

	    final PreciseProblemGenerator generator2 = new PreciseProblemGenerator(result2, mdpip);
	    generator2.writeToFile("problems\\precise_problem.txt", mdpip.getStates().iterator().next(), mdpip.getStates());

	    mdp = reader.readFromFile("problems\\precise_problem.txt");
	}
    }
}
