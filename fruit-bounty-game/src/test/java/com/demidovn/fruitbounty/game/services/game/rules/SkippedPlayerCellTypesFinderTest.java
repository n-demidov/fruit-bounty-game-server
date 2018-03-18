package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class SkippedPlayerCellTypesFinderTest {

  private static final int BOARD_WIDTH = 2;
  private static final int BOARD_HEIGHT = 2;

  private static final int DEFAULT_CELL_TYPE = 2;
  private static final int SECOND_PLAYER_CELL_TYPE = 1;
  private static final int FREE_CELL_TYPE = 5;

  private static final int FIRST_PLAYER_ID = 1;
  private static final int SECOND_PLAYER_ID = 2;

  private SkippedPlayerCellTypesFinder skippedPlayerCellTypesFinder =
    new SkippedPlayerCellTypesFinder();

  private Cell[][] cells;
  private Game game;

  @Before
  public void setupGame() {
    createDefaultBoard();
    setOwnersToCells();
    setCustomTypesToCells();

    game = new Game();
    game.setBoard(new Board(cells));
    game.setPlayers(createPlayers());
  }

  @Test
  public void whenOpponentHasOnlyOnePossibleMove() {
    List<Integer> possibleCellTypes = skippedPlayerCellTypesFinder
      .findPossibleCellTypes(game, FIRST_PLAYER_ID);

    assertThat(possibleCellTypes.size()).isEqualTo(GameOptions.CELL_TYPES_COUNT - 3);
    assertThat(possibleCellTypes).doesNotContain(1, 2, 5);
  }

  @Test
  public void whenOpponentHasTwoPossibleMoves() {
    freeAnotherElseCell();

    List<Integer> possibleCellTypes = skippedPlayerCellTypesFinder
      .findPossibleCellTypes(game, FIRST_PLAYER_ID);

    assertThat(possibleCellTypes.size()).isEqualTo(GameOptions.CELL_TYPES_COUNT - 2);
    assertThat(possibleCellTypes).doesNotContain(1, 2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void whenThereAreMoreThenTwoPlayers() {
    game.getPlayers().add(new Player());

    skippedPlayerCellTypesFinder.findPossibleCellTypes(game, FIRST_PLAYER_ID);
  }

  private void freeAnotherElseCell() {
    int otherFreeCellType = FREE_CELL_TYPE + 1;
    Cell cell = game.getBoard().getCells()[1][1];
    cell.setType(otherFreeCellType);
    cell.setOwner(0);
  }

  /* Plan for game Board:
      ______
     |5 |2p|
     |1o|2p|
   */
  private void createDefaultBoard() {
    cells = new Cell[BOARD_WIDTH][BOARD_HEIGHT];

    for (int x = 0; x < BOARD_WIDTH; x++) {
      for (int y = 0; y < BOARD_HEIGHT; y++) {
        cells[x][y] = new Cell(DEFAULT_CELL_TYPE, x, y);
      }
    }

    cells[0][1].setType(SECOND_PLAYER_CELL_TYPE);
  }

  private void setOwnersToCells() {
    cells[0][1].setOwner(SECOND_PLAYER_ID);

    cells[1][0].setOwner(FIRST_PLAYER_ID);
    cells[1][1].setOwner(FIRST_PLAYER_ID);
  }

  private void setCustomTypesToCells() {
    cells[0][0].setType(FREE_CELL_TYPE);
  }

  private List<Player> createPlayers() {
    Player firstPlayer = new Player();
    Player secondPlayer = new Player();

    firstPlayer.setId(FIRST_PLAYER_ID);
    secondPlayer.setId(SECOND_PLAYER_ID);

    return Arrays.asList(firstPlayer, secondPlayer);
  }

}