package com.demidovn.fruitbounty.server.services.metrics;

import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.game.UserGames;
import java.lang.management.ManagementFactory;
import java.util.concurrent.BlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerMetricsLogger {

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private UserGames userGames;

  @Autowired
  private UserService userService;

  private BlockingQueue<Runnable> authPoolQueue;
  private BlockingQueue<Runnable> gameNotifierPoolQueue;

  public void setAuthPoolQueue(BlockingQueue<Runnable> authPoolQueue) {
    this.authPoolQueue = authPoolQueue;
  }

  public void setGameNotifierPoolQueue(BlockingQueue<Runnable> gameNotifierPoolQueue) {
    this.gameNotifierPoolQueue = gameNotifierPoolQueue;
  }

  public void logMetrics() {
    log.info("metrics: threads={}, notAuthCons={}, authCons={}, playingUsers={}, authPoolQueue.size={}, gameNotifierPoolQueue.size={}, users={}",
      ManagementFactory.getThreadMXBean().getThreadCount(),
      connectionService.countNotAuthedConnections(),
      connectionService.countAuthedConnections(),
      userGames.countPlayingUsers(),
      authPoolQueue.size(),
      gameNotifierPoolQueue.size(),
      userService.getCount()
    );
  }

}
