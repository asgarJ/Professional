package asgarj.interviews.cuvva.crawler;

import asgarj.interviews.cuvva.scraper.WebScraper;
import asgarj.interviews.cuvva.scraper.WebScraperResult;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WebCrawler {

    private final String initialUrl;

    private final long timeout;

    private final WebScraper webScraper;

    private final Executor threadPool;

    private final Set<String> processedUrls;

    private final ConcurrentHashMap<String, Collection<String>> webPageStaticContents;

    public WebCrawler(String initialUrl, long timeout, WebScraper webScraper) {
        this.initialUrl = initialUrl;
        this.timeout = timeout;
        this.webScraper = webScraper;
        this.threadPool = Executors.newCachedThreadPool();
        this.processedUrls = new ConcurrentSkipListSet<>();
        this.webPageStaticContents = new ConcurrentHashMap<>();
    }

    public void crawler() throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.add(initialUrl);
        processedUrls.add(initialUrl);
        while (true) {
            var currentUrl = queue.poll(timeout, TimeUnit.MILLISECONDS);
            if (currentUrl == null) {
                System.out.println("No url found within the configured period of time. Terminating the search..");
                break;
            }

            threadPool.execute(() -> {
                WebScraperResult scraperResult = webScraper.processUrl(currentUrl);
                webPageStaticContents.put(currentUrl, scraperResult.getStaticContents());
                System.out.printf("Processing '%s' with %d internal links and %d static contents\n\n",
                        currentUrl, scraperResult.getInternalLinks().size(), scraperResult.getStaticContents().size());

                scraperResult.getInternalLinks().stream().filter(processedUrls::add).forEach(queue::add);
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
            System.out.printf("In total, there are %d urls crawled with %d number of static contents in total.\n",
                    webPageStaticContents.size(), webPageStaticContents.values().stream().mapToInt(Collection::size).sum()))
        );
    }
}
