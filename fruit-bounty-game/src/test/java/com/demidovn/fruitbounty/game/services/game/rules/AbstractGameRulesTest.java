package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.List;

public class AbstractGameRulesTest {

  private static final int BOARD_WIDTH = 6, BOARD_HEIGHT = 3;
  private static final int DEFAULT_TYPE = 1;
  private static final int OTHER_TYPE = 2;
  protected static final int PLAYER_ID = 9, OTHER_PLAYER_ID = 10;

  protected List<Cell> markToCapturedCells = new ArrayList<>();

  protected Game generateGame() {
    Game game = new Game();

    game.setBoard(generateBoard());
    game.setPlayers(generatePlayers());

    return game;
  }

  protected List<Player> generatePlayers() {
    return null;
  }

  /* Board plan:
     _________________
    |2 |2 |2x|2 |1 |1 |
    |2 |1 |2p|1 |2p|2 |
    |1 |1 |1 |2 |2o|2 |
   */
  private Board generateBoard() {
    Cell[][] cells = generateCells();

    setShouldCapturedCells(cells);
    setShouldNotCapturedCells(cells);

    return new Board(cells);
  }

  private Cell[][] generateCells() {
    Cell[][] cells = new Cell[BOARD_WIDTH][BOARD_HEIGHT];
    for (int x = 0; x < BOARD_WIDTH; x++) {
      for (int y = 0; y < BOARD_HEIGHT; y++) {
        cells[x][y] = new Cell(DEFAULT_TYPE, x, y);
      }
    }
    return cells;
  }

  private void setShouldCapturedCells(Cell[][] cells) {
    markToCaptured(cells[0][0]);
    markToCaptured(cells[1][0]);
    markToCaptured(cells[2][0]);
    markToCaptured(cells[3][0]);

    markToCaptured(cells[0][1]);

    markToCaptured(cells[5][1]);
    markToCaptured(cells[5][2]);
  }

  private void markToCaptured(Cell cell) {
    cell.setType(OTHER_TYPE);
    markToCapturedCells.add(cell);
  }

  private void setShouldNotCapturedCells(Cell[][] cells) {
    cells[2][1].setType(OTHER_TYPE);
    cells[2][1].setOwner(PLAYER_ID);

    cells[4][1].setType(OTHER_TYPE);
    cells[4][1].setOwner(PLAYER_ID);

    cells[3][2].setType(OTHER_TYPE);

    cells[4][2].setType(OTHER_TYPE);
    cells[4][2].setOwner(OTHER_PLAYER_ID);
  }

}
