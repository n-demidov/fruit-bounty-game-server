package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import static org.assertj.core.api.Assertions.assertThat;

import junit.framework.TestCase;
import org.junit.Test;

public class BoardPercentAddedScoreCalculatorTest extends TestCase {

  private final BoardPercentAddedScoreCalculator boardPercentAddedScoreCalculator = new BoardPercentAddedScoreCalculator();

  @Test
  public void test1() {
    int actual = boardPercentAddedScoreCalculator.findWinnerAddedScore(72, 144);
    assertThat(actual).isEqualTo(100);

    actual = boardPercentAddedScoreCalculator.findWinnerAddedScore(72 + 36, 144);
    assertThat(actual).isEqualTo(150);

    actual = boardPercentAddedScoreCalculator.findWinnerAddedScore(144, 144);
    assertThat(actual).isEqualTo(200);

    actual = boardPercentAddedScoreCalculator.findWinnerAddedScore(36, 144);
    assertThat(actual).isEqualTo(50);

    actual = boardPercentAddedScoreCalculator.findWinnerAddedScore(100, 144);
    assertThat(actual).isEqualTo(138);
  }

}