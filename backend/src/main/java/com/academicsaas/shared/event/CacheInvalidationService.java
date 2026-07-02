package com.academicsaas.shared.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationService {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationService.class);
    private final CacheManager cacheManager;

    public CacheInvalidationService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictAll(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.debug("Evicted all entries from cache: {}", cacheName);
        }
    }

    public void evict(String cacheName, Object key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
            log.debug("Evicted cache entry: {}::{}", cacheName, key);
        }
    }
}
