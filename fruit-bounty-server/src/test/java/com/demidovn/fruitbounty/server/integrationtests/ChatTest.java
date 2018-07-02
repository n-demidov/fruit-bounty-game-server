package com.demidovn.fruitbounty.server.integrationtests;

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChatTest extends AbstractAuthMockedTest {

  private static final String SENT_MESSAGE = "Hello from integration test: ChatTest.";

  @Test
  public void a_newChatMessageShouldBeSent2AllOnlineUsers() {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();

    sendAndAssertMessage(secondWebsocketClient, websocketClient, 1);
    sendAndAssertMessage(secondWebsocketClient, websocketClient, 2);
  }

  @Test
  public void b_chatHistoryShouldBeSent2SuccessfullyAuthedUser() {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();
    assertMessageReceived(secondWebsocketClient, SENT_MESSAGE, 1);
  }

  private void sendAndAssertMessage(WebsocketClient firstWebsocket, WebsocketClient secondWebsocket,
    int expectedMsgCount) {
    operationExecutor.sendChatMessage(firstWebsocket, SENT_MESSAGE);

    waitForNotifications();

    assertMessageReceived(firstWebsocket, SENT_MESSAGE, expectedMsgCount);
    assertMessageReceived(secondWebsocket, SENT_MESSAGE, expectedMsgCount);
  }

  private void assertMessageReceived(WebsocketClient ws, String chatMessage, int expectedMsgCount) {
    assertTrue(ws.isConnected());

    assertResponseContains(ws, chatMessage, expectedMsgCount);
  }

}
