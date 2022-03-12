package com.demidovn.fruitbounty.game.services.game.generating;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class GameCreator {

  private final Random rand = new Random();

  private InitPlayersCellsConfigurator initPlayersCellsConfigurator =
    new InitPlayersCellsConfigurator();

  public Game createNewGame(List<Player> players, boolean isTutorial) {
    Game game = new Game();
    game.setTutorial(isTutorial);

    if (!game.isTutorial()) {
      randomlyMixPlayers(players);
    }
    game.setPlayers(players);
    setRandomCurrentPlayer(game);

    game.setBoard(createBoard(game));
    initPlayersCellsConfigurator.configureInitPlayersCells(game);
    changeBoardIfTutorial(game);

    return game;
  }

  private void randomlyMixPlayers(List<Player> players) {
    ArrayList<Player> copiedPlayersList = new ArrayList<>(players);
    players.clear();

    while (!copiedPlayersList.isEmpty()) {
      int randomIndex = rand.nextInt(copiedPlayersList.size());
      Player randomPlayer = copiedPlayersList.remove(randomIndex);

      players.add(randomPlayer);
    }
  }

  private void setRandomCurrentPlayer(Game game) {
    List<Player> players = game.getPlayers();
    int randomIndex = rand.nextInt(players.size());

    game.setCurrentPlayer(players.get(randomIndex));
  }

  private Board createBoard(Game game) {
    int boardWidth = getBoardWidth(game);
    int boardHeight = getBoardHeight(game);
    Cell[][] cells = new Cell[boardWidth][boardHeight];

    for (int x = 0; x < boardWidth; x++) {
      for (int y = 0; y < boardHeight; y++) {
        int randomCellType = rand.nextInt(GameOptions.CELL_TYPES_COUNT) + 1;
        cells[x][y] = new Cell(randomCellType, x, y);
      }
    }

    return new Board(cells);
  }

  private void changeBoardIfTutorial(Game game) {
    if (!game.isTutorial()) {
      return;
    }

    Cell[][] cells = game.getBoard().getCells();

    int type = cells[1][0].getType();
    cells[1][1].setType(type);

    type = cells[2][1].getType();
    cells[3][1].setType(type);
    cells[2][2].setType(type);
    cells[2][0].setType(type);

    type = cells[3][2].getType();
    cells[4][2].setType(type);
    cells[5][2].setType(type);
    cells[6][2].setType(type);

    type = cells[3][3].getType();
    cells[3][4].setType(type);
    cells[3][5].setType(type);
  }

  private int getBoardWidth(Game game) {
    if (game.isTutorial()) {
      return GameOptions.TUTORIAL_BOARD_WIDTH;
    } else {
      return GameOptions.BOARD_WIDTH;
    }
  }

  private int getBoardHeight(Game game) {
    if (game.isTutorial()) {
      return GameOptions.TUTORIAL_BOARD_HEIGHT;
    } else {
      return GameOptions.BOARD_HEIGHT;
    }
  }

}
