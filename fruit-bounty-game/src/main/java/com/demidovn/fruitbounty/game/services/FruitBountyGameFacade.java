package com.demidovn.fruitbounty.game.services;

import com.demidovn.fruitbounty.game.services.game.generating.GameCreator;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.GameFacade;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FruitBountyGameFacade implements GameFacade {

  @Autowired
  private GameCreator gameCreator;

  private final List<Game> games = new CopyOnWriteArrayList<>();

  @Override
  public Game startGame(List<Player> players, boolean isTutorial) {
    Game game = gameCreator.createNewGame(players, isTutorial);
    games.add(game);

    return game;
  }

  @Override
  public void processGameAction(GameAction gameAction) {
    gameAction.getGame().getGameActions().add(gameAction);
  }

  @Override
  public List<Game> getAllGames() {
    return games;
  }

  public void gamesFinished(List<Game> finishedGames) {
    if (!finishedGames.isEmpty()) {
      games.removeAll(finishedGames);

      log.trace("{} games were removed, current games={}", finishedGames.size(), games.size());
    }
  }

}
