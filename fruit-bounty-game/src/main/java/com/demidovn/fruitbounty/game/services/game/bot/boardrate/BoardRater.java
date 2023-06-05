package com.demidovn.fruitbounty.game.services.game.bot.boardrate;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Player;

public interface BoardRater {

  int rateBoard(Cell[][] board, Player targetPlayer, Player nextPlayer);

}
