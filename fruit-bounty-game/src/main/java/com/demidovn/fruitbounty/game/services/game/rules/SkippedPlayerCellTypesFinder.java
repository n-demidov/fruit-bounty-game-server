package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SkippedPlayerCellTypesFinder extends AbstractGameRules {
  private static final PlayersFinder playersFinder = new PlayersFinder();

  public List<Integer> findPossibleCellTypes(Game game, long playerId) {
    playersFinder.validPlayersCount(game);

    List<Integer> possibleCellTypes = new ArrayList<>(GameOptions.ALL_CELL_TYPES);
    removePlayersCellTypes(game, possibleCellTypes);
    Set<Integer> opponentPossibleConquerTypes = getOpponentPossibleConquerTypes(game, playerId);

    if (opponentPossibleConquerTypes.size() == 1) {
      possibleCellTypes.removeAll(opponentPossibleConquerTypes);
    }

    return possibleCellTypes;
  }

  private void removePlayersCellTypes(Game game, List<Integer> possibleCellTypes) {
    Map<Long, Integer> playersCellTypes = findPlayersCellTypes(game.getBoard().getCells());
    possibleCellTypes.removeAll(playersCellTypes.values());
  }

  private Set<Integer> getOpponentPossibleConquerTypes(Game game, long playerId) {
    Player opponent = playersFinder.getNextPlayer(game, playerId);
    Cell[][] cells = game.getBoard().getCells();
    Set<Integer> opponentPossibleConquerTypes = new HashSet<>();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() == 0
          && isBordersWithPlayerCell(cell, cells, opponent.getId())) {
          opponentPossibleConquerTypes.add(cell.getType());
        }
      }
    }

    return opponentPossibleConquerTypes;
  }

}
