package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.gameapi.model.Player;

public class ChessLikeAddedScoreCalculator {

  private static final int DEFAULT_MODIFIER = 5;
  private static final int WINNER_GREATER_SCORE_MODIFIER = 100;
  private static final int WINNER_LESS_SCORE_MODIFIER = 75;
  private static final int MINIMUM_ADDED_RATING = 1;

  public int findWinnerAddedScore(Player winner, Player looser) {
    int winnerScore = winner.getScore();
    int looserScore = looser.getScore();

    int playersDiffScore = Math.abs(winnerScore - looserScore);

    if (winnerScore > looserScore) {
      int playersDiffLevel = playersDiffScore / WINNER_GREATER_SCORE_MODIFIER;
      int addedScore = DEFAULT_MODIFIER - playersDiffLevel;
      if (addedScore < MINIMUM_ADDED_RATING) {
        addedScore = MINIMUM_ADDED_RATING;
      }

      return addedScore;
    } else {
      int playersDiffLevel = playersDiffScore / WINNER_LESS_SCORE_MODIFIER;

      return DEFAULT_MODIFIER + playersDiffLevel;
    }
  }
  
}
