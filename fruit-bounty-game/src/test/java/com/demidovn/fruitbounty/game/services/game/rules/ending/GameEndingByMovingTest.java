package com.demidovn.fruitbounty.game.services.game.rules.ending;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class GameEndingByMovingTest {

  private static final int DEFAULT_CELL_TYPE = 1;
  private static final int CELLS_WIDTH = 2, CELLS_HEIGHT = 2;
  private static final int FIRST_PLAYER_ID = 4, SECOND_PLAYER_ID = 5;
  private static final int EMPTY_CELL_PLAYER_ID = 0;

  private GameEndingByMoving gameEndingByMoving = new GameEndingByMoving();

  private Game game;
  private Player firstPlayer;
  private Player secondPlayer;

  @Before
  public void setup() {
    game = createGame();
  }

  @Test
  public void whenThereisEmptyCell() {
    game.getBoard().getCells()[0][1].setOwner(EMPTY_CELL_PLAYER_ID);

    gameEndingByMoving.checkGameEndingByMoving(game);

    assertFalse(game.isFinished());
    assertThat(game.getWinner()).isNull();
  }

  @Test
  public void whenSecondPlayerHasMoreCells() {
    gameEndingByMoving.checkGameEndingByMoving(game);

    assertTrue(game.isFinished());
    assertThat(game.getWinner()).isEqualTo(secondPlayer);
  }

  @Test
  public void whenPlayersHasEqualsCellsCount() {
    game.getBoard().getCells()[0][1].setOwner(FIRST_PLAYER_ID);

    gameEndingByMoving.checkGameEndingByMoving(game);

    assertTrue(game.isFinished());
    assertThat(game.getWinner()).isNull();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void when3Players() {
    game.getPlayers().add(
      createPlayer(99));

    gameEndingByMoving.checkGameEndingByMoving(game);
  }

  private Game createGame() {
    Game game = new Game();

    game.setPlayers(createPlayers());
    game.setBoard(createBoard());

    return game;
  }

  private List<Player> createPlayers() {
    firstPlayer = createPlayer(FIRST_PLAYER_ID);
    secondPlayer = createPlayer(SECOND_PLAYER_ID);
    return Arrays.asList(firstPlayer, secondPlayer);
  }

  private Player createPlayer(int playerId) {
    Player firstPlayer = new Player();
    firstPlayer.setId(playerId);
    return firstPlayer;
  }

  private Board createBoard() {
    Cell[][] cells = new Cell[CELLS_WIDTH][CELLS_HEIGHT];

    for (int x = 0; x < CELLS_WIDTH; x++) {
      for (int y = 0; y <CELLS_HEIGHT; y++) {
        Cell cell = new Cell(DEFAULT_CELL_TYPE, x, y);
        cell.setOwner(SECOND_PLAYER_ID);

        cells[x][y] = cell;
      }
    }

    cells[0][0].setOwner(FIRST_PLAYER_ID);

    return new Board(cells);
  }

}