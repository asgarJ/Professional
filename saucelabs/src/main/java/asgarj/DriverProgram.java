package asgarj;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Starting point of the service.
 *
 * Reads all configurations and initialises <code>HealthChecker</code> by injecting all necessary information and
 * schedules periodic calls to <code>HealthChecker</code>.
 */
public class DriverProgram {
    public static void main(String...args) throws IOException {
        LogManager logManager = LogManager.getLogManager();
        logManager.readConfiguration(DriverProgram.class.getResourceAsStream("/logging.properties"));
        Logger logger = Logger.getLogger("HealthStatusService");

        Properties props = new Properties();
        try(InputStream inputStream = DriverProgram.class.getResourceAsStream("/config.properties")) {
            props.load(inputStream);
        }

        final String host = props.getProperty("host");
        final int port = Integer.valueOf(props.getProperty("port"));
        final long period = Long.valueOf(props.getProperty("periodInMillis"));
        final int attempts = Integer.valueOf(props.getProperty("attemptsPerPeriod"));

        Runnable monitor = new HealthChecker(host, port, period, attempts, logger);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(monitor, 0L, period, TimeUnit.MILLISECONDS);
    }
}
