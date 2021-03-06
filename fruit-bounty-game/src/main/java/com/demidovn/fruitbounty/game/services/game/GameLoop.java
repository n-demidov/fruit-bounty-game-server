package com.demidovn.fruitbounty.game.services.game;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.model.GameProcessingContext;
import com.demidovn.fruitbounty.game.services.DefaultGameEventsSubscriptions;
import com.demidovn.fruitbounty.game.services.FruitBountyGameFacade;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.GameActionType;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameLoop {

  private static final int MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING = 20;

  @Autowired
  private FruitBountyGameFacade gameFacade;

  @Autowired
  private DefaultGameEventsSubscriptions gameEventsSubscriptions;

  @Autowired
  private GameRules gameRules;

  @Autowired
  private BotService botService;

  @Scheduled(fixedDelayString = GameOptions.GAME_LOOP_SCHEDULE_DELAY)
  public void gameLoop() {
    List<Game> finishedGames = new ArrayList<>();

    for (Game game : gameFacade.getAllGames()) {
      processGame(game);

      if (game.isFinished()) {
        finishedGames.add(game);
      }
    }

    gameFacade.gamesFinished(finishedGames);
  }

  private void processGame(Game game) {
    GameProcessingContext processContext = new GameProcessingContext();

    int i = 0;
    GameAction gameAction;
    while ((gameAction = game.getGameActions().poll()) != null) {
      processGameAction(gameAction, processContext);

      i++;
      if (i == MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING) {
        log.warn("%d iteration of processing game-actions, game=%s", i, game);
      }
    }

    if (i > MIN_GAME_ACTIONS_IN_QUEUE_TO_WARNING) {
      log.warn("%d game-actions have been processed, game=%s", i, game);
    }

    checkForCurrentMoveExpiration(game, processContext);
    notifyIfGameChanged(game, processContext);
    botService.actionIfBot(game);
  }

  private void processGameAction(GameAction gameAction, GameProcessingContext context) {
    if (gameAction.getGame().isFinished()) {
      return;
    }

    if (gameAction.getType() == GameActionType.Move) {
      processMoveAction(gameAction, context);
    } else if (gameAction.getType() == GameActionType.Surrender) {
      processSurrenderAction(gameAction, context);
    } else {
      throw new IllegalArgumentException(String.format(
        "Unknown gameActionType=%s", gameAction.getType()));
    }
  }

  private void processMoveAction(GameAction gameAction, GameProcessingContext context) {
    if (gameRules.isMoveValid(gameAction)) {
      gameAction.getGame().getCurrentPlayer().resetConsecutivelyMissedMoves();

      List<Cell> capturableCells = gameRules.findCapturableCells(gameAction);

      gameRules.captureCells(capturableCells, gameAction);
      gameRules.checkGameEndingByMoving(gameAction.getGame());
      gameRules.switchCurrentPlayer(gameAction.getGame());

      context.markGameChanged();
    }
  }

  private void processSurrenderAction(GameAction gameAction, GameProcessingContext context) {
    playerSurrendered(gameAction.findActionedPlayer(), gameAction.getGame());

    context.markGameChanged();
  }

  private void playerSurrendered(Player player, Game game) {
    player.setSurrendered(true);

    if (player.equals(game.getCurrentPlayer())) {
      gameRules.switchCurrentPlayer(game);
    }

    gameRules.checkGameEndingBySurrendering(game);
  }

  private void checkForCurrentMoveExpiration(Game game, GameProcessingContext processContext) {
    if (game.isFinished()) {
      return;
    }

    if (isCurrentMoveExpired(game)) {
      Player currentPlayer = game.getCurrentPlayer();

      currentPlayer.incrementMissedMoves();

      if (currentPlayer.getConsecutivelyMissedMoves() > GameOptions.MAX_GAME_MISSED_MOVES) {
        playerSurrendered(currentPlayer, game);
      } else {
        gameRules.switchCurrentPlayer(game);
      }

      processContext.markGameChanged();
    }
  }

  private boolean isCurrentMoveExpired(Game game) {
    return
      game.getCurrentMoveStarted() + GameOptions.TIME_PER_MOVE < Instant.now().toEpochMilli();
  }

  private void notifyIfGameChanged(Game game, GameProcessingContext processContext) {
    if (processContext.isGameChanged()) {
      gameEventsSubscriptions.notifyGameChanged(game);
    }
  }

}
