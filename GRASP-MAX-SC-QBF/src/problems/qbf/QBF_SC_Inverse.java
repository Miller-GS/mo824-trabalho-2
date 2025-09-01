package problems.qbf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class QBF_SC_Inverse extends QBF_Inverse {
    protected HashSet<Integer>[] sets;

	/**
	 * Constructor for the QBF_SC_Inverse class.
	 * 
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	public QBF_SC_Inverse(String filename) throws IOException {
		super(filename);
	}

    /**
	 * Responsible for setting the QBF function parameters by reading the
	 * necessary input from an external file. this method reads the domain's
	 * dimension, matrix {@link #A}, and sets for the set-cover.
	 * 
	 * @param filename
	 *            Name of the file containing the input for setting the black
	 *            box function.
	 * @return The dimension of the domain.
	 * @throws IOException
	 *             Necessary for I/O operations.
	 */
	protected Integer readInput(String filename) throws IOException {

		Reader fileInst = new BufferedReader(new FileReader(filename));
		StreamTokenizer stok = new StreamTokenizer(fileInst);

        // First line has the number of variables N
		stok.nextToken();
		Integer _size = (int) stok.nval;

        // There are also N sets that will be used for the set-cover restrictions
		sets = (HashSet<Integer>[]) new HashSet[_size];
        Integer[] setSizes = new Integer[_size];

        // The next line has the sizes of each set
        for (int i = 0; i < _size; i++) {
            sets[i] = new HashSet<Integer>();
            stok.nextToken();
            setSizes[i] = (int) stok.nval;
        }

        // And the next N lines each contain the elements of the sets
        for (int i = 0; i < _size; i++) {
            for (int j = 0; j < setSizes[i]; j++) {
                stok.nextToken();
                Integer elem = (int) stok.nval - 1; // Making it 0-index so we don't have to worry about it anywhere else
                sets[i].add(elem);
            }
        }

        // N x N is also the dimension of the matrix A
		A = new Double[_size][_size];
        // The next N lines are rows of the matrix A
        // We assume a superior triangular matrix
		for (int i = 0; i < _size; i++) {
			for (int j = i; j < _size; j++) {
				stok.nextToken();
				A[i][j] = stok.nval;
				if (j>i)
					A[j][i] = 0.0;
			}
		}

		return _size;
	}

    public ArrayList<Integer> getVariablesThatCanBeSetToZero() {
        // Sets go from 0 to N-1
        // This will list all sets that are still being used to cover the elements
        ArrayList<Integer> setIndexesEnabled = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            // When a variable is set to 1, it means the set covering it is still active
            if (variables[i] == 1.0) {
                setIndexesEnabled.add(i);
            }
        }

        // We count how many times each element (variable) is covered
        HashMap<Integer, Integer> elementCoverageCount = new HashMap<>();
        for (int setIndex : setIndexesEnabled) {
            for (Integer elem : sets[setIndex]) {
                elementCoverageCount.put(
                    elem, 
                    elementCoverageCount.getOrDefault(elem, 0) + 1
                );
            }
        }

        ArrayList<Integer> variablesThatCanBeSetToZero = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (variables[i] == 1.0) {
                boolean canBeSetToZero = true;
                for (Integer elem : sets[i]) {
                    // If we set variable i to 0, we disable a set
                    // If there are elements in the set that are only covered once, they will not be covered anymore
                    // So we can't set this variable to 0
                    if (elementCoverageCount.getOrDefault(elem, 0) == 1) {
                        canBeSetToZero = false;
                        break;
                    }
                }
                if (canBeSetToZero) {
                    variablesThatCanBeSetToZero.add(i);
                }
            }
        }
        
        return variablesThatCanBeSetToZero;
    }
}
