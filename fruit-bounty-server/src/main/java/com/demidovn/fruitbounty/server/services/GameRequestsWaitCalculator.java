package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.server.AppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameRequestsWaitCalculator {

    @Autowired
    private ConnectionService connectionService;

    public int getWaitUntilBot(int userScore) {
        int waitIterations = getWaitByOnlineUsers();

        int delta = userScore - AppConfigs.INITIAL_USER_SCORE;
        if (delta > 0 && waitIterations > 0) {
            waitIterations++;
        }

        if (userScore >= AppConfigs.MIN_RATING_WITH_RARE_BOT && waitIterations > 0) {
            waitIterations = AppConfigs.MAX_GAME_REQUEST_ITERATIONS_BEFORE_BOT_PLAY;
        }

        if (waitIterations > AppConfigs.MAX_GAME_REQUEST_ITERATIONS_BEFORE_BOT_PLAY) {
            waitIterations = AppConfigs.MAX_GAME_REQUEST_ITERATIONS_BEFORE_BOT_PLAY;
        }

        return waitIterations;
    }

    private int getWaitByOnlineUsers() {
        int onlineUsers = connectionService.countOnlineUsers();

        if (onlineUsers <= 1) {
            return 0;
        } else {
            return onlineUsers / 6 + 1;
        }
    }

}
