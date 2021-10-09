package com.demidovn.fruitbounty.server.persistence.entities.metrics.requests;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class HistoryStats implements Serializable {

    private Map<String, DateStats> statsByDate = new HashMap<>();
    private long version = 1;

}
