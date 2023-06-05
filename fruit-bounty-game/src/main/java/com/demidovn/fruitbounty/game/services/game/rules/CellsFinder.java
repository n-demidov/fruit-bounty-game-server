package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  public Cell getOwnedCell(long playerId, Cell[][] cells) {
    // todo: it's may be make sense to store starting cells for player
    Cell cell = cells[0][0];
    if (cell.getOwner() == playerId) {
      return cell;
    }

    cell = cells[cells.length - 1][cells[cells.length - 1].length - 1];
    if (cell.getOwner() == playerId) {
      return cell;
    }

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        cell = cells[x][y];
        if (cell.getOwner() == playerId) {
          return cell;
        }
      }
    }

    String errorMsg = String.format("Can't find owned cells for player %d", playerId);
    log.error(errorMsg);
    throw new IllegalArgumentException(errorMsg);
  }

  public int countCells(Cell[][] cells, long owner) {
    int result = 0;
    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() == owner) {
          result++;
        }
      }
    }

    return result;
  }

}
