package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import java.util.List;

public class CaptureCellsLogic extends AbstractGameRules {

  public void captureCells(List<Cell> capturedCells, GameAction gameAction) {
    changeOldOwnedCellsType(capturedCells, gameAction);
    occupyNewCapturedCells(capturedCells, gameAction);
  }

  private void changeOldOwnedCellsType(List<Cell> capturedCells, GameAction gameAction) {
    int capturedCellType = capturedCells.get(0).getType();
    List<Cell> ownedCells = getOwnedCells(
      gameAction.getGame().getBoard().getCells(), gameAction.getActionedPlayerId());

    ownedCells.forEach(ownedCell ->
      ownedCell.setType(capturedCellType));
  }

  private void occupyNewCapturedCells(List<Cell> capturedCells, GameAction gameAction) {
    long actionedPlayerId = gameAction.getActionedPlayerId();

    capturedCells.forEach(capturedCell ->
      capturedCell.setOwner(actionedPlayerId));
  }

}
