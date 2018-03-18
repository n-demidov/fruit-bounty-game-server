package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.demidovn.fruitbounty.game.services.game.GameRules;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import java.util.List;
import org.junit.Test;

public class CapturableCellsFinderTest extends AbstractGameRulesTest {

  private static final int TARGET_X = 2, TARGET_Y = 0;

  private GameRules gameRules = new GameRules();

  @Test
  public void testFindCapturedCells() {
    GameAction gameAction = new GameAction();
    gameAction.setGame(generateGame());
    gameAction.setX(TARGET_X);
    gameAction.setY(TARGET_Y);
    gameAction.setActionedPlayerId(PLAYER_ID);

    List<Cell> capturedCells = gameRules.findCapturableCells(gameAction);

    assertThat(capturedCells.size()).isEqualTo(7);
    assertThat(capturedCells).containsAll(markToCapturedCells);
  }

}
