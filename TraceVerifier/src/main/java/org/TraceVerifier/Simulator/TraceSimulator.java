package org.TraceVerifier.Simulator;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.connection.Connection;
import com.uppaal.engine.connection.LocalConnection;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.EngineSettings;
import com.uppaal.model.core2.QueryResult;
import com.uppaal.model.io2.Problem;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import org.TraceVerifier.Query.TraceQuery;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Util.MemoryTracker;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;


/**
 * Simulator that communicates using the UPPAAL API
 */
public class TraceSimulator {

    // Class used to get simulation feedback.
    private final QueryFeedback qf = new QueryFeedback() {

        @Override
        public void setProgressAvail(boolean availability) {
            System.out.println("Starting trace analysis");
        }

        @Override
        public void setProgress(int load, long vm, long rss, long cached, long avail, long swap, long swapfree,
                                long user, long sys, long timestamp) {
        }

        @Override
        public void setSystemInfo(long vmsize, long physsize, long swapsize) {
        }

        @Override
        public void setLength(int length) {
            System.out.println("Length: " + length);
        }

        @Override
        public void setCurrent(int pos) {
            System.out.println("Pos: " + pos);
        }

        @Override
        public void setTrace(char c, String s, SymbolicTrace symbolicTrace, QueryResult queryResult) {
            System.out.println("Symbolic trace set.");
        }

        @Override
        public void setTrace(char c, String s, ConcreteTrace concreteTrace, QueryResult queryResult) {
            System.out.println("Concrete trace set.");
        }

        @Override
        public void setFeedback(String feedback) {
            if (feedback != null) {
                System.out.println("Feedback: " + feedback);
            } else {
                System.out.println("Failure.");
            }
        }

        @Override
        public void appendText(String s) {
            if (s != null && !s.isEmpty()) {
                System.out.println("Append: " + s);
            }
        }

        @Override
        public void setResultText(String s) {
            if (s != null && !s.isEmpty()) {
                System.out.println("Result: " + s);
            }
        }
    };
    // Model is the XML file used to simulate/store the model.
    private Document model;
    private TraceQueryCollection properties;
    private Engine engine;
    private final List<QueryResult> queryResults = new ArrayList<>();
    private final List<Problem> queryProblems = new ArrayList<>();

    public TraceSimulator() {

    }

    /**
     * Halt the simulation and disconnect.
     */
    public void stop() throws ExecutionException, InterruptedException {
        engine.cancel();
        engine.forceDisconnect().get();
    }


    public void setModel(Document model) {
        this.model = model;
    }

    public void setProperties(TraceQueryCollection properties) {
        this.properties = properties;
    }

    /**
     * Setup and connect to the engine.
     */
    public void setupEngine(String serverPath) throws IOException {
        LocalConnection connection = new LocalConnection("LOCAL", new File(serverPath));
        List<Connection> conns = new ArrayList<>();
        conns.add(connection);

        engine = new Engine();
        engine.resetConnections(conns);
        engine.setConnectionMode("LOCAL");
    }

    private void setEngineSettings(EngineSettings engineSettings) {
        // This causes major memory issues.
        // Which is also the main reason to use tracking queries and not do API diagnostic trace retrieval.
//        engineSettings.setValue("--diagnostic", "0");
    }

    /**
     * Start simulating, and log the results using the queryLogger.
     * If logMem is set, the memory tracker is also activated.
     */
    public void simulate(boolean logMem, String logString, Logger queryLogger) throws ExecutionException, InterruptedException, EngineException, TimeoutException {
        engine.connect().get();
        queryResults.clear();
        queryProblems.clear();

        // Prepare UPPAAL engine
        EngineSettings engineSettings = engine.getOptions().get().getDefaultSettings();
        setEngineSettings(engineSettings);
        engine.setOptionSettings(engineSettings).get();

        ArrayList<Problem> problems = new ArrayList<>();
        if(logMem)
        {
            System.out.println("MEMORY LOG SIM START");
            MemoryTracker.getSingleton().determinePIDs();
            MemoryTracker.getSingleton().logStart(logString + "| STARTUP_SERVER");
        }

        // Get a simulatable UPPAAL system for our model
        UppaalSystem sys = engine.getSystem(model, problems).get();
        if(logMem)
        {
            MemoryTracker.getSingleton().logStop();
            System.out.println("MEMORY LOG SIM STOP");
        }

        // Simulate each query and log the results
        for (TraceQuery traceQuery : properties.getAllQueries()) {
            if(logMem)
            {
                MemoryTracker.getSingleton().logStart(logString + "| " + traceQuery.getIdentifier());
            }

            String queryString = MessageFormat.format("Query: {0}", traceQuery.getIdentifier());
            System.out.println(queryString);
            queryLogger.info(queryString);

            String propString = MessageFormat.format("Property: {0}", traceQuery.getProperty().queryString());
            System.out.println(propString);
            queryLogger.info(propString);

            QueryResult queryResult = new com.uppaal.model.core2.QueryResult();
            try{
                queryResult = engine.query(sys, traceQuery.generateUPPAALQuery(), qf).get();
                if(!traceQuery.isExpectedToPass())
                {
                    // We failed, but that's what we wanted.
                    System.out.println("Passed query\n");
                    queryLogger.severe("Passed query");
                }
                else if (!queryResult.getStatus().name().equals("Success")) {
                    System.out.println("Failed query\n");
                    queryLogger.severe("Failed query");
                } else {
                    System.out.println("Passed query\n");
                    queryLogger.info("Passed query");
                }
            }
            // Crashes or memory issues cause the uppaal engine to throw an exception.
            catch(Exception e)
            {
                // If we crash, e.g., due to wanting a compilation error, this is the expected result.
                if(traceQuery.isExpectedToCrash())
                {
                    System.out.println("Passed query\n");
                    queryLogger.info("Passed query");
                }
                else
                {
                   throw e;
                }
            }

            traceQuery.parseUPPAALQueryResult(queryResult);
            queryResults.add(queryResult);
            if(logMem)
            {
                MemoryTracker.getSingleton().logStop();
            }
        }
        stop();
        System.out.println("Simulation finished");
    }
}
