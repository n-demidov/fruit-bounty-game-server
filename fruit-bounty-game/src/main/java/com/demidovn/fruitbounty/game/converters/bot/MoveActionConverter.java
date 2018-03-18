package com.demidovn.fruitbounty.game.converters.bot;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import org.springframework.stereotype.Component;

@Component
public class MoveActionConverter {

  public GameAction convert2MoveAction(Game game, int x, int y) {
    GameAction gameAction = new GameAction();

    gameAction.setGame(game);
    gameAction.setActionedPlayerId(game.getCurrentPlayer().getId());
    gameAction.setType(GameActionType.Move);
    gameAction.setX(x);
    gameAction.setY(y);

    return gameAction;
  }

}
