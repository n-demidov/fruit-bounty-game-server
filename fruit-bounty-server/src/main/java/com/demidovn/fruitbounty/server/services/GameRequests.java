package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.server.AppConstants;
import com.demidovn.fruitbounty.server.MetricsConsts;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.game.GameNotifier;
import com.demidovn.fruitbounty.server.services.game.UserGames;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.demidovn.fruitbounty.server.services.metrics.StatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameRequests {

  private static final int INIT_ITERATIONS_VALUE = 0;

  @Autowired
  private UserGames userGames;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private GameNotifier gameNotifier;

  @Autowired
  private UserService userService;

  @Autowired
  private GameRequestsWaitCalculator gameRequestsWaitCalculator;

  @Autowired
  private StatService statService;

  private final Random random = new Random();
  private final Map<Long, Integer> gameRequests = new ConcurrentHashMap<>();

  public void userSubmitRequest(long userId) {
    if (userGames.isUserPlaying(userId)) {
      return;
    }

    gameRequests.put(userId, INIT_ITERATIONS_VALUE);
  }

  public void userCancelRequest(long userId) {
    gameRequests.remove(userId);
  }

  public void processGameRequests() {
    List<Long> userIds = new ArrayList<>(gameRequests.keySet());
    log.trace("Starting of processGameRequests, gameRequests={}", userIds.size());

    clearNotActualRequests(userIds);

    while (userIds.size() >= 2) {
      long firstUser = extractRandomUser(userIds);
      long secondUser = extractRandomUser(userIds);
      List<Long> players = Arrays.asList(firstUser, secondUser);

      log.debug("Creating game between 2 users: {} and {}", firstUser, secondUser);

      Game game = userGames.startGame(players);
      gameNotifier.notifyThatGameStarted(game);

      statService.incCounter(MetricsConsts.GAME.ALL_STAT);
      statService.incCounter(MetricsConsts.GAME.BETWEEN_PLAYERS_STAT);
    }

    if (userIds.size() == 1) {
      processRemainingUser(userIds.get(0));
    }
  }

  private void clearNotActualRequests(List<Long> userIds) {
    Iterator<Long> users = userIds.iterator();
    while (users.hasNext()) {
      Long userId = users.next();
      User user = userService.get(userId);

      if (userGames.isUserPlaying(userId) || !connectionService.isUserOnline(userId)) {
        users.remove();
        gameRequests.remove(userId);
      } else if (isTutorialGame(user)) {
        log.debug("Creating tutorial game for: {}", userId);
        Game game = userGames.startTutorialGame(userId);
        gameNotifier.notifyThatGameStarted(game);

        users.remove();
        gameRequests.remove(userId);

        statService.incCounter(MetricsConsts.GAME.ALL_STAT);
        statService.incCounter(MetricsConsts.GAME.TUTORIAL_STAT);
      }
    }
  }

  private boolean isTutorialGame(User user) {
    return user.getWins() <= AppConstants.MAX_WINS_FOR_TUTORIAL_GAME;
  }

  private long extractRandomUser(List<Long> userIds) {
    int randomIndex = random.nextInt(userIds.size());
    Long pulledUserId = userIds.get(randomIndex);

    userIds.remove(randomIndex);
    gameRequests.remove(pulledUserId);

    return pulledUserId;
  }

  private void processRemainingUser(Long remainingUserId) {
    User user = userService.get(remainingUserId);
    int iterationsCount = incrementPassedIterationsCount(remainingUserId);

    int waitUntilBot = gameRequestsWaitCalculator.getWaitUntilBot(user.getScore());
    if (iterationsCount > waitUntilBot) {
      log.debug("Creating game between user (id={}) and bot", remainingUserId);
      gameRequests.remove(remainingUserId);

      Game game = userGames.startGameWithBot(remainingUserId);
      gameNotifier.notifyThatGameStarted(game);

      statService.incCounter(MetricsConsts.GAME.ALL_STAT);
      statService.incCounter(MetricsConsts.GAME.WITH_BOT_STAT);
    }
  }

  private int incrementPassedIterationsCount(Long remainingUserId) {
    int newIterationsCount = gameRequests.get(remainingUserId) + 1;
    gameRequests.put(remainingUserId, newIterationsCount);

    return newIterationsCount;
  }

}
