package com.demidovn.fruitbounty.game.services.game.bot.boardrate;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Player;

public class MovementToCenterBoardRater implements BoardRater {

  private static final double SELF_CELLS_MULTIPLIER = 3.5;

  public int rateBoard(Cell[][] board, Player targetPlayer, Player nextPlayer) {
    return (int) (rateBoard(board, targetPlayer) * SELF_CELLS_MULTIPLIER) - rateBoard(board, nextPlayer);
  }

  public int rateCell(Cell cell, Cell[][] cells) {
    return countCoordinateCost(cell.getX(), cells.length - 1) +
        countCoordinateCost(cell.getY(), cells[0].length - 1);
  }

  public int rateBoard(Cell[][] board, Player targetPlayer) {
    int result = 0;
    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        Cell cell = board[x][y];

        if (cell.getOwner() == targetPlayer.getId()) {
          int rate = rateCell(cell, board);
          result += rate;
        }
      }
    }
    return result;
  }

  private int countCoordinateCost(int coord, int maxIndex) {
    int maxIterationsCosts = maxIndex / 2;

    for (int i = 0; i < maxIterationsCosts; i++) {
      if (coord == i || coord == maxIndex - i) {
        return i * i * maxIndex;
      }
    }

    return maxIterationsCosts * maxIterationsCosts * maxIndex;
  }

}
