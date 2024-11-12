package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

public class BoardPercentAddedScoreCalculator {

  public int findWinnerAddedScore(int playerCellsNum, int allCellsNum) {
    int result = playerCellsNum * 200 / allCellsNum;

    return result;
  }
  
}
