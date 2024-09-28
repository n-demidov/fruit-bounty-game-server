package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.exceptions.AbstractGameServerException;
import com.demidovn.fruitbounty.server.services.websocket.WebSocketSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ConnectionService {

  private static final long SLEEP_BEFORE_DISCONNECT = 10L;
  private static final String NO_SUCH_CONNECTION_ERROR = "Can't find connection with id='%s'.";
  private static final String CONNECTION_WILL_CLOSE_BECAUSE_NEW_WAS_STARTED =
      "Connection will close, because new was started.";
  private static final String CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME =
      "Connection will close, because you wasn't auth a long time.";
  private static final String CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE =
      "Connection will close, because you was not active a long time.";

  @Value("${game-server.ttl.not-authed-connection}")
  private long NOT_AUTHED_CONNECTION_TTL_SEC;

  @Value("${game-server.ttl.authed-inactive-connection}")
  private long AUTHED_INACTIVE_CONNECTION_TTL_SEC;

  @Autowired
  private WebSocketSender webSocketSender;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final Map<String, Connection> notAuthedConnections = new ConcurrentHashMap<>();
  private final Map<String, Connection> authedConnections = new ConcurrentHashMap<>();
  private final Map<Long, Connection> onlineUsers = new ConcurrentHashMap<>();

  public Connection getAnyConnection(String connectionId) {
    Optional<Connection> foundConnection = findAnyConnection(connectionId);
    if (foundConnection.isPresent()) {
      return foundConnection.get();
    }

    String errorMessage = String.format(NO_SUCH_CONNECTION_ERROR, connectionId);
    log.error(errorMessage);
    throw new AbstractGameServerException(errorMessage);
  }

  public void wasConnected(WebSocketSession wsSession) {
    Connection connection = new Connection(wsSession);

    notAuthedConnections.put(wsSession.getId(), connection);
  }

  public void wasDisconnected(String connectionId) {
    clearConnection(connectionId);
  }

  public void wasAuthed(Connection connection, long userId) {
    closeOldUserConnection(connection, userId);
    clearOldUserFromMap(connection, userId);

    connection.setUserId(userId);
    connection.updateLastActionTime();

    authedConnections.put(connection.getId(), connection);
    onlineUsers.put(userId, connection);

    notAuthedConnections.remove(connection.getId());
    log.info("Connection id={} was marked as authed", connection.getId());
  }

  public boolean isUserOnline(long userId) {
    return onlineUsers.containsKey(userId);
  }

  public void killConnection(Connection connection) {
    log.debug("killConnection connection={}", connection);

    clearConnection(connection.getId());
    closeConnection(connection);
  }

  public void send(Connection targetConnection, Object payload, ResponseOperationType operationType) {
    ResponseOperation responseOperation = new ResponseOperation(operationType, payload);

    send(targetConnection, responseOperation);
  }

  public void send(Connection targetConnection, Object payload) {
    // log.trace("send, targetConnection={}, payload={}", targetConnection, payload);

    if (!targetConnection.getWsSession().isOpen()) {
      wasDisconnected(targetConnection.getId());
      return;
    }

    webSocketSender.sendToSession(targetConnection.getId(), payload);
  }

  public void sendWithConversion(Connection targetConnection, Object payload) {
    send(targetConnection, payload);
  }

  public void send(long userId, Object payload) {
    Connection connection = onlineUsers.get(userId);

    if (connection != null) {
      send(connection, payload);
    }
  }

  public void sendWithConversion(long userId, Object payload) {
    send(userId, payload);
  }

  public void sendToAll(Object payload) {
    webSocketSender.sendToAll(payload);
  }

  public int getAllCount() {
    return notAuthedConnections.size() + authedConnections.size();
  }

  public int countNotAuthedConnections() {
    return notAuthedConnections.size();
  }

  public int countOnlineUsers() {
    return onlineUsers.size();
  }

  public int countAuthedConnections() {
    return authedConnections.size();
  }

  public void validNotAuthedConnections() {
    log.trace("Starting of validNotAuthedConnections; allConnections={}; notAuthedConnections={}", getAllCount(), notAuthedConnections.size());

    final long nowSeconds = Instant.now().getEpochSecond();
    List<Connection> copiedConnections = new ArrayList<>(notAuthedConnections.values());

    for (Connection connection : copiedConnections) {
      if (isNotAuthedConnectionExpired(nowSeconds, connection)) {
        log.debug("TTL of not-authed-connection id='{}' was expired and it will be closed", connection.getId());
        sendMessageBeforeCloseConnection(connection,
            CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME);
        killConnection(connection);
      }
    }

    log.trace("Ending of validNotAuthedConnections; allConnections={}; notAuthedConnections={}", getAllCount(), notAuthedConnections.size());
  }

  public void validAuthedConnections() {
    log.trace("Starting of validAuthedConnections; allConnections={}; authedConnections={}", getAllCount(), authedConnections.size());

    final long nowSeconds = Instant.now().getEpochSecond();
    List<Connection> copiedConnections = new ArrayList<>(authedConnections.values());

    for (Connection connection : copiedConnections) {
      if (nowSeconds > connection.getLastActionTime() + AUTHED_INACTIVE_CONNECTION_TTL_SEC) {
        log.debug("TTL of inactive authed-connection id='{}' was expired and it will be closed", connection.getId());
        sendMessageBeforeCloseConnection(connection,
            CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE);
        killConnection(connection);
      }
    }

    log.trace("Ending of validAuthedConnections; allConnections={}; authedConnections={}", getAllCount(), authedConnections.size());
  }

  private Optional<Connection> findAnyConnection(String connectionId) {
    Connection connection = authedConnections.get(connectionId);
    if (connection != null) {
      return Optional.of(connection);
    }

    connection = notAuthedConnections.get(connectionId);
    if (connection != null) {
      return Optional.of(connection);
    }

    return Optional.empty();
  }

  private void clearConnection(String connectionId) {
    Optional<Connection> foundConnection = findAnyConnection(connectionId);
    if (foundConnection.isPresent()) {
      notAuthedConnections.remove(connectionId);
      authedConnections.remove(connectionId);

      onlineUsers.remove(foundConnection.get().getUserId());
    }
  }

  private void closeConnection(Connection connection) {
    try {
      connection.getWsSession().close();
    } catch (IOException e) {
      log.error("Error on closing WS connection", e);
    }
  }

  private void closeOldUserConnection(Connection newConnection, long userId) {
    if (onlineUsers.containsKey(userId)) {
      Connection oldConnection = onlineUsers.get(userId);

      if (!oldConnection.equals(newConnection)) {
        log.trace("Connection will close, because new was started. oldConnection={}, newConnection={}", oldConnection, newConnection);

        sendMessageBeforeCloseConnection(oldConnection,
            CONNECTION_WILL_CLOSE_BECAUSE_NEW_WAS_STARTED);
        killConnection(oldConnection);
      }
    }
  }

  private boolean isNotAuthedConnectionExpired(long nowSeconds, Connection connection) {
    return nowSeconds > connection.getConnectionEstablished() + NOT_AUTHED_CONNECTION_TTL_SEC;
  }

  private void sendMessageBeforeCloseConnection(Connection oldConnectionId, String causeClosingMessage) {
    ResponseOperation responseOperation = new ResponseOperation(
            ResponseOperationType.SendChat,
            Collections.singletonList(causeClosingMessage));
    send(oldConnectionId, responseOperation);

    minSleep();
  }

  private void clearOldUserFromMap(Connection connection, long newUserId) {
    long oldUserId = connection.getUserId();

    if (oldUserId != 0 && oldUserId != newUserId) {
      onlineUsers.remove(oldUserId);
    }
  }

  private void minSleep() {
    try {
      Thread.sleep(SLEEP_BEFORE_DISCONNECT);
    } catch (InterruptedException e) {
      log.error("minSleep", e);
    }
  }

  private String convert2Json(Object payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      log.error("An error occurring on conversion to JSON, payload={}", payload, e);
      throw new IllegalStateException(e);
    }
  }

}
