package com.demidovn.fruitbounty.game.converters.bot;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;

public class MoveActionConverter {

  public GameAction convert2MoveAction(Game game, int x, int y) {
    return new GameAction(
        game,
        game.getCurrentPlayer().getId(),
        GameActionType.Move,
        x,
        y
    );
  }

}
