package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;

import problems.qbf.QBF_Inverse;
import problems.qbf.QBF_SC_Inverse;
import solutions.Solution;

public class GRASP_QBF_SC extends GRASP_QBF {
    	/**
	 * Constructor for the GRASP_QBF_SC class. It is a solver for the max-QBF problem, but having set-cover restrictions.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public GRASP_QBF_SC(Double alpha, Integer iterations, String filename) throws IOException {
		super(alpha, iterations, new QBF_SC_Inverse(filename));
	}

    /*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {
        QBF_SC_Inverse objFunc = (QBF_SC_Inverse) ObjFunction;
        CL = objFunc.getVariablesThatCanBeSetToZero();
	}

    	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		GRASP_QBF_SC grasp = new GRASP_QBF_SC(0.05, 1000, "GRASP-MAX-SC-QBF/instances/qbf-sc/instance_1.txt");
		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}
}
