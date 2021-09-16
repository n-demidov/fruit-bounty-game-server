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

    randomlyMixPlayers(players);
    game.setPlayers(players);
    setRandomCurrentPlayer(game);

    game.setTutorial(isTutorial);

    game.setBoard(createBoard(game));
    initPlayersCellsConfigurator.configureInitPlayersCells(game);

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
