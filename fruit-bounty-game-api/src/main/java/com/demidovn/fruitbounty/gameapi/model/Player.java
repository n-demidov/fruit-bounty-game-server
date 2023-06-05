package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Player {

  private long id;
  private String publicName;
  private int score;
  private boolean isSurrendered;
  private int addedScore;
  private int wins, defeats, draws;
  private String img;

  @JsonIgnore
  private int consecutivelyMissedMoves;

  public static Player copyPlayer(Player fromPlayer) {
    if (fromPlayer == null) {
      return null;
    }

    return new Player(
        fromPlayer.getId(),
        fromPlayer.getPublicName(),
        fromPlayer.getScore(),
        fromPlayer.isSurrendered(),
        fromPlayer.getAddedScore(),
        fromPlayer.getWins(),
        fromPlayer.getDefeats(),
        fromPlayer.getDraws(),
        fromPlayer.getImg(),
        fromPlayer.getConsecutivelyMissedMoves()
    );
  }

  public void incrementMissedMoves() {
    consecutivelyMissedMoves++;
  }

  public void resetConsecutivelyMissedMoves() {
    consecutivelyMissedMoves = 0;
  }

}
