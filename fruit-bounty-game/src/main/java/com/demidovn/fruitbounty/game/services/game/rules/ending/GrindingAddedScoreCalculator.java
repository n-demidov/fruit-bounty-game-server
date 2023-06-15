package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class GrindingAddedScoreCalculator {

  public int findWinnerAddedScore(Player winner, Player looser) {
    int looserScore = looser.getScore();

    int result = ((int) Math.abs(looserScore) / 100) + 1;

    return result;
  }
  
}
