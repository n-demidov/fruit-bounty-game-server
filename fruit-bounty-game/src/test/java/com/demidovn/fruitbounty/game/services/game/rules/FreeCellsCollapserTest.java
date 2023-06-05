package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.game.rules.FreeCellsCollapser.Figure;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.Test;

public class FreeCellsCollapserTest {

  private static final int CELLS_WIDTH = 11;
  private static final int CELLS_HEIGHT = 16;
  private static final long EM = 0;
  private static final long P1 = 1;
  private static final long P2 = 2;
  private static final int P1_TYPE = 3;
  private static final int P2_TYPE = 5;

  private FreeCellsCollapser freeCellsCollapser = new FreeCellsCollapser();
  private Random rand = new Random();

  @Test
  public void shouldCollapse() {
    Cell[][] board = createBoard();
    Cell[][] expectedCollapsedBoard = createCollapsedBoard(board);

    Map<Long, List<Figure>> actual = freeCellsCollapser.getCollapsedFigures(board);

    assertThat(actual.get(EM).size()).isEqualTo(3);
    assertThat(actual.get(P1).size()).isEqualTo(9);
    assertThat(actual.get(P2).size()).isEqualTo(1);

    Figure p2Figure = actual.get(P2).iterator().next();
    assertThat(p2Figure.getPlayers().iterator().next()).isEqualTo(P2);

    // Part 2 test
    freeCellsCollapser.collapseBoard(board, actual);

    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        Cell actualCell = board[x][y];
        assertThat(actualCell).isEqualTo(expectedCollapsedBoard[x][y]);
      }
    }
  }

  /**
   * See: file 'board_for_collapsing_test.jpg' for image.
   *
   * Plan:
   * OP1: 9 figures;
   * OP2: 1 figure;
   * 3 figures between 2 players.
   */
  private Cell[][] createBoard() {
    Cell[][] cells = new Cell[CELLS_WIDTH][CELLS_HEIGHT];
    int x = -1;

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(EM, getRandT(), x,  5);
    cells[x][ 6] = new Cell(P1, P1_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    cells[x][10] = new Cell(P1, P1_TYPE, x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(P1, P1_TYPE, x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(EM, getRandT(), x,  1);
    cells[x][ 2] = new Cell(EM, getRandT(), x,  2);
    cells[x][ 3] = new Cell(EM, getRandT(), x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P1, P1_TYPE, x,  5);
    cells[x][ 6] = new Cell(P1, P1_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(P2, P2_TYPE, x,  8);
    cells[x][ 9] = new Cell(EM, getRandT(), x,  9);
    cells[x][10] = new Cell(EM, getRandT(), x, 10);
    cells[x][11] = new Cell(EM, getRandT(), x, 11);
    cells[x][12] = new Cell(EM, getRandT(), x, 12);
    cells[x][13] = new Cell(P1, P1_TYPE, x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(EM, getRandT(), x,  1);
    cells[x][ 2] = new Cell(EM, getRandT(), x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P1, P1_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P2, P2_TYPE, x,  7);
    cells[x][ 8] = new Cell(P2, P2_TYPE, x,  8);
    cells[x][ 9] = new Cell(P2, P2_TYPE, x,  9);
    cells[x][10] = new Cell(EM, getRandT(), x, 10);
    cells[x][11] = new Cell(EM, getRandT(), x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(P1, P1_TYPE, x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P2, P2_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P2, P2_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    cells[x][10] = new Cell(P1, P1_TYPE, x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(EM, getRandT(), x, 13);
    cells[x][14] = new Cell(EM, getRandT(), x, 14);
    cells[x][15] = new Cell(EM, getRandT(), x, 15);

    x++;
    cells[x][ 0] = new Cell(EM, getRandT(), x,  0);
    cells[x][ 1] = new Cell(EM, getRandT(), x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P1, P1_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(EM, getRandT(), x,  8);
    cells[x][ 9] = new Cell(EM, getRandT(), x,  9);
    cells[x][10] = new Cell(EM, getRandT(), x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(EM, getRandT(), x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P2, P2_TYPE, x,  4);
    cells[x][ 5] = new Cell(P2, P2_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    cells[x][10] = new Cell(P1, P1_TYPE, x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(EM, getRandT(), x, 12);
    cells[x][13] = new Cell(EM, getRandT(), x, 13);
    cells[x][14] = new Cell(EM, getRandT(), x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(EM, getRandT(), x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(EM, getRandT(), x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(EM, getRandT(), x,  9);
    cells[x][10] = new Cell(EM, getRandT(), x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(P1, P1_TYPE, x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(EM, getRandT(), x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P1, P1_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P1, P1_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    cells[x][10] = new Cell(P1, P1_TYPE, x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(EM, getRandT(), x, 13);
    cells[x][14] = new Cell(EM, getRandT(), x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(EM, getRandT(), x,  1);
    cells[x][ 2] = new Cell(EM, getRandT(), x,  2);
    cells[x][ 3] = new Cell(EM, getRandT(), x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P2, P2_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P2, P2_TYPE, x,  7);
    cells[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    cells[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    cells[x][10] = new Cell(P1, P1_TYPE, x, 10);
    cells[x][11] = new Cell(P1, P1_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(P1, P1_TYPE, x, 13);
    cells[x][14] = new Cell(P1, P1_TYPE, x, 14);
    cells[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(EM, getRandT(), x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(P1, P1_TYPE, x,  4);
    cells[x][ 5] = new Cell(P2, P2_TYPE, x,  5);
    cells[x][ 6] = new Cell(P2, P2_TYPE, x,  6);
    cells[x][ 7] = new Cell(P2, P2_TYPE, x,  7);
    cells[x][ 8] = new Cell(P2, P2_TYPE, x,  8);
    cells[x][ 9] = new Cell(P2, P2_TYPE, x,  9);
    cells[x][10] = new Cell(P2, P2_TYPE, x, 10);
    cells[x][11] = new Cell(P2, P2_TYPE, x, 11);
    cells[x][12] = new Cell(P1, P1_TYPE, x, 12);
    cells[x][13] = new Cell(P2, P2_TYPE, x, 13);
    cells[x][14] = new Cell(P2, P2_TYPE, x, 14);
    cells[x][15] = new Cell(P2, P2_TYPE, x, 15);

    x++;
    cells[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    cells[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    cells[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    cells[x][ 3] = new Cell(P1, P1_TYPE, x,  3);
    cells[x][ 4] = new Cell(EM, getRandT(), x,  4);
    cells[x][ 5] = new Cell(EM, getRandT(), x,  5);
    cells[x][ 6] = new Cell(EM, getRandT(), x,  6);
    cells[x][ 7] = new Cell(P2, P2_TYPE, x,  7);
    cells[x][ 8] = new Cell(P2, P2_TYPE, x,  8);
    cells[x][ 9] = new Cell(P2, P2_TYPE, x,  9);
    cells[x][10] = new Cell(P2, P2_TYPE, x, 10);
    cells[x][11] = new Cell(P2, P2_TYPE, x, 11);
    cells[x][12] = new Cell(P2, P2_TYPE, x, 12);
    cells[x][13] = new Cell(P2, P2_TYPE, x, 13);
    cells[x][14] = new Cell(EM, getRandT(), x, 14);
    cells[x][15] = new Cell(P2, P2_TYPE, x, 15);

    return cells;
  }

  private Cell[][] createCollapsedBoard(Cell[][] cells) {
    Cell[][] copiedBoard = new Cell[CELLS_WIDTH][CELLS_HEIGHT];

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        copiedBoard[x][y] = new Cell(cell.getOwner(), cell.getType(), cell.getX(), cell.getY());
      }
    }

    int x = -1;

    x++;
    copiedBoard[x][ 5] = new Cell(P1, P1_TYPE, x,  5);

    x++;
    copiedBoard[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    copiedBoard[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    copiedBoard[x][ 3] = new Cell(P1, P1_TYPE, x,  3);

    x++;
    copiedBoard[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    copiedBoard[x][ 2] = new Cell(P1, P1_TYPE, x,  2);

    x++;
    copiedBoard[x][13] = new Cell(P1, P1_TYPE, x, 13);
    copiedBoard[x][14] = new Cell(P1, P1_TYPE, x, 14);
    copiedBoard[x][15] = new Cell(P1, P1_TYPE, x, 15);

    x++;
    copiedBoard[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    copiedBoard[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    copiedBoard[x][ 8] = new Cell(P1, P1_TYPE, x,  8);
    copiedBoard[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    copiedBoard[x][10] = new Cell(P1, P1_TYPE, x, 10);
    copiedBoard[x][13] = new Cell(P1, P1_TYPE, x, 13);

    x++;
    copiedBoard[x][12] = new Cell(P1, P1_TYPE, x, 12);
    copiedBoard[x][13] = new Cell(P1, P1_TYPE, x, 13);
    copiedBoard[x][14] = new Cell(P1, P1_TYPE, x, 14);

    x++;
    copiedBoard[x][ 0] = new Cell(P1, P1_TYPE, x,  0);
    copiedBoard[x][ 9] = new Cell(P1, P1_TYPE, x,  9);
    copiedBoard[x][10] = new Cell(P1, P1_TYPE, x, 10);

    x++;
    copiedBoard[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    copiedBoard[x][13] = new Cell(P1, P1_TYPE, x, 13);
    copiedBoard[x][14] = new Cell(P1, P1_TYPE, x, 14);

    x++;
    copiedBoard[x][ 1] = new Cell(P1, P1_TYPE, x,  1);
    copiedBoard[x][ 2] = new Cell(P1, P1_TYPE, x,  2);
    copiedBoard[x][ 3] = new Cell(P1, P1_TYPE, x,  3);

    x++;
    copiedBoard[x][ 2] = new Cell(P1, P1_TYPE, x,  2);

    x++;
    copiedBoard[x][14] = new Cell(P2, P2_TYPE, x, 14);

    return copiedBoard;
  }

  private int getRandT() {
    return rand.nextInt(GameOptions.CELL_TYPES_COUNT) + 1;
  }

}