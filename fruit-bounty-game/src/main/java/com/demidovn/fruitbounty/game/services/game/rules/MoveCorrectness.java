package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MoveCorrectness extends AbstractGameRules {
  private static final CellsFinder cellsFinder = new CellsFinder();

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
    return gameAction.getGame().getCurrentMoveStarted() + gameAction.getGame().getTimePerMoveMs()
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
        targetCell.getType() == cellsFinder.getOwnedCell(
            player.getId(), gameAction.getGame().getBoard().getCells()).getType());
  }

}
