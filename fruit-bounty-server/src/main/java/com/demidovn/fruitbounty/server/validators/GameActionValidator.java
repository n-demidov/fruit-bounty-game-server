package com.demidovn.fruitbounty.server.validators;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.server.AppConstants;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.exceptions.RequestOperationValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameActionValidator {

  public void valid(RequestOperation operation, Game game) {
    validPlayerExists(operation, game);

    validGameActionType(operation);
    validCoordinates(operation, game);
  }

  private void validPlayerExists(RequestOperation operation, Game game) {
    long actionedPlayerId = operation.getConnection().getUserId();

    boolean isPlayerExists = game.getPlayers()
      .stream()
      .anyMatch(player -> player.getId() == actionedPlayerId);

    if (!isPlayerExists) {
      log.warn("No such player {} in game={}", actionedPlayerId, game);
      throw new RequestOperationValidationException("No such player in game");
    }
  }

  private void validGameActionType(RequestOperation operation) {
    String gameActionType = operation.getData().get(AppConstants.GAME_ACTION_TYPE);

    boolean isContains = GameActionType.contains(gameActionType);

    if (!isContains) {
      log.warn("No such value {} exists in enum", gameActionType);
      throw new RequestOperationValidationException("No such value exists in enum");
    }
  }

  private void validCoordinates(RequestOperation operation, Game game) {
    String gameActionTypeString = operation.getData().get(AppConstants.GAME_ACTION_TYPE);
    GameActionType gameActionType = GameActionType.valueOf(gameActionTypeString);

    if (gameActionType == GameActionType.Move) {
      String x = operation.getData().get(AppConstants.GAME_ACTION_MOVE_X_COORDINATE);
      String y = operation.getData().get(AppConstants.GAME_ACTION_MOVE_Y_COORDINATE);

      if (!isCoordinateValid(x, game.getBoard().getCells().length - 1)
        || !isCoordinateValid(y, game.getBoard().getCells()[0].length - 1)) {
        log.warn("Not valid coordinates x={}, y={}", x, y);
        throw new RequestOperationValidationException("Not valid coordinates x; y");
      }
    }
  }

  private boolean isCoordinateValid(String coordinate, int maxValidValue) {
    if (!isInteger(coordinate)) {
      return false;
    }

    int coord = Integer.parseInt(coordinate);

    return !(coord < 0 || coord > maxValidValue);
  }

  private static boolean isInteger(String s) {
    try
    {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException ex)
    {
      return false;
    }
  }

}
