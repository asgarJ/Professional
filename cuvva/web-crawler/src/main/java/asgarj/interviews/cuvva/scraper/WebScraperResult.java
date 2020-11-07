package asgarj.interviews.cuvva.scraper;

import java.util.Collection;

public class WebScraperResult {

    private final Collection<String> internalLinks;

    private final Collection<String> staticContent;

    public WebScraperResult(Collection<String> internalLinks, Collection<String> staticContent) {
        this.internalLinks = internalLinks;
        this.staticContent = staticContent;
    }

    public Collection<String> getInternalLinks() {
        return internalLinks;
    }

    public Collection<String> getStaticContents() {
        return staticContent;
    }
}
