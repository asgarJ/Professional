# Domain-bound web-crawler

This is an implementation of domain-bound web-crawler.
It means that the search doesn't go out of the configured base domain.

## Tech stack
* Java 11
* Gradle 6.5
* Jsoup


## Strategy and Implementation details

It uses a fairly modified Breadth-First-Search algorithm. The modifications are made to
accommodate the asynchronous calls. The problem by nature is IO-bound. All network calls are made asynchronously.

Below, there's an example of refactoring that I did on my own approach. As part of the BFS, to mark
urls visited and provide a synchronization around it, I had used ReadWriteLock.

```java
// BEFORE
class WebCrawler {
    public void crawler() throws InterruptedException {
        // rest of the code ...
        for (var reachableLink: scraperResult.getInternalLinks()) {
            this.readWriteLock.readLock().lock();
            if (!processedUrls.contains(reachableLink)) {
                this.readWriteLock.readLock().unlock();

                this.readWriteLock.writeLock().lock();
                if (!processedUrls.contains(reachableLink)) {
                    processedUrls.add(reachableLink);
                    queue.add(reachableLink);
                }
                this.readWriteLock.writeLock().unlock();
            } else {
                this.readWriteLock.readLock().unlock();
            }
        }
    }
}
```

It was later refactored into the below by using concurrent set for `processedUrls`:

```java
// AFTER
class WebCrawler {
    public void crawler() {
        // rest of the code ...
        scraperResult.getInternalLinks().stream().filter(processedUrls::add).forEach(queue::add);
    }
}
```
