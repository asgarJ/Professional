package asgarj;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * This class owns the functionality to ping <i>Magnificent</i> several times per some period (configurable).
 */
public class HealthChecker implements Runnable {
    private final int attemptsPerPeriod;
    private final long period;

    private final HttpClient client;
    private final HttpMethod getMethod;
    private final Logger logger;

    public HealthChecker(String host, int port, long period, int attemptsPerPeriod, Logger logger) {
        client = new HttpClient();
        getMethod = new GetMethod("http://" + host + ":" + port);
        this.period = period;
        this.attemptsPerPeriod = attemptsPerPeriod;
        this.logger = logger;
    }

    @Override
    public void run() {
        int count = 0;
        for (int i = 0; i < attemptsPerPeriod; ++i) {
            try {
                int statusCode = client.executeMethod(getMethod);
                String response = getMethod.getResponseBodyAsString();

                if (statusCode == HttpStatus.SC_OK && response.equals("Magnificent!"))
                    ++count;

                TimeUnit.MILLISECONDS.sleep(period / attemptsPerPeriod);
            } catch (ConnectException e) {
                count = -1;
                break;
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info(status(count));
    }

    /**
     * Interprets the health status of Magnificent by returning one of <i>Excellent, Good, Average, Bad, Too Bad and No signal<i/>.
     *
     * @param count number of times Magnificent received, out of 8 attempts.
     * @return
     */
    private String status(int count) {
        switch (count) {
            case 8:
                return "Excellent";
            case 7:
            case 6:
                return "Good";
            case 5:
            case 4:
                return "Average";
            case 3:
            case 2:
                return "Bad";
            case 1:
            case 0:
                return "Too Bad";
            case -1:
                return "No signal";
            default: throw new IllegalStateException("Unknown state. @param count must be between -1 and 8 inclusive.");
        }
    }
}
