package com.demidovn.fruitbounty.server.integrationtests;

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CloseConnectionTest extends AbstractAuthMockedTest {

  private static final String OLD_CONNECTION_WILL_CLOSE_MESSAGE =
      "Connection will close, because new was started.";

  @Test
  public void whenSendUnknownOperation() {
    String message = "{ \"name\" : \"Nick\" }";
    websocketClient.sendToServer(message);

    waitForNotifications();

    assertFalse(websocketClient.isConnected());
  }

  @Test
  public void whenUserConnectFromNewDevice() {
    // auth from old connection
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);

    waitForNotifications();

    assertTrue(websocketClient.isConnected());

    // authed from new connection
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();
    operationExecutor.auth(secondWebsocketClient, DEFAULT_USER_ID);

    waitForNotifications();

    assertFalse(websocketClient.isConnected());
    assertResponseContains(websocketClient, OLD_CONNECTION_WILL_CLOSE_MESSAGE);
    assertResponseContains(websocketClient, Constants.USER_INFO_OPERATION_TYPE);

    assertTrue(secondWebsocketClient.isConnected());
    assertResponseNotContains(secondWebsocketClient, OLD_CONNECTION_WILL_CLOSE_MESSAGE);
    assertResponseContains(secondWebsocketClient, Constants.USER_INFO_OPERATION_TYPE);
  }

  @Test
  public void whenUserAuthedTwice() {
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);

    waitForNotifications();

    assertTrue(websocketClient.isConnected());
    assertResponseContains(websocketClient, Constants.USER_INFO_OPERATION_TYPE);
    assertThat(websocketClient.getServerResponses().size()).isEqualTo(3);

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);

    waitForNotifications();

    assertFalse(websocketClient.isConnected());
    assertResponseContains(websocketClient, Constants.USER_INFO_OPERATION_TYPE);
    assertThat(websocketClient.getServerResponses().size()).isEqualTo(3);
  }

}
