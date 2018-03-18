package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import java.util.ArrayList;
import java.util.List;

public class CapturableCellsFinder extends AbstractGameRules {

  public List<Cell> findCapturableCells(GameAction gameAction) {
    int x = gameAction.getX();
    int y = gameAction.getY();
    Cell[][] cells = gameAction.getGame().getBoard().getCells();
    int initType = cells[x][y].getType();

    List<Cell> capturedCells = new ArrayList<>();

    deepTargetCell(capturedCells, cells, initType, x, y);
    deepOwnedCells(capturedCells, cells, initType, gameAction);

    return capturedCells;
  }

  private void deepTargetCell(List<Cell> capturedCells, Cell[][] cells, int initType, int x, int y) {
    deep(capturedCells, cells, initType, x, y);
  }

  private void deepOwnedCells(List<Cell> capturedCells, Cell[][] cells, int initType, GameAction gameAction) {
    List<Cell> ownedCells = getOwnedCells(
      gameAction.getGame().getBoard().getCells(), gameAction.getActionedPlayerId());

    ownedCells.forEach(ownedCell ->
      deep(capturedCells, cells, initType, ownedCell.getX(), ownedCell.getY()));
  }

  private void deep(List<Cell> capturedCells, Cell[][] cells, int initType, int x, int y) {
    Cell capturedCell = cells[x][y];
    if (capturedCell.getOwner() == 0) {
      capturedCells.add(capturedCell);
    }

    for (Cell neighborCell : getNeighborCells(cells, x, y)) {
      if (isNeighborCellCapturable(neighborCell, capturedCells, initType)) {
        deep(capturedCells, cells, initType,
          neighborCell.getX(), neighborCell.getY());
      }
    }
  }

  private boolean isNeighborCellCapturable(Cell checkingCell, List<Cell> capturedCells, int initType) {
    return checkingCell.getType() == initType &&
      checkingCell.getOwner() == 0 &&
      !capturedCells.contains(checkingCell);
  }

}
