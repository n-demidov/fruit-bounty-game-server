package com.demidovn.fruitbounty.game.services.game.rules.ending;

import com.demidovn.fruitbounty.game.model.BooleanContext;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEndingByMoving extends GameEnding {

  public void checkGameEndingByMoving(Game game) {
    BooleanContext isAllCellsOwned = new BooleanContext();

    Map<Long, Integer> playersCellsCount = preparePlayersCellsCount(game);
    fillPlayersCellsCount(game, playersCellsCount, isAllCellsOwned);

    if (!isAllCellsOwned.isBool) {
      return;
    }

    Player winner = findMostConqueredCellsPlayer(playersCellsCount, game);

    finishGame(game, winner);
  }

  private Map<Long, Integer> preparePlayersCellsCount(Game game) {
    Map<Long, Integer> capturedCells = new HashMap<>();

    for (Player player : game.getPlayers()) {
      capturedCells.put(player.getId(), 0);
    }

    return capturedCells;
  }

  private void fillPlayersCellsCount(Game game, Map<Long, Integer> playersCellsCount,
    BooleanContext isAllCellsOwned) {
    Cell[][] cells = game.getBoard().getCells();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];

        if (cell.getOwner() == 0) {
          isAllCellsOwned.isBool = false;
          return;
        }

        incrementCellCount(playersCellsCount, cell, game);
      }
    }

    isAllCellsOwned.isBool = true;
  }

  private void incrementCellCount(Map<Long, Integer> capturedCells, Cell cell, Game game) {
    Integer count = capturedCells.get(cell.getOwner());
    Player player = game.findPlayer(cell.getOwner());

    if (!player.isSurrendered()) {
      capturedCells.put(cell.getOwner(), count + 1);
    }
  }

  private Player findMostConqueredCellsPlayer(Map<Long, Integer> playersCellsCount, Game game) {
    List<Long> playersCells = new ArrayList<>(playersCellsCount.keySet());

    if (playersCells.size() > 2) {
      throw new UnsupportedOperationException("Now there is supporting only of 2 players!");
    }

    Long firstPlayer = playersCells.get(0);
    Long secondPlayer = playersCells.get(1);

    Integer firstPlayerCellsCount = playersCellsCount.get(firstPlayer);
    Integer secondPlayerCellsCount = playersCellsCount.get(secondPlayer);

    if (firstPlayerCellsCount.equals(secondPlayerCellsCount)) {
      return null;
    } else if (firstPlayerCellsCount > secondPlayerCellsCount) {
      return game.findPlayer(firstPlayer);
    } else {
      return game.findPlayer(secondPlayer);
    }
  }

}
