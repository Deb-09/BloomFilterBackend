package com.rubun.bloom_username_checker.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.rubun.bloom_username_checker.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j


public class BloomFilterService{

    private final UserRepository userRepository;
    private BloomFilter<String> bloomFilter;

    //Stats counters

    private final AtomicLong totalChecks = new AtomicLong(0);
    private final AtomicLong bloomFilterShortCircuits = new AtomicLong(0);
    private final AtomicLong databaseQueries = new AtomicLong(0);

    @PostConstruct
    public void initBloomFilter(){
        // Create filter — expects max 10,000 usernames, 1% false positive rate
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8),
        10_000,
        0.01);

        // Warm up — load every existing username from DB into the filter
        long count = userRepository.count();
        userRepository.findAll().forEach(user -> bloomFilter.put(user.getUsername()));

        log.info("Bloom Filter initialised with {} usernames" , count);

    }

    public boolean mightContain(String username){
        totalChecks.incrementAndGet();

        boolean might = bloomFilter.mightContain(username);

        if(!might){
            // Bloom filter is 100% certain — no DB call needed
            bloomFilterShortCircuits.incrementAndGet();
            log.debug("Bloom filter: '{}' is DEFINITELY free — skipping DB", username);
        } else {
            // Bloom filter says maybe — we must check DB
            databaseQueries.incrementAndGet();
            log.debug("Bloom filter: '{}' might exist — querying DB", username);
        }

        return might;
    }

    public void add(String username){
        bloomFilter.put(username);
        log.info("Added '{}' to bloom filter", username);
    }

    public BloomFilterStats getStats() {
        long total = totalChecks.get();
        long shortcuts = bloomFilterShortCircuits.get();
        long dbHits = databaseQueries.get();
        double savedPercent = total == 0 ? 0 :
                Math.round((shortcuts * 100.0 / total) * 10.0) / 10.0;

        return new BloomFilterStats(total, shortcuts, dbHits, savedPercent);
    }

    public record BloomFilterStats(
            long totalChecks,
            long bloomShortCircuits,
            long databaseQueries,
            double dbCallsSavedPercent
    ) {}

}