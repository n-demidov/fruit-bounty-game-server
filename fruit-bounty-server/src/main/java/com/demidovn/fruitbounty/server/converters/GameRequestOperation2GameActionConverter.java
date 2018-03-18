package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.server.AppConstants;
import com.demidovn.fruitbounty.server.dto.operations.request.GameRequestOperation;
import org.springframework.stereotype.Component;

@Component
public class GameRequestOperation2GameActionConverter implements FruitServerConverter<GameRequestOperation, GameAction> {

  @Override
  public GameAction convert(GameRequestOperation operation) {
    GameAction gameAction = new GameAction();

    gameAction.setGame(operation.getGame());
    gameAction.setActionedPlayerId(operation.getConnection().getUserId());
    gameAction.setType(GameActionType.valueOf(
      operation.getData().get(AppConstants.GAME_ACTION_TYPE)));

    if (gameAction.getType() == GameActionType.Move) {
      gameAction.setX(Integer.valueOf(
        operation.getData().get(AppConstants.GAME_ACTION_MOVE_X_COORDINATE)));
      gameAction.setY(Integer.valueOf(
        operation.getData().get(AppConstants.GAME_ACTION_MOVE_Y_COORDINATE)));
    }

    return gameAction;
  }

}
