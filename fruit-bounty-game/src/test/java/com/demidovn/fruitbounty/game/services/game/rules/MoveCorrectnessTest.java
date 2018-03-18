package com.demidovn.fruitbounty.game.services.game.rules;

import static org.junit.Assert.*;

import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.game.GameRules;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MoveCorrectnessTest extends AbstractGameRulesTest {

  private static final int OWNED_CELL_X = 2;
  private static final int OWNED_CELL_Y = 1;

  private final List<Pair<Integer, Integer>> neighborCells = Arrays.asList(
    new Pair<>(OWNED_CELL_X - 1, OWNED_CELL_Y),
    new Pair<>(OWNED_CELL_X + 1, OWNED_CELL_Y),
    new Pair<>(OWNED_CELL_X, OWNED_CELL_Y - 1),
    new Pair<>(OWNED_CELL_X, OWNED_CELL_Y + 1));
  private final Pair<Integer, Integer> capturingNeighborCell = neighborCells.get(0);

  private GameRules gameRules = new GameRules();

  private GameAction gameAction;

  @Before
  public void before() {
    gameAction = new GameAction();
    gameAction.setGame(generateGame());

    setCurrentPlayer();
  }

  @Test
  public void approve() {
    gameAction.findCell(4, 2).setType(100);

    neighborCells.forEach(closeCell -> {
      gameAction.setX(closeCell.getKey());
      gameAction.setY(closeCell.getValue());

      boolean actual = gameRules.isMoveValid(gameAction);

      assertTrue(actual);
    });
  }

  @Test
  public void rejectIfObliquely() {
    List<Pair<Integer, Integer>> obliquelyCells = new ArrayList<>();
    obliquelyCells.add(new Pair<>(OWNED_CELL_X - 1, OWNED_CELL_Y - 1));
    obliquelyCells.add(new Pair<>(OWNED_CELL_X - 1, OWNED_CELL_Y + 1));
    obliquelyCells.add(new Pair<>(OWNED_CELL_X + 1, OWNED_CELL_Y - 1));
    obliquelyCells.add(new Pair<>(OWNED_CELL_X + 1, OWNED_CELL_Y + 1));

    obliquelyCells.forEach(obliquelyCell -> {
      gameAction.setX(obliquelyCell.getKey());
      gameAction.setY(obliquelyCell.getValue());

      boolean actual = gameRules.isMoveValid(gameAction);

      assertFalse(actual);
    });
  }

  @Test
  public void rejectIfNoNeighborOwnedCell() {
    gameAction.setX(0);
    gameAction.setY(0);

    boolean actual = gameRules.isMoveValid(gameAction);

    assertFalse(actual);
  }

  @Test
  public void rejectIfAlreadyOccupied() {
    gameAction.setX(2);
    gameAction.setY(1);

    boolean actual = gameRules.isMoveValid(gameAction);

    assertFalse(actual);
  }

  @Test
  public void rejectIfAlreadyOwnedByOtherPlayer() {
    int otherPlayerCellX = 3;
    int otherPlayerCellY = 1;

    setOtherPlayerCell(otherPlayerCellX, otherPlayerCellY);

    boolean actual = gameRules.isMoveValid(gameAction);

    assertFalse(actual);
  }

  @Test
  public void rejectIfPlayerNotCurrent() {
    gameAction.setX(capturingNeighborCell.getKey());
    gameAction.setY(capturingNeighborCell.getValue());

    gameAction.getGame().setCurrentPlayer(getOtherPlayer());

    boolean actual = gameRules.isMoveValid(gameAction);

    assertFalse(actual);
  }

  @Test
  public void rejectIfOpponentAlreadyOccupiedSuchType() {
    int opponentCellX = 0, opponentCellY = 2;
    Cell opponentCell = gameAction.findCell(opponentCellX, opponentCellY);

    opponentCell.setOwner(getOtherPlayer().getId());

    gameAction.setX(capturingNeighborCell.getKey());
    gameAction.setY(capturingNeighborCell.getValue());

    boolean actual = gameRules.isMoveValid(gameAction);

    assertFalse(actual);
  }

  @Override
  protected List<Player> generatePlayers() {
    List<Player> players = new ArrayList<>();

    Player firstPlayer = new Player();
    Player secondPlayer = new Player();

    firstPlayer.setId(PLAYER_ID);
    secondPlayer.setId(OTHER_PLAYER_ID);

    players.add(firstPlayer);
    players.add(secondPlayer);

    return players;
  }

  private void setCurrentPlayer() {
    gameAction.setActionedPlayerId(PLAYER_ID);
    gameAction.getGame().setCurrentPlayer(getPlayer());
  }

  private void setOtherPlayerCell(int otherPlayerCellX, int otherPlayerCellY) {
    gameAction.setX(otherPlayerCellX);
    gameAction.setY(otherPlayerCellY);

    Cell[][] cells = gameAction.getGame().getBoard().getCells();
    cells[otherPlayerCellX][otherPlayerCellY].setOwner(OTHER_PLAYER_ID);
  }

  private Player getPlayer() {
    return gameAction.getGame().getPlayers().get(0);
  }

  private Player getOtherPlayer() {
    return gameAction.getGame().getPlayers().get(1);
  }

}