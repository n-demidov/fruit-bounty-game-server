package com.demidovn.fruitbounty.server.services.auth;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.server.AppConfigs;
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
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthExecutor implements Runnable {

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private ThirdPartyUserAuthenticator thirdPartyUserAuthenticator;

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

  @Setter
  private RequestOperation operation;

  public void run() {
    Connection connection = operation.getConnection();
    try {
      log.trace("process operation={}", operation);

      authAttemptsValidator.valid(connection);

      AuthOperation authOperation = conversionService.convert(operation, AuthOperation.class);
      ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo = thirdPartyUserAuthenticator.authenticate(authOperation);
      User authedUser = getOrCreateUser(thirdPartyAuthedUserInfo);

      connectionService.wasAuthed(connection, authedUser.getId());

      clientNotifier.sendSelfUserInfo(authedUser, connection);
      sendChatHistory(connection);
      sendCurrentGame(connection, authedUser);
      sendTopRated(connection);
    } catch (AuthFailedException | AuthValidationException e) {
      log.warn("process: auth failed", e);
      connectionService.killConnection(connection);
    } catch (RuntimeException e) {
      log.error("process", e);
      connectionService.killConnection(connection);
    }
  }

  private User getOrCreateUser(ThirdPartyAuthedUserInfo thirdPartyAuthedUserInfo) {
    List<User> thirdPartyUsers = userService.findByThirdPartyInfo(thirdPartyAuthedUserInfo);
    if (!thirdPartyUsers.isEmpty()) {
      User user = thirdPartyUsers.get(0);

      updateUserLastLogin(user);
      return user;
    }

    User authedUser = createUser(thirdPartyAuthedUserInfo);
    return userService.create(authedUser);
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

  private void updateUserLastLogin(User user) {
    user.setLastLogin(Instant.now().toEpochMilli());
    userService.update(user);
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
