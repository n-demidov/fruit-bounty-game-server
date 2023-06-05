package com.demidovn.fruitbounty.game.services.game.bot.boardrate;

import com.demidovn.fruitbounty.game.services.game.rules.CellsFinder;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Player;

public class UniformMovementBoardRater implements BoardRater {

  private static final CellsFinder cellsFinder = new CellsFinder();

  public int rateBoard(Cell[][] board, Player targetPlayer, Player nextPlayer) {
    int count = cellsFinder.countCells(board, targetPlayer.getId());

    if (count >= countHalfBoardNum(board)) {
      count *= count;
    }

    return count;
  }

  private int countHalfBoardNum(Cell[][] board) {
    return board.length * board[0].length / 2 + 1;
  }

}
