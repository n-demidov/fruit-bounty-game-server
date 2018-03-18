package com.demidovn.fruitbounty.server.services.game.subscriber;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.services.GameEventsSubscriber;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultGameEventsSubscriber implements GameEventsSubscriber {

  @Autowired
  private ExecutorService gameNotifierExecutorService;

  @Autowired
  private ApplicationContext ctx;

  @Override
  public void gameChangedNotification(Game game) {
    log.trace("gameChangedNotification game={}", game);

    GameChangeNotificationExecutor gameChangeNotificationExecutor =
      ctx.getBean(GameChangeNotificationExecutor.class);
    gameChangeNotificationExecutor.setGame(game);

    gameNotifierExecutorService.submit(gameChangeNotificationExecutor);
  }

}
