package org.TraceVerifier.Pipeline;

import com.google.gson.annotations.Expose;

/**
 * Configuration used to setup all the standard names and symbols for generating the results from the simulation and verification.
 */
public class TracePipelineConfig {
    @Expose
    private final String defaultDirNameRun;
    @Expose
    private final String defaultDirNameTraceGroup;
    @Expose
    private final String defaultDirNameTrace;
    @Expose
    private final String defaultDirNameMergedModel;
    @Expose
    private final String defaultDirNameQuery;
    @Expose
    private final String defaultNameTraceGroup;
    @Expose
    private final String defaultNameTrace;
    @Expose
    private final String defaultNameMergedModel;
    @Expose
    private final String defaultNameModelConfig;
    @Expose
    private final String defaultNameModelVariables;
    @Expose
    private final String defaultNameQuery;
    @Expose
    private final String defaultNameSeparator;
    @Expose
    private int runAmount;
    @Expose
    private String basePath;
    @Expose
    private String serverPath;
    @Expose
    private boolean backupRunsBeforeSimulation;
    @Expose
    private String backupPath;
    @Expose
    private boolean deleteRunsBeforeSimulation;

    /**
     * Has default values which work in most circumstances. Ultimately requires a valid base path to save data to, and server path for the UPPAAL server.
     */
    public TracePipelineConfig(String savePath, String serverPath, int runAmount, boolean deleteRunsBeforeSimulation) {
        this.runAmount = runAmount;
        this.basePath = savePath;
        this.serverPath = serverPath;
        this.deleteRunsBeforeSimulation = deleteRunsBeforeSimulation;
        this.defaultDirNameTraceGroup = "trace-group";
        this.defaultNameTraceGroup = "trace-group";
        this.defaultDirNameRun = "run";
        this.defaultDirNameTrace = "trace";
        this.defaultNameModelConfig = "config";
        this.defaultNameModelVariables = "variables";
        this.defaultDirNameMergedModel = "merged-model";
        this.defaultDirNameQuery = "query";
        this.defaultNameSeparator = "_";
        this.defaultNameTrace = "trace";
        this.defaultNameMergedModel = "merged-model";
        this.defaultNameQuery = "query";
    }

    public String getDefaultNameTraceGroup() {
        return defaultNameTraceGroup;
    }

    public String getDefaultDirNameTraceGroup() {
        return defaultDirNameTraceGroup;
    }

    public String getDefaultDirNameRun() {
        return defaultDirNameRun;
    }

    public String getDefaultDirNameTrace() {
        return defaultDirNameTrace;
    }

    public String getDefaultDirNameMergedModel() {
        return defaultDirNameMergedModel;
    }

    public String getDefaultDirNameQuery() {
        return defaultDirNameQuery;
    }

    public String getDefaultNameSeparator() {
        return defaultNameSeparator;
    }

    public int getRunAmount() {
        return runAmount;
    }

    public void setRunAmount(int runAmount) {
        this.runAmount = runAmount;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getDefaultNameMergedModel() {
        return defaultNameMergedModel;
    }

    public String getDefaultNameQuery() {
        return defaultNameQuery;
    }

    public String getDefaultNameTrace() {
        return defaultNameTrace;
    }

    public String getDefaultNameModelConfig() {
        return defaultNameModelConfig;
    }

    public String getDefaultNameModelVariables() {
        return defaultNameModelVariables;
    }

    public boolean deleteRunsBeforeSimulation() {
        return deleteRunsBeforeSimulation;
    }

    public void setDeleteRunsBeforeSimulation(boolean deleteRunsBeforeSimulation) {
        this.deleteRunsBeforeSimulation = deleteRunsBeforeSimulation;
    }
}
