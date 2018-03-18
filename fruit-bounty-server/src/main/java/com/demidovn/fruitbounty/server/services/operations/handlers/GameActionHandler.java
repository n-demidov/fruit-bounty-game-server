package com.demidovn.fruitbounty.server.services.operations.handlers;

import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.server.dto.operations.request.GameRequestOperation;
import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.services.game.UserGames;
import com.demidovn.fruitbounty.server.validators.GameActionValidator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GameActionHandler implements OperationHandler {

  @Autowired
  private UserGames userGames;

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private GameActionValidator gameActionValidator;

  @Override
  public OperationType getOperationType() {
    return OperationType.GameAction;
  }

  @Override
  public void process(RequestOperation operation) {
    log.trace("process operation={}", operation);

    Optional<Game> currentGame = userGames.getCurrentGame(operation.getConnection().getUserId());
    if (!currentGame.isPresent()) {
      return;
    }

    GameRequestOperation gameRequestOperation = new GameRequestOperation(operation, currentGame.get());

    gameActionValidator.valid(gameRequestOperation, currentGame.get());
    GameAction gameAction = conversionService.convert(gameRequestOperation, GameAction.class);

    userGames.processGameAction(gameAction);
  }

}
