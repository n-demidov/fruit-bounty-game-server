package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;

@Data
public class GameAction {

  private Game game;
  private long actionedPlayerId;
  private long createdTime;

  private GameActionType type;
  private int x, y;

  public Player findActionedPlayer() {
    return game.getPlayers()
        .stream()
        .filter(player -> player.getId() == actionedPlayerId)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format(
          "Can't find player by actionedPlayerId=%s, players=%s",
          actionedPlayerId, game.getPlayers())));
  }

  public Cell getTargetCell() {
    return game.getBoard().getCells()[x][y];
  }

  public Cell findCell(int x, int y) {
    return game.getBoard().getCells()[x][y];
  }

}
