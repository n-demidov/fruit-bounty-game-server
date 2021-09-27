package com.demidovn.fruitbounty.server.persistence.entities.metrics;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class HistoryMetrics implements Serializable {

    private Map<String, DateStat> statsByDate = new HashMap<>();
    private long version = 1;

}
