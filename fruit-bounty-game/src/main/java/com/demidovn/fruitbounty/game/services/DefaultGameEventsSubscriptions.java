package com.demidovn.fruitbounty.game.services;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.services.GameEventsSubscriber;
import com.demidovn.fruitbounty.gameapi.services.GameEventsSubscriptions;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class DefaultGameEventsSubscriptions implements GameEventsSubscriptions {

  private final List<GameEventsSubscriber> gameEventsSubscribers = new CopyOnWriteArrayList<>();

  @Override
  public void subscribe(GameEventsSubscriber gameEventsSubscriber) {
    gameEventsSubscribers.add(gameEventsSubscriber);
  }

  public void notifyGameChanged(Game game) {
    for (GameEventsSubscriber gameEventsSubscriber : gameEventsSubscribers) {
      gameEventsSubscriber.gameChangedNotification(game);
    }
  }

}
