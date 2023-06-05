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
    GameActionType gameActionType = GameActionType.valueOf(
        operation.getData().get(AppConstants.GAME_ACTION_TYPE));
    int x = -1, y = -1;
    if (gameActionType == GameActionType.Move) {
      x = Integer.parseInt(
          operation.getData().get(AppConstants.GAME_ACTION_MOVE_X_COORDINATE));
      y = Integer.parseInt(
          operation.getData().get(AppConstants.GAME_ACTION_MOVE_Y_COORDINATE));
    }

    return new GameAction(
        operation.getGame(),
        operation.getConnection().getUserId(),
        gameActionType,
        x,
        y);
  }

}
