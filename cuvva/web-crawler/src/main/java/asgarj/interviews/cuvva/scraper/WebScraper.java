package asgarj.interviews.cuvva.scraper;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Objects.nonNull;

public class WebScraper {

    private static final WebScraperResult EMPTY_RESULT = new WebScraperResult(EMPTY_LIST, EMPTY_LIST);

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
                        .peek(System.err::println)
                        .filter(uri -> nonNull(uri.getScheme()) && nonNull(uri.getHost()) && nonNull(uri.getPath()))
                        .filter(uri -> uri.getScheme().startsWith("http"))
                        .filter(uri -> uri.getHost().contains("cuvva.com"))
                        .map(WebScraper::stripQueryParams)
                        .filter(Objects::nonNull)
                        .map(URI::toString)
                        .peek(uri -> System.out.println(uri + " after processing"))
                        .collect(Collectors.toSet());

                var staticContents = retrieveStaticContentsAsync(scriptUrls, styleUrls, imageSources);
                return new WebScraperResult(reachableInternalLinks, staticContents);
            } else {
                return EMPTY_RESULT;
            }
        } catch (IOException e) {
            System.out.println("an error occurred" + e);
            return EMPTY_RESULT;
        }
    }

    private List<byte[]> retrieveStaticContentsAsync(List<String> scriptUrls, List<String> styleUrls, List<String> imageSources) {
        return Stream.of(scriptUrls, styleUrls, imageSources)
                .flatMap(List::stream)
                .map(Unirest::get)
                .map(WebScraper::composeAsyncComputation)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private static CompletableFuture<byte[]> composeAsyncComputation(GetRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return request.asBinary().getBody().readAllBytes();
            } catch (IOException | UnirestException e) {
                throw new RuntimeException();
            }
        });
    }

    private static URI stripQueryParams(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
