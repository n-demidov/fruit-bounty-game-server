package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGameRules {

  protected final Random rand = new Random();
  private final CellsFinder cellsFinder = new CellsFinder();

  public List<Cell> getNeighborCells(Cell[][] cells, int x, int y) {
    return cellsFinder.getNeighborCells(cells, x, y);
  }

  protected boolean isBordersWithPlayerCell(int x, int y, Cell[][] cells, long playerId) {
    return cellsFinder.isBordersWithPlayerCell(x, y, cells, playerId);
  }

  public boolean isBordersWithPlayerCell(Cell cell, Cell[][] cells, long playerId) {
    return isBordersWithPlayerCell(cell.getX(), cell.getY(), cells, playerId);
  }

  protected List<Cell> getOwnedCells(Cell[][] cells, long playerId) {
    List<Cell> ownedCells = new ArrayList<>();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() == playerId) {
          ownedCells.add(cell);
        }
      }
    }

    return ownedCells;
  }

  protected Map<Long, Integer> findPlayersCellTypes(Cell[][] cells) {
    Map<Long, Integer> playersCellTypes = new HashMap<>();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() != 0) {
          playersCellTypes.put(cell.getOwner(), cell.getType());
        }
      }
    }

    return playersCellTypes;
  }

}
