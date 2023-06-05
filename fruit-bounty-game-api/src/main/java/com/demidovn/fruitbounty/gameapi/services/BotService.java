package com.demidovn.fruitbounty.gameapi.services;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;

public interface BotService {

  void setLevel2BotScoreThreshold(int minBotScore);

  Player createNewBot(int botRating);

  Player createTrainer();

  boolean isPlayerBot(Player player);

  void actionIfBot(Game game);
}
