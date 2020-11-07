package asgarj.interviews.cuvva.scraper;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Objects.nonNull;

public class WebScraper {

    private static final WebScraperResult EMPTY_RESULT = new WebScraperResult(EMPTY_LIST, EMPTY_LIST);
    private static final String CUVVA_COM = "cuvva.com";

    public WebScraperResult processUrl(String url) {
        System.out.println("Processing the url: " + url);
        try {
            Response response = Jsoup.connect(url).execute();
            if (response.contentType().contains("html")) {
                var doc = response.parse();

                var scriptUrls = doc.select("script").eachAttr("abs:src");
                var styleUrls = doc.select("link").eachAttr("abs:href");
                var imageSources = doc.select("img").eachAttr("abs:src");
                var reachableLinks = doc.select("a").eachAttr("abs:href");

                var reachableInternalLinks = reachableLinks.stream()
                        .filter(link -> !link.contains(" "))
                        .map(URI::create)
                        .filter(uri -> nonNull(uri.getScheme()) && nonNull(uri.getHost()) && nonNull(uri.getPath()))
                        .filter(uri -> uri.getScheme().startsWith("http"))
                        .filter(uri -> uri.getHost().contains(CUVVA_COM))
                        .map(WebScraper::stripQueryParams)
                        .filter(Objects::nonNull)
                        .map(URI::toString)
                        .collect(Collectors.toSet());

                var staticContents = Stream.of(scriptUrls, styleUrls, imageSources)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

                return new WebScraperResult(reachableInternalLinks, staticContents);
            } else {
                return EMPTY_RESULT;
            }
        } catch (IOException e) {
            System.err.println("Unexpected IO exception occurred" + e);
            return EMPTY_RESULT;
        }
    }

    private static URI stripQueryParams(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
