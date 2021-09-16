package com.demidovn.fruitbounty.gameapi.services;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;

public interface GameFacade {

  Game startGame(List<Player> players, boolean isTutorial);

  void processGameAction(GameAction gameAction);

  List<Game> getAllGames();

}
