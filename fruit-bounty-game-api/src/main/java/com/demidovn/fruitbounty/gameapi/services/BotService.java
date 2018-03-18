package com.demidovn.fruitbounty.gameapi.services;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;

public interface BotService {

  void setMinBotRating(int minBotScore);

  void setMaxBotRating(int maxBotScore);

  Player createNewBot();

  boolean isPlayerBot(Player player);

  void actionIfBot(Game game);

}
