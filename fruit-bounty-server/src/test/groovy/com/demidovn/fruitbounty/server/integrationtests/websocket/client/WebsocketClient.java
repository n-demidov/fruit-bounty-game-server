package com.demidovn.fruitbounty.server.integrationtests.websocket.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class WebsocketClient {

  private static final String WS_USER_SUBSCRIPTION_QUEUE_NAME = "/user/queue/to_client";
  private static final String WS_USER_SUBSCRIPTION_TOPIC_NAME = "/topic/broadcast";
  private static final String WS_USER_SEND_QUEUE_NAME = "/app/from_client";
  private static final String WS_CONNECTION_URL = "ws://localhost:%d/connect-app";

  private final String websocketConnectionUrl;

  @Getter
  private final List<String> serverResponses = new ArrayList<>();
  private final WebSocketStompClient stompClient;
  private StompSession session;

  public WebsocketClient(int randomServerPort) {
    websocketConnectionUrl = String.format(WS_CONNECTION_URL, randomServerPort);

    Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
    List<Transport> transports = Collections.singletonList(webSocketTransport);
    SockJsClient sockJsClient = new SockJsClient(transports);
    sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

    stompClient = new WebSocketStompClient(sockJsClient);
  }

  public WebsocketClient connect() {
    try {
      session = stompClient
              .connect(websocketConnectionUrl, new StompSessionHandlerAdapter() {})
              .get(2, TimeUnit.SECONDS);

      log.info("Client has connected to the server");

      session.subscribe(WS_USER_SUBSCRIPTION_QUEUE_NAME, new QueueHandler());
      session.subscribe(WS_USER_SUBSCRIPTION_TOPIC_NAME, new QueueHandler());

      return this;
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    if (session.isConnected()) {
      session.disconnect();
    }
  }

  public void sendToServer(String message) {
    session.send(WS_USER_SEND_QUEUE_NAME, message.getBytes());
  }

  public boolean isConnected() {
    return session.isConnected();
  }

  public long countResponses(String content) {
    return getServerResponses()
            .stream()
            .filter(msg -> msg.contains(content))
            .count();
  }

  public boolean containsResponse(String content) {
    return countResponses(content) == 1;
  }

  public boolean notContainsResponse(String content) {
    return countResponses(content) == 0;
  }

  public String findResponse(String content) {
    return getServerResponses()
            .stream()
            .filter(msg -> msg.contains(content))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Can't find '%s' in responses", content)));
  }

  @Override
  public String toString() {
    return "WebsocketClient{" +
            "websocketConnectionUrl='" + websocketConnectionUrl + '\'' +
            ", serverResponses=" + serverResponses +
            '}';
  }


  public class QueueHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
      return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
      serverResponses.add(new String((byte[]) o));
    }
  }

}
