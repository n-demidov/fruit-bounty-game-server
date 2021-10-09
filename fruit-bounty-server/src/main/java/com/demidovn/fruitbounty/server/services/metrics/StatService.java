package com.demidovn.fruitbounty.server.services.metrics;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.LongAdder;

@Component
public class StatService {

    private final ConcurrentMap<String, LongAdder> counterByName = new ConcurrentHashMap<>();

    public void incCounter(String name) {
        incCounter(name, 1);
    }

    public void incCounter(String name, long value) {
        LongAdder counter = counterByName.get(name);
        if (counter == null) {
            registerCounter(name);
            counter = counterByName.get(name);
        }
        counter.add(value);
    }

    public Map<String, Long> getAndClear() {
        Map<String, Long> countByName = new HashMap<>(counterByName.size());
        for (String key : counterByName.keySet()) {
            LongAdder adder = counterByName.get(key);
            long sum = adder.sum();
            countByName.put(key, sum);
            if (sum > 0) {
                adder.add(-1 * sum);
            }
        }
        return countByName;
    }

    private void registerCounter(String name) {
        counterByName.putIfAbsent(name, new LongAdder());
    }

    private void unRegisterCounter(String name) {
        counterByName.remove(name);
    }

}
