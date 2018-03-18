package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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

  public void incrementMissedMoves() {
    consecutivelyMissedMoves++;
  }

  public void resetConsecutivelyMissedMoves() {
    consecutivelyMissedMoves = 0;
  }

}
