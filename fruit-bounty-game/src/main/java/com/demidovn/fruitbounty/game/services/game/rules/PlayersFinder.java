package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayersFinder {

  public Player getNextPlayer(Game game, long playerId) {
    List<Player> players = game.getPlayers();

    validPlayersCount(game);

    if (playerId == players.get(0).getId()) {
      return players.get(1);
    } else {
      return players.get(0);
    }
  }

  public void validPlayersCount(Game game) {
    if (game.getPlayers().size() > 2) {
      String errMsg = String.format("There is supporting only of 2 players! game=%s", game);

      log.error(errMsg);
      throw new UnsupportedOperationException(errMsg);
    }
  }

}
