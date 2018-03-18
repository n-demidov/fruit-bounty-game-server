package com.demidovn.fruitbounty.server.integrationtests;

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChatTest extends AbstractAuthMockedTest {

  private static final String SENT_MESSAGE = "Hello from integration test: ChatTest.";

  @Test
  public void onlineUsersGetPublicMessage_whenSendPublicMessage() {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();

    sendAndAssertMessages(secondWebsocketClient, 1);
    sendAndAssertMessages(secondWebsocketClient, 2);
  }

  @Test
  public void userGetHistoryMessages_whenAuthed() {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();

    assertResponseContains(secondWebsocketClient, SENT_MESSAGE);
    assertTrue(secondWebsocketClient.isConnected());
  }

  private void sendAndAssertMessages(WebsocketClient secondWebsocketClient, int expectedMessagesCount) {
    operationExecutor.sendChatMessage(websocketClient, SENT_MESSAGE);
    waitForNotifications();

    assertResponseContains(websocketClient, SENT_MESSAGE, expectedMessagesCount);
    assertResponseContains(secondWebsocketClient, SENT_MESSAGE, expectedMessagesCount);

    assertTrue(websocketClient.isConnected());
    assertTrue(secondWebsocketClient.isConnected());
  }

}
