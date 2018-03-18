package com.demidovn.fruitbounty.server.schedule;

import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.GameRequests;
import com.demidovn.fruitbounty.server.services.PlayersRating;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.metrics.ServerMetricsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private GameRequests gameRequests;

  @Autowired
  private ServerMetricsLogger serverMetricsLogger;

  @Autowired
  private PlayersRating playersRating;

  @Autowired
  private UserService userService;

  @Scheduled(fixedDelayString = "${game-server.schedule-delay.valid-not-authed-connections}")
  public void validNotAuthedConnections() {
    connectionService.validNotAuthedConnections();
  }

  @Scheduled(fixedDelayString  = "${game-server.schedule-delay.valid-authed-connections}")
  public void validAuthedConnections() {
    connectionService.validAuthedConnections();
  }

  @Scheduled(fixedDelayString  = "${game-server.schedule-delay.process-game-requests}")
  public void processGameRequests() {
    gameRequests.processGameRequests();
  }

  @Scheduled(fixedDelayString  = "${game-server.schedule-delay.rating-table-notification}")
  public void notifyAllWithTopRated() {
    playersRating.notifyAllWithTopRated();
  }

  @Scheduled(fixedDelayString = "${game-server.schedule-delay.metrics}")
  public void logMetrics() {
    serverMetricsLogger.logMetrics();
  }

  @Scheduled(fixedDelayString = "${game-server.schedule-delay.clear-not-actual-users}")
  public void clearNotActual() {
    userService.clearNotActual();
  }

}
