package org.TraceVerifier.Util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Path;

/**
 * Class to track the memory of the java VM , and the UPPAAL server.
 * See https://docs.kernel.org/filesystems/proc.html for additional information what VmRSS (current physical RAM) and VmHWM (peak physical RAM) is.
 * We want VmRSS as the VM is not killed, else we might reuse the same peak from a different model/query.
 * We might miss a peak due to the polling rate, but the VM memory is not that volatile from measurements.
 * We want VmHWM for the server, so we don't miss a peak.
 */
public class MemoryTracker {
    private static MemoryTracker singleton;

    private boolean setupCalled    = false;
    private String logFilePath;

    // PID of the log script we use.
    private long bashLoggerPID = -1;
    private long   vmPID;

    // Want to make sure we can track the memory.
    private boolean vmPIDReady  = false;
    private String[] extraColumns  = new String[0];

    // Singleton, otherwise have to pass around the class too much.
    private MemoryTracker() {}

    public static synchronized MemoryTracker getSingleton() {
        if (singleton == null) {
            singleton = new MemoryTracker();
        }
        return singleton;
    }

    /**
     * Setup the logging info before you start logging.
     */
    public synchronized void setup(String logFilePath, String... extraColumns) {
        this.logFilePath   = logFilePath;
        this.extraColumns  = extraColumns != null ? extraColumns : new String[0];
        this.setupCalled   = true;

        determinePIDs();
        writeHeader();
    }

    public synchronized void determinePIDs() {
        try {
            vmPID      = getVMPID();
            vmPIDReady = true;
        } catch (Exception e) {
            vmPIDReady = false;
            System.out.println("MemoryTracker: Could not resolve VM PID: " + e.getMessage());
        }
    }
    /**
     * Log that we're starting to track memory.
     */
    public synchronized void logStart(String comment) {
        checkLogReady();

        if (bashLoggerPID > 0) {
            System.out.println("MemoryTracker: logStart() called while already logging " +
                    "(bash logger PID " + bashLoggerPID + "). Call logStop() first.");
            return;
        }

        try {
            String pidStr = startLogger(comment);

            bashLoggerPID = Long.parseLong(pidStr.trim());
            System.out.println("MemoryTracker: Logging started. Started logger with PID " +
                    bashLoggerPID + ".");

        } catch (IOException | InterruptedException | NumberFormatException e) {
            bashLoggerPID = -1;
            throw new IllegalStateException(
                    "MemoryTracker: Failed to start logger: " + e.getMessage(), e);
        }
    }

    /**
     * Launches a mem_track.sh script instance to track memory.
     */
    private String startLogger(String comment) throws IOException, InterruptedException {
        Process p = getProcess(comment);
        String pidStr;
        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            pidStr = br.readLine();
        }
        p.waitFor();

        if (pidStr == null || pidStr.isBlank()) {
            throw new IOException("Failed to read PID of spawned bash logger.");
        }
        return pidStr;
    }

    private Process getProcess(String comment) throws IOException {
        // In case any comments contain bash chars that can alter script behavior, we just make the entire string "inactive". We parse it again in the script itself.
        String safeComment = (comment == null) ? "" : comment.trim()
                .replace("\\", "\\\\")
                .replace("'",  "'\\''")
                .replace("`",  "\\`")
                .replace("\"",  "\\\"")
                .replace("$",  "\\$")
                .replace(" ", "")
                ;
        URL scriptPath = MemoryTracker.class.getResource("/mem_track.sh");
        if(scriptPath == null)
        {
            throw new IOException("Can't find mem_track script.");
        }

        ProcessBuilder pb = new ProcessBuilder(
                "bash", "-c",
                "nohup bash " + scriptPath.getPath() + " " + vmPID + " " + logFilePath + " \"" + safeComment + "\" >/dev/null 2>&1 & echo $!");
        pb.redirectErrorStream(true);
        return pb.start();
    }

    /**
     * Log that we're done with tracking the memory.
     */
    public synchronized void logStop() {
        if (bashLoggerPID <= 0) {
            System.out.println("MemoryTracker: logStop called but not logging.");
            return;
        }
        try {
            new ProcessBuilder("kill", Long.toString(bashLoggerPID))
                    .start()
                    .waitFor();
            System.out.println("MemoryTracker: Logging stopped. Killed logger with PID " + bashLoggerPID);
        } catch (Exception e) {
            System.out.println("MemoryTracker: Failed to kill bash logger PID " +
                    bashLoggerPID + ". Exception: " + e.getMessage());
        } finally {
            bashLoggerPID = -1;
        }
    }

    private long getVMPID() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(name.split("@")[0]);
    }

    private void writeHeader() {
        StringBuilder header = new StringBuilder("timestamp | vm pid | vm rss (KB) | server pid | server hwm (KB)");
        for (String col : extraColumns) {
            header.append(" | ").append(col);
        }
        appendLine(header.toString());
    }

    private void appendLine(String line) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("MemoryTracker: Failed writing to log file: " + e.getMessage());
        }
    }
    private void checkLogReady() {
        if (!setupCalled) {
            throw new IllegalStateException(
                    "MemoryTracker: Cannot log because setup() has not been called.");
        }
        if (!vmPIDReady) {
            throw new IllegalStateException(
                    "MemoryTracker: Cannot log because VM PID not ready or available.");
        }
    }
}