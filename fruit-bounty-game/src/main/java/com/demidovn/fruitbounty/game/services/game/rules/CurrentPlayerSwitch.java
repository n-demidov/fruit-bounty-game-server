package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrentPlayerSwitch extends AbstractGameRules {

  private static final String THERE_IS_DEAD_LOOP_ERR = "There is a dead loop in switchCurrentPlayer";
  private static final int ITERATIONS_MULTIPLIER = 20;

  private final SkippedPlayerCellTypesFinder skippedPlayerCellTypesFinder =
    new SkippedPlayerCellTypesFinder();
  private static final MoveFeasibilityChecker moveFeasibilityChecker = new MoveFeasibilityChecker();
  private static final PlayersFinder playersFinder = new PlayersFinder();

  public void switchCurrentPlayer(Game game) {
    if (game.isFinished()) {
      return;
    }

    int iterations = 0;
    Player player = game.getCurrentPlayer();
    boolean isMoveFeasible;

    do {
      player = playersFinder.getNextPlayer(game, player.getId());

      isMoveFeasible = moveFeasibilityChecker
        .isAnyMoveFeasible(game.getBoard().getCells(), player.getId());

      if (!isMoveFeasible) {
        switchPlayerCells(game, player.getId());
      }

      iterations++;
      if (isDeadLoop(game, iterations)) {
        throw new IllegalStateException(THERE_IS_DEAD_LOOP_ERR + "; game=" + game);
      }
    } while (!isMoveFeasible);

    game.setCurrentPlayer(player);
  }

  private void switchPlayerCells(Game game, long playerId) {
    List<Integer> possibleCellTypes =
      skippedPlayerCellTypesFinder.findPossibleCellTypes(game, playerId);

    switchPlayerCellsToRandom(game, playerId, possibleCellTypes);
  }

  private void switchPlayerCellsToRandom(Game game, long playerId,
    List<Integer> possibleCellTypes) {
    int randomType = possibleCellTypes.get(rand.nextInt(possibleCellTypes.size()));

    getOwnedCells(game.getBoard().getCells(), playerId)
      .forEach(cell -> cell.setType(randomType));
  }

  private boolean isDeadLoop(Game game, int iterations) {
    return iterations > game.getPlayers().size() * ITERATIONS_MULTIPLIER;
  }

}
