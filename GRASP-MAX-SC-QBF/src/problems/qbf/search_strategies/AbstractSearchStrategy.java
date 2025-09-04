package problems.qbf.search_strategies;

import metaheuristics.grasp.AbstractGRASP;
import problems.Evaluator;
import solutions.Solution;

public abstract class AbstractSearchStrategy<E> {
    public abstract Solution<E> localSearch(
        Evaluator<E> ObjFunction,
        Solution<E> solution,
        AbstractGRASP<E> solver
    );
}
