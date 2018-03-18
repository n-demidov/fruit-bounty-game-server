package com.demidovn.fruitbounty.server.config.websocket.handlers;

import com.demidovn.fruitbounty.server.services.ConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Slf4j
public class GameServerSubProtocolWebSocketHandler extends SubProtocolWebSocketHandler {

  @Autowired
  private ConnectionService connectionService;

  public GameServerSubProtocolWebSocketHandler(MessageChannel clientInboundChannel, SubscribableChannel clientOutboundChannel) {
    super(clientInboundChannel, clientOutboundChannel);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);

    connectionService.wasConnected(session);
    log.info("New websocket connection was established; connectionId={}; all connections={}", session.getId(), connectionService.getAllCount());
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    super.afterConnectionClosed(session, closeStatus);

    log.info("Websocket connection was closed: connectionId={}, all connections={}", session.getId(), connectionService.getAllCount());

    connectionService.wasDisconnected(session.getId());
  }

}
