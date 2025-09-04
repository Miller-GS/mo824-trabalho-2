import problems.qbf.search_strategies.FirstImprovingSearchStrategy;
import problems.qbf.search_strategies.BestImprovingSearchStrategy;
import problems.qbf.search_strategies.AbstractSearchStrategy;
import problems.qbf.solvers.GRASP_QBF_SC;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        // Setup logger to write to file
        setupLogger();
        
        String[] instances = listInstances();
        InstanceParameters[] parameters = listParameters();

        logger.info("Starting GRASP QBF-SC solver execution");
        logger.info("Number of instances: " + instances.length);
        logger.info("Number of parameter configurations: " + parameters.length);

        for (String instance : instances) {
            for (InstanceParameters param : parameters) {
                try {
                    GRASP_QBF_SC solver = param.createSolver(instance, logger);
                    long startTime = System.currentTimeMillis();

                    String paramInfo = String.format("alpha=%.2f, iterations=%d, timeoutInSeconds=%d, maxIterationsWithoutImprovement=%d",
                            param.alpha, param.iterations, param.timeoutInSeconds, param.maxIterationsWithoutImprovement);
                    logger.info("Solving instance " + instance + " with parameters: " + paramInfo);
                    logger.info("Search Strategy: " + param.searchStrategy.getClass().getSimpleName());
                    
                    solver.solve();
                    long endTime = System.currentTimeMillis();
                    long executionTime = endTime - startTime;
                    
                    logger.info("Solution found in " + executionTime + " ms");
                    logger.info("Instance " + instance + " completed successfully\n");
                    
                } catch (Exception e) {
                    logger.severe("Error solving instance " + instance + " with parameters: " + e.getMessage());
                    logger.severe("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()));
                }
            }
        }
        
        logger.info("GRASP QBF-SC solver execution completed");
    }
    
    private static void setupLogger() {
        try {
            // Create timestamp for unique log file
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String logFileName = "results/grasp_qbf_sc_" + timestamp + ".log";
            
            // Create results directory if it doesn't exist
            java.io.File resultsDir = new java.io.File("results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }
            
            // Create file handler
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            
            // Configure logger
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            
            // Also log to console for immediate feedback
            logger.setUseParentHandlers(true);
            
            logger.info("Logger initialized. Output will be written to: " + logFileName);
            
        } catch (IOException e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    protected static String[] listInstances() {
        int nInstances = 15;
        String[] instances = new String[nInstances];
        for (int i = 0; i < nInstances; i++) {
            instances[i] = "GRASP-MAX-SC-QBF/instances/qbf-sc/instance_" + i + ".txt";
        }
        return instances;
    }

    protected static InstanceParameters[] listParameters() {
        Integer iterations = Integer.MAX_VALUE; // Run until timeout
        Long timeoutInSeconds = 60L * 30L; // 30 minutes
        Integer maxIterationsWithoutImprovement = 100000; // Stop if no improvement in 100,000 iterations
        Double alpha1 = 0.05;
        Double alpha2 = 0.5;

        return new InstanceParameters[] {
            // PADRÃO: alpha = 0.05, FirstImproving, heurística construtiva padrão
            new InstanceParameters(alpha1, iterations, new FirstImprovingSearchStrategy<Integer>(), timeoutInSeconds, maxIterationsWithoutImprovement),
            // PADRÃO + ALPHA: PADRÃO, mas com alpha = 0.5
            new InstanceParameters(alpha2, iterations, new FirstImprovingSearchStrategy<Integer>(), timeoutInSeconds, maxIterationsWithoutImprovement),
            //PADRÃO + BEST: PADRÃO, mas com BestImproving
            new InstanceParameters(alpha1, iterations, new BestImprovingSearchStrategy<Integer>(), timeoutInSeconds, maxIterationsWithoutImprovement),
            // PADRÃO + HC1: PADRÃO, mas com heurística construtiva alternativa 1
            // @TODO: implementar
            // PADRÃO + HC2: PADRÃO, mas com heurística construtiva alternativa 2
            // @TODO: implementar
        };
    }
}

class InstanceParameters {
    protected Double alpha;
    protected Integer iterations;
    protected AbstractSearchStrategy<Integer> searchStrategy;
    protected Long timeoutInSeconds;
    protected Integer maxIterationsWithoutImprovement;

    public InstanceParameters(Double alpha, Integer iterations, AbstractSearchStrategy<Integer> searchStrategy, Long timeoutInSeconds, Integer maxIterationsWithoutImprovement) {
        this.alpha = alpha;
        this.iterations = iterations;
        this.searchStrategy = searchStrategy;
        this.timeoutInSeconds = timeoutInSeconds;
        this.maxIterationsWithoutImprovement = maxIterationsWithoutImprovement;
    }

    public GRASP_QBF_SC createSolver(String filename, Logger logger) throws Exception {
        GRASP_QBF_SC solver = new GRASP_QBF_SC(alpha, iterations, filename, timeoutInSeconds, maxIterationsWithoutImprovement);
        solver.setSearchStrategy(searchStrategy);
        solver.setLogger(logger);
        return solver;
    }
}