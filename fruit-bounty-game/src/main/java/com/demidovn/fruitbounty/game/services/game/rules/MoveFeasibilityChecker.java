package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoveFeasibilityChecker extends AbstractGameRules {

  public boolean isAnyMoveFeasible(Cell[][] cells, long playerId) {
    Collection<Integer> busyCellTypes = findBusyCellTypes(cells, playerId);

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        List<Long> neighborCellsTypes = getNeighborCells(cells, x, y)
          .stream()
          .map(Cell::getOwner)
          .collect(Collectors.toList());

        if (cell.getOwner() == 0 &&
          !busyCellTypes.contains(cell.getType()) &&
          neighborCellsTypes.contains(playerId)) {
          return true;
        }
      }
    }

    return false;
  }

  private Collection<Integer> findBusyCellTypes(Cell[][] cells, long playerId) {
    Map<Long, Integer> playersCellTypes = findPlayersCellTypes(cells);
    playersCellTypes.remove(playerId);

    return playersCellTypes.values();
  }

}
