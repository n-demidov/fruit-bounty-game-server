package com.demidovn.fruitbounty.game.services.game.bot.movefinder;

import com.demidovn.fruitbounty.game.model.GameProcessingContext;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.model.pathfind.BestMoveResult;
import com.demidovn.fruitbounty.game.services.game.GameLoop;
import com.demidovn.fruitbounty.game.services.game.GameRules;
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

public class DeepSearchMoveFinder {

  private static final int MAX_DEEP_LIMIT = Integer.MAX_VALUE;
  private static final GameRules gameRules = new GameRules();
  private static final GameLoop gameLoop = new GameLoop();

  public Pair<Integer, Integer> findBestMove(Player movingPlayer, Game game) {
    BestMoveResult bestBoard = findBestBoard(movingPlayer, game, 1);
    return new Pair<>(bestBoard.getMove().getX(), bestBoard.getMove().getY());
  }

  private BestMoveResult findBestBoard(Player movingPlayer, Game game, int deep) {
    if (game.isFinished()) {
      throw new IllegalStateException("Game finished, game=" + game);
    }

    List<Cell> allPossibleMoves = getAllPossibleMoves(game, movingPlayer.getId());  // O(n^2) - when find opponent's current cell type
    if (allPossibleMoves.isEmpty()) {
      throw new IllegalStateException("Can't find any possible move, game=" + game);
    }

    List<BestMoveResult> results = new ArrayList<>();
    for (Cell cell : allPossibleMoves) {  // O(k)
      Game newGame = Game.copy(game);  // O(n)

      GameAction gameAction = new GameAction(newGame, movingPlayer.getId(),
          GameActionType.Move, cell.getX(), cell.getY());
      gameLoop.processGameAction(gameAction, new GameProcessingContext(true));  // O(n * several times)

      if (!newGame.isFinished() && deep < MAX_DEEP_LIMIT) {
        Player nextPlayer = newGame.getCurrentPlayer();
        BestMoveResult bestBoard = findBestBoard(nextPlayer, newGame, deep + 1);
        bestBoard.setMove(cell); // ??
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
      if (count(bestMoveResult, movingPlayer) < count(results.get(i), movingPlayer)) {  // O(n * 2)
        bestMoveResult = results.get(i);
      }
    }

    return bestMoveResult;
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

  private int count(BestMoveResult bestMoveResult, Player movingPlayer) {
    int result = 0;
    Cell[][] cells = bestMoveResult.getResultedGame().getBoard().getCells();
    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[0].length; y++) {
        Cell cell = cells[x][y];
        if (cell.getOwner() == movingPlayer.getId()) {
          result++;
        }
      }
    }

    return result;
  }

}
