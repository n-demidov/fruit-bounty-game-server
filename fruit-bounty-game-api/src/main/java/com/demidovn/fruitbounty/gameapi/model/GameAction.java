package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // todo: remove it later
@Data
public class GameAction {

  private long createdTime = System.currentTimeMillis();
  private Game game;
  private long actionedPlayerId;

  private GameActionType type;
  private int x, y;

  public GameAction(Game game, long actionedPlayerId, GameActionType type, int x, int y) {
    this.game = game;
    this.actionedPlayerId = actionedPlayerId;
    this.type = type;
    this.x = x;
    this.y = y;
  }

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
