package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.Constants
import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractAuthMockedSpecification
import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient
import org.json.JSONException
import org.json.JSONObject

class AuthSpecification extends AbstractAuthMockedSpecification {

  def "should successfully auth users with getting account's data"() {
    given:
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect()

    when:
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID)

    then:
    waitForNotifications()

    assertAuth(websocketClient, getMockedUserName(DEFAULT_USER_ID))
    assertAuth(secondWebsocketClient, getMockedUserName(SECOND_USER_ID))
  }

  private void assertAuth(WebsocketClient ws, String userName) throws JSONException {
    assert ws.isConnected()

    assert ws.getServerResponses().size() == 3

    assert ws.containsResponse(Constants.USER_INFO_OPERATION_TYPE)
    assert ws.containsResponse(Constants.SEND_CHAT_OPERATION_TYPE)
    assert ws.containsResponse(Constants.RATING_TABLE_OPERATION_TYPE)

    assertAccountDetails(ws, userName)
  }

  private void assertAccountDetails(WebsocketClient ws, String userName) {
    String secondResponse = ws.findResponse(Constants.USER_INFO_OPERATION_TYPE)
    JSONObject jsonObj = new JSONObject(secondResponse)

    assert jsonObj.get("type") == Constants.USER_INFO_OPERATION_TYPE
    assert ((JSONObject) jsonObj.get("data")).get("name") == userName
    assert ((JSONObject) jsonObj.get("data")).get("score") == 700
    assert ((JSONObject) jsonObj.get("data")).get("wins") == 0
  }

}
