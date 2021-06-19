package com.demidovn.fruitbounty.server.services.game;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameNotifier {

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private BotService botService;

  public void notifyThatGameStarted(Game game) {
    ResponseOperation gameNotification =
      createGameNotification(game, ResponseOperationType.GameStarted);

    for (Player player : game.getPlayers()) {
      sendToClient(player, gameNotification);
    }
  }

  public ResponseOperation createGameChangedNotification(Game game) {
    return createGameNotification(game, ResponseOperationType.GameChanged);
  }

  public void sendToClient(Player player, ResponseOperation gameNotification) {
    if (!botService.isPlayerBot(player)) {
      connectionService.sendWithConversion(player.getId(), gameNotification);
    }
  }

  public ResponseOperation createGameNotification(Game game,
    ResponseOperationType responseOperationType) {
    updateCurrentMoveTimeLeft(game);

    return new ResponseOperation(responseOperationType, game);
  }

  private void updateCurrentMoveTimeLeft(Game game) {
    long currentMoveTimeLeft = game.getTimePerMoveMs() -
      (Instant.now().toEpochMilli() - game.getCurrentMoveStarted());

    game.setClientCurrentMoveTimeLeft(currentMoveTimeLeft);
  }

}
