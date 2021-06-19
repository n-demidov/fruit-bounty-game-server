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
  private List<Player> players;
  private Player currentPlayer;
  private boolean isTutorial;

  @JsonIgnore
  private long timePerMoveMs;
  @JsonIgnore
  private long currentMoveStarted;
  private long clientCurrentMoveTimeLeft;

  private boolean isFinished;
  private Player winner;

  @JsonIgnore
  private final Queue<GameAction> gameActions = new ConcurrentLinkedQueue<>();

  public void setCurrentPlayer(Player player) {
    this.currentPlayer = player;
    this.currentMoveStarted = Instant.now().toEpochMilli();
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
      ", isFinished=" + isFinished +
      ", winner=" + winner +
      '}';
  }

}
