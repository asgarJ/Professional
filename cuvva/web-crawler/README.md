
```java
class WebCrawler {
    public void crawler() throws InterruptedException {
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