package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class GrindingAddedScoreCalculator {
  private static final int MAX_SCORE = 16;

  public int findWinnerAddedScore(Player winner, Player looser) {
    int looserScore = looser.getScore();

    int result = ((int) Math.abs(looserScore) / 100) + 1;

    if (result > MAX_SCORE) {
      result = MAX_SCORE;
    }

    return result;
  }
  
}
