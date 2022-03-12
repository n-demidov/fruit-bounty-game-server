package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Data;

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
