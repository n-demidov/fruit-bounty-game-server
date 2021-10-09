package com.demidovn.fruitbounty.server.dto.operations;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MetricsDto {

    private int threads;
    private int notAuthCons;
    private int authedConns;
    private int playingUsers;
    private int authPoolQueueSize;
    private int gameNotifierPoolQueueSize;
    private long users;

    public String getString() {
        return "" +
                "threads=" + threads +
                ", notAuthCons=" + notAuthCons +
                ", authedConns=" + authedConns +
                ", playingUsers=" + playingUsers +
                ", authPoolQueueSize=" + authPoolQueueSize +
                ", gameNotifierPoolQueueSize=" + gameNotifierPoolQueueSize +
                ", users=" + users +
                "";
    }
}
