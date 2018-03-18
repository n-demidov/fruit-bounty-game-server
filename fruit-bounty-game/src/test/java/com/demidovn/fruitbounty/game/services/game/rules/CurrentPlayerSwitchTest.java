package com.demidovn.fruitbounty.game.services.game.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class CurrentPlayerSwitchTest {

  private static final int CELLS_WIDTH = 1;
  private static final int CELLS_HEIGHT = 2;
  private static final int DEFAULT_CELL_TYPE = 1;
  private static final int FIRST_PLAYER_ID = 10;
  private static final int SECOND_PLAYER_ID = 11;

  private CurrentPlayerSwitch currentPlayerSwitch = new CurrentPlayerSwitch();

  private Game game;

  @Before
  public void setupGame() {
    game = new Game();

    game.setBoard(createBoard());
    game.setPlayers(createPlayers());
    game.setCurrentPlayer(game.getPlayers().get(0));
  }

  @Test(expected = IllegalStateException.class)
  public void exception_whenNobodyHasFeasibleMove() {
    currentPlayerSwitch.switchCurrentPlayer(game);
  }

  @Test
  public void notSwitchPlayer_whenGameFinished() {
    game.setFinished(true);

    currentPlayerSwitch.switchCurrentPlayer(game);

    assertThat(game.getCurrentPlayer().getId()).isEqualTo(FIRST_PLAYER_ID);
  }

  /* Plan for game Board:
      ___
     |5f|
     |1s|
   */
  private Board createBoard() {
    Cell[][] cells = new Cell[CELLS_WIDTH][CELLS_HEIGHT];

    Cell firstCell = new Cell(DEFAULT_CELL_TYPE, 0, 0);
    Cell secondCell = new Cell(DEFAULT_CELL_TYPE, 0, 1);

    firstCell.setOwner(FIRST_PLAYER_ID);
    secondCell.setOwner(SECOND_PLAYER_ID);

    cells[0][0] = firstCell;
    cells[0][1] = secondCell;

    return new Board(cells);
  }

  private List<Player> createPlayers() {
    Player firstPlayer = new Player();
    Player secondPlayer = new Player();

    firstPlayer.setId(FIRST_PLAYER_ID);
    secondPlayer.setId(SECOND_PLAYER_ID);

    return Arrays.asList(firstPlayer, secondPlayer);
  }

}