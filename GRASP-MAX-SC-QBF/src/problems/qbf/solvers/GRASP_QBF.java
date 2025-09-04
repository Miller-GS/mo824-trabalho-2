package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;

import metaheuristics.grasp.AbstractGRASP;
import problems.qbf.QBF;
import problems.qbf.QBF_Inverse;
import problems.qbf.search_strategies.AbstractSearchStrategy;
import problems.qbf.search_strategies.BestImprovingSearchStrategy;
import problems.qbf.search_strategies.FirstImprovingSearchStrategy;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class GRASP_QBF extends AbstractGRASP<Integer> {
    protected AbstractSearchStrategy<Integer> searchStrategy;

	/**
	 * Constructor for the GRASP_QBF class. An inverse QBF objective function is
	 * passed as argument for the superclass constructor.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
     * @param timeoutInSeconds Maximum time in seconds that the GRASP can run. If null, there is no time limit.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public GRASP_QBF(Double alpha, Integer iterations, String filename, Long timeoutInSeconds) throws IOException {
		super(new QBF_Inverse(filename), alpha, iterations, timeoutInSeconds);
        searchStrategy = new BestImprovingSearchStrategy<Integer>();
	}

    /**
	 * Alternate constructor for the GRASP_QBF class, that receives the evaluator as a parameter.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param evaluator
	 *            The QBF evaluator to be used.
     * @param timeoutInSeconds Maximum time in seconds that the GRASP can run. If null, there is no time limit.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public GRASP_QBF(Double alpha, Integer iterations, QBF evaluator, Long timeoutInSeconds) throws IOException {
		super(evaluator, alpha, iterations, timeoutInSeconds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	@Override
	public ArrayList<Integer> makeCL() {

		ArrayList<Integer> _CL = new ArrayList<Integer>();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			Integer cand = i;
			_CL.add(cand);
		}

		return _CL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
	 */
	@Override
	public ArrayList<Integer> makeRCL() {

		ArrayList<Integer> _RCL = new ArrayList<Integer>();

		return _RCL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {

		// do nothing since all elements off the solution are viable candidates.

	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution. In our context, this means that all variables
     * are set to 1. When an element is added to the solution, it will be set to 0.
     * This inversion makes it easier to implement the set-cover restrictions.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();
		sol.cost = ObjFunction.evaluate(sol);
		return sol;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The local search operator developed for the QBF objective function is
	 * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
	 */
	@Override
	public Solution<Integer> localSearch() {
        return searchStrategy.localSearch(ObjFunction, sol, this);
	}

    public AbstractSearchStrategy<Integer> getSearchStrategy() {
        return searchStrategy;
    }

    public void setSearchStrategy(AbstractSearchStrategy<Integer> searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {
        // Specific parameters for testing
        Double alpha = 0.05;
        Integer iterations = 1000;
        String filename = "GRASP-MAX-SC-QBF/instances/qbf/qbf020";
        AbstractSearchStrategy<Integer> searchStrategy = new BestImprovingSearchStrategy<Integer>();

		long startTime = System.currentTimeMillis();
		GRASP_QBF grasp = new GRASP_QBF(alpha, iterations, filename);
		grasp.setSearchStrategy(searchStrategy);
		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}

}
