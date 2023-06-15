package com.demidovn.fruitbounty.game.services.game.bot.movefinder;

import com.demidovn.fruitbounty.game.model.GameProcessingContext;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.model.pathfind.BestMoveResult;
import com.demidovn.fruitbounty.game.services.game.GameLoop;
import com.demidovn.fruitbounty.game.services.game.GameRules;
import com.demidovn.fruitbounty.game.services.game.bot.boardrate.BoardRater;
import com.demidovn.fruitbounty.game.services.game.bot.boardrate.MovementToCenterBoardRater;
import com.demidovn.fruitbounty.game.services.game.bot.boardrate.UniformMovementBoardRater;
import com.demidovn.fruitbounty.game.services.game.rules.CellsFinder;
import com.demidovn.fruitbounty.game.services.game.rules.PlayersFinder;
import com.demidovn.fruitbounty.gameapi.model.Board;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level2ThresholdDeepMoveFinder {

  private static final int FULL_DEEP_SEARCH_THRESHOLD = 14;
  private static final double SECOND_ALG_MULTIPLIER_THRESHOLD = 1.75;

  private static final GameRules gameRules = new GameRules();
  private static final GameLoop gameLoop = new GameLoop();
  private static final CellsFinder cellsFinder = new CellsFinder();
  private static final PlayersFinder playersFinder = new PlayersFinder();
  private static final MovementToCenterBoardRater movementToCenterBoardRater = new MovementToCenterBoardRater();
  private static final UniformMovementBoardRater uniformMovementBoardRater = new UniformMovementBoardRater();

  /**
   * @param maxDeepLimit: 0: 0 move (in deep); 2: 1 move (in deep); 4: 2 moves (in deep); 6: 3 moves (in deep)
   */
  public Pair<Integer, Integer> findBestMove(Player movingPlayer, Game game, int maxDeepLimit) {
    boolean timeToSecondAlgo;
    BoardRater boardRater;

    if (cellsFinder.countCells(game.getBoard().getCells(), 0) <= FULL_DEEP_SEARCH_THRESHOLD) {
      timeToSecondAlgo = true;
    } else {
      timeToSecondAlgo = isTimeToSecondAlgo(game);
    }

    if (timeToSecondAlgo) {
      boardRater = uniformMovementBoardRater;
    } else {
      boardRater = movementToCenterBoardRater;
    }

    BestMoveResult bestBoard = findBestBoard(movingPlayer, game, 1, timeToSecondAlgo, boardRater, maxDeepLimit);

    return new Pair<>(bestBoard.getMove().getX(), bestBoard.getMove().getY());
  }

  private BestMoveResult findBestBoard(Player movingPlayer, Game game, int deep,
      boolean timeToSecondAlgo, BoardRater boardRater, int maxDeepLimit) {
    if (game.isFinished()) {
      throw new IllegalStateException("Game finished, game=" + game);
    }

    List<Cell> allPossibleMoves = getAllPossibleMoves(game, movingPlayer.getId());  // O(n)
    if (allPossibleMoves.isEmpty()) {
      throw new IllegalStateException("Can't find any possible move, game=" + game);
    }

    List<BestMoveResult> results = new ArrayList<>();
    for (Cell cell : allPossibleMoves) {  // O(k)
      Game newGame = Game.copy(game);  // O(n)

      GameAction gameAction = new GameAction(newGame, movingPlayer.getId(),
          GameActionType.Move, cell.getX(), cell.getY());
      GameProcessingContext gameContext = timeToSecondAlgo
          ? new GameProcessingContext(true) : new GameProcessingContext();
      gameLoop.processGameAction(gameAction, gameContext);  // O(n * several times)

      if (!newGame.isFinished() && deep <= maxDeepLimit) {
        Player nextPlayer = newGame.getCurrentPlayer();
        BestMoveResult bestBoard = findBestBoard(nextPlayer, newGame, deep + 1, timeToSecondAlgo, boardRater, maxDeepLimit);
        bestBoard.setMove(cell);
        results.add(bestBoard);
      } else {
        BestMoveResult bestBoard = new BestMoveResult();
        bestBoard.setMove(cell);
        bestBoard.setResultedGame(newGame);
        results.add(bestBoard);
      }
    }

    BestMoveResult bestMoveResult = results.get(0);
    for (int i = 1; i < results.size(); i++) {  // O(k)
      Player nextPlayer = playersFinder.getNextPlayer(bestMoveResult.getResultedGame(), movingPlayer.getId());
      if (boardRater.rateBoard(bestMoveResult.getResultedGame().getBoard().getCells(), movingPlayer, nextPlayer)     // O(n * 2)
          <
          boardRater.rateBoard(results.get(i).getResultedGame().getBoard().getCells(), movingPlayer, nextPlayer)) {  // O(n * 2)
        bestMoveResult = results.get(i);
      }
    }

    return bestMoveResult;
  }

  private boolean isTimeToSecondAlgo(Game game) {
    int cellsNum = game.getBoard().getCells().length * game.getBoard().getCells()[0].length;
    int thresholdToSecondAlg = (int) (cellsNum / SECOND_ALG_MULTIPLIER_THRESHOLD);
    return cellsFinder.countCells(game.getBoard().getCells(), 0) <= thresholdToSecondAlg;
  }

  private List<Cell> getAllPossibleMoves(Game game, long actionedPlayerId) {
    Map<Integer, Cell> result = new HashMap<>();

    Board board = game.getBoard();
    Cell[][] cells = board.getCells();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        GameAction gameAction = new GameAction(
            game,
            actionedPlayerId,
            GameActionType.Move,
            x,
            y);
        if (gameRules.isMoveValid(gameAction)) {
          Cell cell = cells[x][y];
          result.put(cell.getType(), cell);
        }
      }
    }

    return new ArrayList<>(result.values());
  }

}
