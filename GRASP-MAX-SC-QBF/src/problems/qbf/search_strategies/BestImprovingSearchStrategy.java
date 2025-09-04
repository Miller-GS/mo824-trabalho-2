package problems.qbf.search_strategies;

import java.util.ArrayList;

import metaheuristics.grasp.AbstractGRASP;
import problems.Evaluator;
import solutions.Solution;

public class BestImprovingSearchStrategy<E> extends AbstractSearchStrategy<E> {
    /*
     * (non-Javadoc)
     *
     * @see problems.qbf.search_strategies.AbstractSearchStrategy#localSearch(problems.Evaluator,
     * solutions.Solution)
     */
    @Override
    public Solution<E> localSearch(
        Evaluator<E> ObjFunction,
        Solution<E> solution,
        AbstractGRASP<E> solver
    ) {
		Double minDeltaCost;
		E bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			solver.updateCL();
            ArrayList<E> cl = solver.getCL();
				
			// Evaluate insertions
			for (E candIn : cl) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, solution);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}
			// Evaluate removals
			for (E candOut : solution) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, solution);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			for (E candIn : cl) {
				for (E candOut : solution) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, solution);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}
			// Implement the best move, if it reduces the solution cost.
			if (minDeltaCost < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					solution.remove(bestCandOut);
					cl.add(bestCandOut);
				}
				if (bestCandIn != null) {
					solution.add(bestCandIn);
					cl.remove(bestCandIn);
				}
				ObjFunction.evaluate(solution);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);

		return solution;
    }
    
}
