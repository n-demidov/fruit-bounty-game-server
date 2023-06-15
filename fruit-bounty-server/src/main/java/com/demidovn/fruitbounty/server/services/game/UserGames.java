package com.demidovn.fruitbounty.server.services.game;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import com.demidovn.fruitbounty.gameapi.services.GameFacade;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserGames {

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private GameFacade gameFacade;

  @Autowired
  private BotService botService;

  private final Map<Long, Game> userGames = new ConcurrentHashMap<>();

  public Game startGame(List<Long> userIds) {
    List<Player> players = convert2Players(userIds);

    return createGame(userIds, players, false);
  }

  public Game startGameWithBot(long userId) {
    Player userPlayer = conversionService.convert(userId, Player.class);
    Player botPlayer = botService.createNewBot(userPlayer.getScore());

    List<Player> players = new ArrayList<>(Arrays.asList(userPlayer, botPlayer));

    return createGame(Collections.singletonList(userId), players, false);
  }

  public Game startTutorialGame(Long userId) {
    Player userPlayer = conversionService.convert(userId, Player.class);
    Player botPlayer = botService.createTrainer();

    List<Player> players = new ArrayList<>(Arrays.asList(userPlayer, botPlayer));

    Game game = createGame(Collections.singletonList(userId), players, true);
    game.setTimePerMoveMs(GameOptions.TUTORIAL_TIME_PER_MOVE_MS);
    game.setTutorial(true);

    return game;
  }

  public void processGameAction(GameAction gameAction) {
    gameFacade.processGameAction(gameAction);
  }

  public boolean isUserPlaying(long userId) {
    return userGames.containsKey(userId);
  }

  public Optional<Game> getCurrentGame(long userId) {
    return Optional.ofNullable(userGames.get(userId));
  }

  public void gameFinished(Game game) {
    for (Player player : game.getPlayers()) {
      userGames.remove(player.getId());
      log.trace("Game for user {} was removed; current userGames={}", player.getId(), userGames.size());
    }
  }

  public int countPlayingUsers() {
    return userGames.size();
  }

  private List<Player> convert2Players(List<Long> userIds) {
    return userIds.stream()
        .map(userId -> conversionService.convert(userId, Player.class))
        .collect(Collectors.toList());
  }

  private Game createGame(List<Long> userIds, List<Player> players, boolean isTutorial) {
    Game createdGame = gameFacade.startGame(players, isTutorial);
    updateUsersGame(userIds, createdGame);
    createdGame.setTimePerMoveMs(GameOptions.TIME_PER_MOVE_MS);
    return createdGame;
  }

  private void updateUsersGame(List<Long> userIds, Game createdGame) {
    userIds
      .forEach(userId -> userGames.put(userId, createdGame));
  }

}
