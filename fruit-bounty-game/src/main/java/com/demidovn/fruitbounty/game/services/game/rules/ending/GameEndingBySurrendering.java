package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;
import java.util.stream.Collectors;

public class GameEndingBySurrendering extends GameEnding {

  public void checkGameEndingBySurrendering(Game game) {
    List<Player> notSurrenderedPlayers = game.getPlayers()
      .stream()
      .filter(player -> !player.isSurrendered())
      .collect(Collectors.toList());

    if (notSurrenderedPlayers.size() > 1) {
      return;
    }

    Player winner = notSurrenderedPlayers.get(0);
    finishGame(game, winner);
  }

}
