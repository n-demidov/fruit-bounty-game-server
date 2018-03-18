package com.demidovn.fruitbounty.server.controllers;

import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.operations.OperationDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

  private static final String SOME_ERROR_ON_SERVER_ERR_MESSAGE =
    "An error was occurred on the server. Please, contact to administrator.";

  @Autowired
  private OperationDispatcher operationDispatcher;

  @Autowired
  private ConnectionService connectionService;

  @MessageMapping(AppConfigs.WS_FROM_USER_QUEUE_NAME)
  public void processUserMessage(@Payload RequestOperation requestOperation,
      SimpMessageHeaderAccessor headerAccessor) throws Exception {
    requestOperation.setConnection(connectionService.getAnyConnection(headerAccessor.getSessionId()));

    operationDispatcher.process(requestOperation);
  }

  @MessageExceptionHandler
  @SendToUser(AppConfigs.WS_USER_ERRORS_QUEUE_NAME)
  public String handleException(Throwable exception) {
    final String message;

    if (AppConfigs.isDev) {
      message = exception.getMessage();
    } else {
      message = SOME_ERROR_ON_SERVER_ERR_MESSAGE;
    }

    return message;
  }

}
