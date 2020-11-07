package asgarj.interviews.cuvva;

import asgarj.interviews.cuvva.crawler.WebCrawler;
import asgarj.interviews.cuvva.scraper.WebScraper;

import java.io.IOException;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws InterruptedException, IOException {
        var props = new Properties();
        props.load(App.class.getClassLoader().getResourceAsStream("application.properties"));

        var initUrl = props.getProperty("initial_url", "http://www.cuvva.com");
        var timeout = Long.parseLong(props.getProperty("timeout", "5000L"));
        var domainBound = props.getProperty("domain_bound", "cuvva.com");

        new WebCrawler(initUrl, timeout, new WebScraper(domainBound)).crawler();
    }
}
