package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  // todo: remove it later?
@AllArgsConstructor
@Data
public class Game {
  private Board board;
  private int turnsCount;
  private boolean isTutorial;
  private List<Player> players;
  private Player currentPlayer;

  private long timePerMoveMs;
  @JsonIgnore
  private long currentMoveStarted;
  private long clientCurrentMoveTimeLeft;  // Only for client

  private boolean isFinished;
  private Player winner;

  @JsonIgnore
  private final Queue<GameAction> gameActions = new ConcurrentLinkedQueue<>();

  public static Game copy(Game fromGame) {
    Cell[][] cells = fromGame.getBoard().getCells();
    Cell[][] copiedCells = new Cell[cells.length][cells[0].length];

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];
        copiedCells[x][y] = new Cell(cell.getOwner(), cell.getType(), cell.getX(), cell.getY());
      }
    }

    return new Game(
        new Board(copiedCells),
        fromGame.turnsCount,
        fromGame.isTutorial,
        fromGame.players.stream().map(Player::copyPlayer).collect(Collectors.toList()),
        Player.copyPlayer(fromGame.currentPlayer),
        fromGame.timePerMoveMs,
        fromGame.currentMoveStarted,
        fromGame.clientCurrentMoveTimeLeft,
        fromGame.isFinished,
        Player.copyPlayer(fromGame.winner)
    );
  }

  public void setCurrentPlayer(Player player) {
    this.currentPlayer = player;
    this.currentMoveStarted = Instant.now().toEpochMilli();
    turnsCount++;
  }

  public Player findPlayer(long playerId) {
    for (Player player : players) {
      if (player.getId() == playerId) {
        return player;
      }
    }

    throw new IllegalStateException(String.format(
      "Can't find player by id '%s' in game=%s",
      playerId,
      this
    ));
  }

  @Override
  public String toString() {
    return "Game{" +
            "players=" + players +
            ", currentPlayer=" + currentPlayer +
            ", isTutorial=" + isTutorial +
            ", timePerMoveMs=" + timePerMoveMs +
            ", currentMoveStarted=" + currentMoveStarted +
            ", clientCurrentMoveTimeLeft=" + clientCurrentMoveTimeLeft +
            ", isFinished=" + isFinished +
            ", winner=" + winner +
            '}';
  }

  public String toFullString() {
    return "Game{" +
            "board=" + board +
            ", turnsCount=" + turnsCount +
            ", isTutorial=" + isTutorial +
            ", players=" + players +
            ", currentPlayer=" + currentPlayer +
            ", timePerMoveMs=" + timePerMoveMs +
            ", currentMoveStarted=" + currentMoveStarted +
            ", clientCurrentMoveTimeLeft=" + clientCurrentMoveTimeLeft +
            ", isFinished=" + isFinished +
            ", winner=" + winner +
            '}';
  }
}
