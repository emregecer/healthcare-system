package nl.gerimedica.assignment.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class UsageTracker {

    private final AtomicInteger usageCounter = new AtomicInteger(0);

    public void record(String context) {
        int count = usageCounter.incrementAndGet();
        log.info("UsageTrackerService used. Counter: {} | Context: {}", count, context);
    }
}
