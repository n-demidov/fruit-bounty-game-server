package com.demidovn.fruitbounty.game.services.game.generating;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.list.ListExtractor;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.List;

public class InitPlayersCellsConfigurator {

  private final ListExtractor listExtractor = new ListExtractor();

  public void configureInitPlayersCells(Game game) {
    List<Player> players = validPlayersCount(game);

    Cell[][] cells = game.getBoard().getCells();
    final int secondPlayerCellX = cells.length - 1;
    final int secondPlayerCellY = cells[0].length - 1;

    Cell firstPlayerCell = cells[0][0];
    Cell secondPlayerCell = cells[secondPlayerCellX][secondPlayerCellY];

    List<Integer> possibleCellTypes = new ArrayList<>(GameOptions.ALL_CELL_TYPES);

    setRandomType(firstPlayerCell, possibleCellTypes);
    setRandomType(secondPlayerCell, possibleCellTypes);

    setRandomType(cells[0][1], possibleCellTypes);
    setRandomType(cells[1][0], possibleCellTypes);

    setRandomType(cells[secondPlayerCellX][secondPlayerCellY - 1], possibleCellTypes);
    setRandomType(cells[secondPlayerCellX - 1][secondPlayerCellY], possibleCellTypes);

    setOwners(players, firstPlayerCell, secondPlayerCell);
  }

  private void setRandomType(Cell cell, List<Integer> possibleCellTypes) {
    int extractedRandomType = listExtractor.extractRandomValue(possibleCellTypes);
    cell.setType(extractedRandomType);
  }

  private void setOwners(List<Player> players, Cell firstPlayerCell, Cell secondPlayerCell) {
    firstPlayerCell.setOwner(players.get(0).getId());
    secondPlayerCell.setOwner(players.get(1).getId());
  }

  private List<Player> validPlayersCount(Game game) {
    List<Player> players = game.getPlayers();
    if (players.size() != 2) {
      throw new UnsupportedOperationException("Now there is supporting of 2 players only!");
    }
    return players;
  }

}
