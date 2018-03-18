package com.demidovn.fruitbounty.gameapi.services;

import com.demidovn.fruitbounty.gameapi.model.Game;

public interface GameEventsSubscriber {

  void gameChangedNotification(Game game);

}
