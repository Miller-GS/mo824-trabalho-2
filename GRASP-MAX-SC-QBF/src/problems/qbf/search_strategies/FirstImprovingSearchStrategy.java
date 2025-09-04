package problems.qbf.search_strategies;

import java.util.ArrayList;

import metaheuristics.grasp.AbstractGRASP;
import problems.Evaluator;
import solutions.Solution;

public class FirstImprovingSearchStrategy<E> extends AbstractSearchStrategy<E> {
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
        Double deltaCost;
        boolean improvementFound;
        E bestCandIn = null, bestCandOut = null;

        do {
            improvementFound = false;
            solver.updateCL();
            ArrayList<E> cl = solver.getCL();
                
            // Evaluate insertions
            for (E candIn : cl) {
                deltaCost = ObjFunction.evaluateInsertionCost(candIn, solution);
                if (deltaCost < -Double.MIN_VALUE) {
                    bestCandIn = candIn;
                    bestCandOut = null;
                    improvementFound = true;
                    break;
                }
            }
            // Evaluate removals
            if (!improvementFound) {
                for (E candOut : solution) {
                    deltaCost = ObjFunction.evaluateRemovalCost(candOut, solution);
                    if (deltaCost < -Double.MIN_VALUE) {
                        bestCandIn = null;
                        bestCandOut = candOut;
                        improvementFound = true;
                        break;
                    }
                }
            }
            // Evaluate exchanges
            if (!improvementFound) {
                for (E candIn : cl) {
                    for (E candOut : solution) {
                        deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, solution);
                        if (deltaCost < -Double.MIN_VALUE) {
                            bestCandIn = candIn;
                            bestCandOut = candOut;
                            improvementFound = true;
                            break;
                        }
                    }
                    if (improvementFound) {
                        break;
                    }
                }
            }
            // Implement the first move that reduces the solution cost.
            if (improvementFound) {
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
        } while (improvementFound);
        return solution;
    }
}
