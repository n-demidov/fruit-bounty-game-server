package com.demidovn.fruitbounty.game.services.game.bot.level;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.gameapi.model.Game;

public interface BotMover {

  Pair<Integer, Integer> findMove(Game game);

}
