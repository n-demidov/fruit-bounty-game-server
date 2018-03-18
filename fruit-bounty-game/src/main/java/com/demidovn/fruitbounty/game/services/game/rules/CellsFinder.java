package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.ArrayList;
import java.util.List;

public class CellsFinder {

  public List<Cell> getNeighborCells(Cell[][] cells, int x, int y) {
    List<Cell> neighborCells = new ArrayList<>();

    if (x > 0) {
      neighborCells.add(cells[x - 1][y]);
    }

    if (x < cells.length - 1) {
      neighborCells.add(cells[x + 1][y]);
    }

    if (y > 0) {
      neighborCells.add(cells[x][y - 1]);
    }

    if (y < cells[0].length - 1) {
      neighborCells.add(cells[x][y + 1]);
    }

    return neighborCells;
  }

  public boolean isBordersWithPlayerCell(int x, int y, Cell[][] cells, long playerId) {
    return getNeighborCells(cells, x, y)
      .stream()
      .anyMatch(neighborCell -> neighborCell.getOwner() == playerId);
  }

  public boolean isBordersWithPlayerCell(Cell cell, Cell[][] cells, long playerId) {
    return isBordersWithPlayerCell(cell.getX(), cell.getY(), cells, playerId);
  }

}
