package com.demidovn.fruitbounty.server.services.operations.handlers;

import com.demidovn.fruitbounty.server.MetricsConsts;
import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.UserService;
import com.demidovn.fruitbounty.server.services.chat.ChatHub;
import com.demidovn.fruitbounty.server.services.metrics.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.springframework.web.util.HtmlUtils;

@Component
public class SendChatOperationHandler implements OperationHandler {

  private static final String MESSAGE_FORMAT = "[%s] %s: %s";
  private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private UserService userService;

  @Autowired
  private ChatHub chatHub;

  @Autowired
  private StatService statService;

  @Override
  public OperationType getOperationType() {
    return OperationType.SendChat;
  }

  @Override
  public void process(RequestOperation requestOperation) {
    String chatMessage = conversionService.convert(requestOperation, String.class);
    chatMessage = HtmlUtils.htmlEscape(chatMessage);

    sendToAll(chatMessage, requestOperation.getConnection());
  }

  private void sendToAll(String chatMessage, Connection connection) {
    User user = userService.get(connection.getUserId());

    String message = formatMessage(chatMessage, user);

    chatHub.push(message);
    sendMessageToTopic(message);
    statService.incCounter(MetricsConsts.OTHER.CHAT_SENT_STAT);
  }

  private String formatMessage(String message, User user) {
    LocalDateTime localDateTime = LocalDateTime.now();

    return String.format(
            MESSAGE_FORMAT,
            localDateTime.format(timeFormatter),
            user.getPublicName(),
            message);
  }

  private void sendMessageToTopic(String message) {
    ResponseOperation responseOperation = new ResponseOperation(
            ResponseOperationType.SendChat, Collections.singletonList(message));
    connectionService.sendToAll(responseOperation);
  }

}
