package com.demidovn.fruitbounty.server.persistence.entities.metrics.players;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DateStat {

    private int totalMinutes;
    private Map<Integer, DateStatMinutes> minutesByPlayers = new HashMap<>();

}
