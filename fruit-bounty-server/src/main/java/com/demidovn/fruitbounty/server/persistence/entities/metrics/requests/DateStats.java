package com.demidovn.fruitbounty.server.persistence.entities.metrics.requests;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DateStats {

    private Map<String, Long> statsByKey = new HashMap<>();

}
