package com.demidovn.fruitbounty.server.services.auth;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.game.bot.BotNameGenerator;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.MetricsConsts;
import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.dto.operations.response.UserInfo;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthFailedException;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthValidationException;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.ClientNotifier;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.PlayersRating;
import com.demidovn.fruitbounty.server.services.game.GameNotifier;
import com.demidovn.fruitbounty.server.services.game.UserGames;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.chat.ChatHub;
import com.demidovn.fruitbounty.server.services.auth.authenticator.ThirdPartyUserAuthenticator;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.demidovn.fruitbounty.server.services.metrics.StatService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthExecutor implements Runnable {

  private static final Map<AuthType, String> authStatsByAuthType = new HashMap<>();

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private ConnectionService connectionService;

  @Resource(name = "thirdPartyUserAuthenticators")
  private Map<AuthType, ThirdPartyUserAuthenticator> thirdPartyUserAuthenticators;

  @Autowired
  private UserService userService;

  @Autowired
  private ChatHub chatHub;

  @Autowired
  private AuthAttemptsValidator authAttemptsValidator;

  @Autowired
  private UserGames userGames;

  @Autowired
  private GameNotifier gameNotifier;

  @Autowired
  private ClientNotifier clientNotifier;

  @Autowired
  private PlayersRating playersRating;

  @Autowired
  private BotNameGenerator nameGenerator;

  @Autowired
  private StatService statService;

  @Setter
  private RequestOperation operation;

  static {
    for (AuthType authType : AuthType.values()) {
      authStatsByAuthType.put(authType, MetricsConsts.AUTH.SUCCESS_BY_TYPE_STAT + authType);
    }
  }

  public void run() {
    Connection connection = operation.getConnection();
    try {
      log.trace("process operation={}", operation);

      authAttemptsValidator.valid(connection);

      AuthOperation authOperation = conversionService.convert(operation, AuthOperation.class);
      statService.incCounter(MetricsConsts.AUTH.ALL_TRIES_STAT);
      statService.incCounter(MetricsConsts.AUTH.DEVICE_STAT + authOperation.getDevice());

      AuthType authType = authOperation.getType();
      ThirdPartyUserAuthenticator thirdPartyUserAuthenticator =
              thirdPartyUserAuthenticators.get(authType);
      ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo = thirdPartyUserAuthenticator.authenticate(authOperation);
      User authedUser = getOrCreateUser(thirdPartyAuthedUserInfo);

      connectionService.wasAuthed(connection, authedUser.getId());

      clientNotifier.sendSelfUserInfo(authedUser, connection);
      sendChatHistory(connection);
      sendCurrentGame(connection, authedUser);
      sendTopRated(connection);

      statService.incCounter(MetricsConsts.AUTH.SUCCESS_ALL_STAT);
      statService.incCounter(authStatsByAuthType.get(authType));
    } catch (AuthFailedException | AuthValidationException e) {
      log.warn("process: auth failed", e);
      connectionService.killConnection(connection);
      statService.incCounter(MetricsConsts.AUTH.ERRORS_STAT);
    } catch (RuntimeException e) {
      log.error("process", e);
      connectionService.killConnection(connection);
      statService.incCounter(MetricsConsts.AUTH.ERRORS_STAT);
    }
  }

  private User getOrCreateUser(ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo) {
    List<User> thirdPartyUsers = userService.findByThirdPartyInfo(thirdPartyAuthedUserInfo);
    if (!thirdPartyUsers.isEmpty()) {
      User user = thirdPartyUsers.get(0);

      // Refresh Name and avatar.
      if (thirdPartyAuthedUserInfo.getImg() != null && !Objects.equals(thirdPartyAuthedUserInfo.getImg(), user.getImg())) {
        user.setImg(thirdPartyAuthedUserInfo.getImg());
      }
      if (thirdPartyAuthedUserInfo.getPublicName() != null && !Objects.equals(thirdPartyAuthedUserInfo.getPublicName(), user.getPublicName())) {
        user.setPublicName(thirdPartyAuthedUserInfo.getPublicName());
      }
      user.setLastLogin(Instant.now().toEpochMilli());

      userService.update(user);
      return user;
    }

    setRandomParamsIfNecessary(thirdPartyAuthedUserInfo);
    User authedUser = createUser(thirdPartyAuthedUserInfo);
    return userService.create(authedUser);
  }

  private void setRandomParamsIfNecessary(ThirdPartyAuthedUserInfo userInfo) {
    if (userInfo.getImg() == null) {
      userInfo.setImg(GameOptions.UNKNOWN_PERSON_IMG);
    }
    if (userInfo.getPublicName() == null) {
      userInfo.setPublicName(nameGenerator.getRandomName());
    }
  }

  private User createUser(ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo) {
    User authedUser = new User();

    authedUser.setThirdPartyType(thirdPartyAuthedUserInfo.getThirdPartyType());
    authedUser.setThirdPartyId(thirdPartyAuthedUserInfo.getThirdPartyId());
    authedUser.setPublicName(thirdPartyAuthedUserInfo.getPublicName());
    authedUser.setImg(thirdPartyAuthedUserInfo.getImg());

    authedUser.setScore(AppConfigs.INITIAL_USER_SCORE);

    return authedUser;
  }

  private void sendChatHistory(Connection connection) {
    List<String> messages = chatHub.get();

    ResponseOperation responseOperation = new ResponseOperation(
        ResponseOperationType.SendChat,
        messages);

    connectionService.send(connection, responseOperation);
  }

  private void sendCurrentGame(Connection connection, User authedUser) {
    Optional<Game> currentGame = userGames.getCurrentGame(authedUser.getId());

    if (currentGame.isPresent()) {
      ResponseOperation gameNotification = gameNotifier.createGameNotification(
        currentGame.get(), ResponseOperationType.GameStarted);

      connectionService.sendWithConversion(connection, gameNotification);
    }
  }

  private void sendTopRated(Connection connection) {
    List<UserInfo> topRatedPlayers = playersRating.getTopRatedPlayers();

    connectionService.send(connection, topRatedPlayers, ResponseOperationType.RatingTable);
  }

}
