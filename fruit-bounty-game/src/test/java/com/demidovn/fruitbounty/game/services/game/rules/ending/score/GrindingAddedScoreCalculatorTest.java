package com.demidovn.fruitbounty.game.services.game.rules.ending.score;

import static org.assertj.core.api.Assertions.assertThat;

import com.demidovn.fruitbounty.gameapi.model.Player;
import org.junit.Test;

public class GrindingAddedScoreCalculatorTest {

  private static final int MIN_MODIFIER = 1;
  private static final int MAX_SCORE = 16;

  private final GrindingAddedScoreCalculator grindingAddedScoreCalculator = new GrindingAddedScoreCalculator();

  @Test
  public void test1() {
    Player winner = createPlayer(0);
    Player looser = createPlayer(0);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MIN_MODIFIER);
  }

  @Test
  public void test2() {
    Player winner = createPlayer(10_000);
    Player looser = createPlayer(99);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MIN_MODIFIER);
  }

  @Test
  public void test3() {
    Player winner = createPlayer(1000);
    Player looser = createPlayer(100);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MIN_MODIFIER + 1);
  }

  @Test
  public void test4() {
    Player winner = createPlayer(3432);
    Player looser = createPlayer(101);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MIN_MODIFIER + 1);
  }

  @Test
  public void test5() {
    Player winner = createPlayer(4);
    Player looser = createPlayer(199);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MIN_MODIFIER + 1);
  }

  @Test
  public void test6() {
    Player winner = createPlayer(1100);
    Player looser = createPlayer(1799);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MAX_SCORE);
  }

  @Test
  public void test7() {
    Player winner = createPlayer(1799);
    Player looser = createPlayer(1800);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MAX_SCORE);
  }

  @Test
  public void test8() {
    Player winner = createPlayer(4);
    Player looser = createPlayer(9000);

    int actual = grindingAddedScoreCalculator.findWinnerAddedScore(winner, looser);

    assertThat(actual).isEqualTo(MAX_SCORE);
  }

  private Player createPlayer(int score) {
    Player player = new Player();
    player.setScore(score);
    return player;
  }

}