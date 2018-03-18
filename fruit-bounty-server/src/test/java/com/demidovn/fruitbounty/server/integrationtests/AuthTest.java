package com.demidovn.fruitbounty.server.integrationtests;

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class AuthTest extends AbstractAuthMockedTest {

  @Test
  public void testAuth() throws JSONException {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();

    assertAuth(websocketClient, getMockedUserName(DEFAULT_USER_ID));
    assertAuth(secondWebsocketClient, getMockedUserName(SECOND_USER_ID));
  }

  private void assertAuth(WebsocketClient ws, String userName) throws JSONException {
    assertTrue(ws.isConnected());

    assertResponseContains(ws, Constants.USER_INFO_OPERATION_TYPE);
    assertResponseContains(ws, Constants.SEND_CHAT_OPERATION_TYPE);
    assertResponseContains(ws, Constants.RATING_TABLE_OPERATION_TYPE);
    Assertions.assertThat(ws.getServerResponses().size()).isEqualTo(3);

    String secondResponse = findResponse(ws, Constants.USER_INFO_OPERATION_TYPE);
    JSONObject jsonObj = new JSONObject(secondResponse);

    assertThat(jsonObj.get("type")).isEqualTo(Constants.USER_INFO_OPERATION_TYPE);
    assertThat(((JSONObject) jsonObj.get("data")).get("name")).isEqualTo(userName);
    assertThat(((JSONObject) jsonObj.get("data")).get("score")).isEqualTo(700);
    assertThat(((JSONObject) jsonObj.get("data")).get("wins")).isEqualTo(0);
  }

}
