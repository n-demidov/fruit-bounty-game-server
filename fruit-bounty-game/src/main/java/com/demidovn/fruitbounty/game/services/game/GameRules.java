package com.demidovn.fruitbounty.game.services.game;

import com.demidovn.fruitbounty.game.services.game.rules.CaptureCellsLogic;
import com.demidovn.fruitbounty.game.services.game.rules.CapturableCellsFinder;
import com.demidovn.fruitbounty.game.services.game.rules.CurrentPlayerSwitch;
import com.demidovn.fruitbounty.game.services.game.rules.ending.GameEndingByMoving;
import com.demidovn.fruitbounty.game.services.game.rules.ending.GameEndingBySurrendering;
import com.demidovn.fruitbounty.game.services.game.rules.MoveCorrectness;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import java.util.List;

public class GameRules {

  private final MoveCorrectness moveCorrectness = new MoveCorrectness();
  private final CapturableCellsFinder capturableCellsFinder = new CapturableCellsFinder();
  private final CaptureCellsLogic captureCellsLogic = new CaptureCellsLogic();
  private final GameEndingBySurrendering gameEndingBySurrendering = new GameEndingBySurrendering();
  private final GameEndingByMoving gameEndingByMoving = new GameEndingByMoving();
  private final CurrentPlayerSwitch currentPlayerSwitch = new CurrentPlayerSwitch();

  public boolean isMoveValid(GameAction gameAction) {
    return moveCorrectness.isMoveValid(gameAction);
  }

  public List<Cell> findCapturableCells(GameAction gameAction) {
    return capturableCellsFinder.findCapturableCells(gameAction);
  }

  public void captureCells(List<Cell> capturedCells, GameAction gameAction) {
    captureCellsLogic.captureCells(capturedCells, gameAction);
  }

  public void switchCurrentPlayer(Game game) {
    currentPlayerSwitch.switchCurrentPlayer(game);
  }

  public void checkGameEndingBySurrendering(Game game) {
    gameEndingBySurrendering.checkGameEndingBySurrendering(game);
  }

  public void checkGameEndingByMoving(Game game) {
    gameEndingByMoving.checkGameEndingByMoving(game);
  }

}
