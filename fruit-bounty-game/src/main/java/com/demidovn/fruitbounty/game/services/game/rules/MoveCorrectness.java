package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveCorrectness extends AbstractGameRules {

  public boolean isMoveValid(GameAction gameAction) {
    return isCellUnoccupied(gameAction) &&
      isPlayerCurrent(gameAction) &&
      isActionBeforeGameExpired(gameAction) &&
      isBordersWithPlayerCell(gameAction) &&
      !isTypeOccupiedByOtherPlayers(gameAction);
  }

  private boolean isCellUnoccupied(GameAction gameAction) {
    int x = gameAction.getX();
    int y = gameAction.getY();
    Cell cell = gameAction.getGame().getBoard().getCells()[x][y];

    return cell.getOwner() == 0;
  }

  private boolean isPlayerCurrent(GameAction gameAction) {
    Player actionedPlayer = gameAction.findActionedPlayer();
    return actionedPlayer.equals(gameAction.getGame().getCurrentPlayer());
  }

  private boolean isActionBeforeGameExpired(GameAction gameAction) {
    return gameAction.getGame().getCurrentMoveStarted() + GameOptions.TIME_PER_MOVE
      + GameOptions.MOVE_TIME_DELAY_CORRECTION > Instant.now().toEpochMilli();
  }

  private boolean isBordersWithPlayerCell(GameAction gameAction) {
    return isBordersWithPlayerCell(
      gameAction.getX(),
      gameAction.getY(),
      gameAction.getGame().getBoard().getCells(),
      gameAction.getActionedPlayerId());
  }

  private boolean isTypeOccupiedByOtherPlayers(GameAction gameAction) {
    Cell targetCell = gameAction.getTargetCell();

    return gameAction.getGame().getPlayers()
      .stream()
      .filter(player -> player.getId() != gameAction.getActionedPlayerId())
      .anyMatch(player ->
        targetCell.getType() == getOwnedCell(gameAction, player.getId()).getType());
  }

  private Cell getOwnedCell(GameAction gameAction, long playerId) {
    Cell[][] cells = gameAction.getGame().getBoard().getCells();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() == playerId) {
          return cell;
        }
      }
    }

    String errorMsg = String.format("Can't find owned cells for player %d", playerId);
    log.error(errorMsg);
    throw new IllegalArgumentException(errorMsg);
  }

}
