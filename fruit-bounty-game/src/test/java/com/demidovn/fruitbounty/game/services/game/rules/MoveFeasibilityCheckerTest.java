package com.demidovn.fruitbounty.game.services.game.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MoveFeasibilityCheckerTest {

  private static final int WIDTH = 4, HEIGHT = 3;

  private static final int DEFAULT_CELL_TYPE = 5;
  private static final int FIRST_CELL_TYPE = 6;
  private static final int SECOND_CELL_TYPE = 7;
  private static final int FREE_CELL_TYPE = 8;

  private static final int FIRST_PLAYER_ID = 1;
  private static final int SECOND_PLAYER_ID = 2;
  private static final int THIRD_PLAYER_ID = 3;

  private Cell[][] cells;

  private MoveFeasibilityChecker moveFeasibilityChecker = new MoveFeasibilityChecker();

  @Before
  public void before() {
    createDefaultBoard();
    setOwnersToCells();
    setCustomTypesToCells();
  }

  @Test
  public void whenNoAnyFeasibleMove() {
    boolean actual = moveFeasibilityChecker.isAnyMoveFeasible(cells, FIRST_PLAYER_ID);

    assertFalse(actual);
  }

  @Test
  public void whenThereIsFeasibleMove() {
    List<Integer> otherPlayers = Arrays.asList(SECOND_PLAYER_ID, THIRD_PLAYER_ID);

    for (Integer otherPlayer : otherPlayers) {
      boolean actual = moveFeasibilityChecker.isAnyMoveFeasible(cells, otherPlayer);

      assertTrue(actual);
    }
  }

  /* Plan for game Board:
      ___________
     |5 |1 |5 |1x|
     |1 |8p|2 |8p|
     |5 |2 |5 |2y|
   */
  private void createDefaultBoard() {
    cells = new Cell[WIDTH][HEIGHT];

    for (int x = 0; x < WIDTH; x++) {
      for (int y = 0; y < HEIGHT; y++) {
        cells[x][y] = new Cell(DEFAULT_CELL_TYPE, x, y);
      }
    }
  }

  private void setOwnersToCells() {
    cells[1][1].setOwner(FIRST_PLAYER_ID);
    cells[3][1].setOwner(FIRST_PLAYER_ID);

    cells[3][0].setOwner(SECOND_PLAYER_ID);
    cells[3][2].setOwner(THIRD_PLAYER_ID);
  }

  private void setCustomTypesToCells() {
    cells[1][1].setType(FREE_CELL_TYPE);
    cells[3][1].setType(FREE_CELL_TYPE);

    cells[1][0].setType(FIRST_CELL_TYPE);
    cells[3][0].setType(FIRST_CELL_TYPE);
    cells[0][1].setType(FIRST_CELL_TYPE);

    cells[2][1].setType(SECOND_CELL_TYPE);
    cells[1][2].setType(SECOND_CELL_TYPE);
    cells[3][2].setType(SECOND_CELL_TYPE);
  }

}