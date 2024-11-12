package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class CommonAddedScoreCalculator {
  private final GrindingAddedScoreCalculator grindingAddedScoreCalculator = new GrindingAddedScoreCalculator();
  private final BoardPercentAddedScoreCalculator boardPercentAddedScoreCalculator = new BoardPercentAddedScoreCalculator();

  public int findWinnerAddedScore(Player winner, Player looser, int playerCellsNum, int allCellsNum) {
    int winnerAddedScore = 0;

    winnerAddedScore =
        grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser)
            * boardPercentAddedScoreCalculator.findWinnerAddedScore(playerCellsNum, allCellsNum)
            / 100;

    return winnerAddedScore;
  }
  
}
