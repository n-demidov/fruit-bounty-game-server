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

    assertAdditionalData(websocketClient)
    assertAdditionalData(secondWebsocketClient)
  }

  private void assertAuth(WebsocketClient ws, String userName) throws JSONException {
    ws.isConnected()
    assertResponseContains(ws, Constants.USER_INFO_OPERATION_TYPE)
    assertAccountDetails(ws, userName)
  }

  private void assertAccountDetails(WebsocketClient ws, String userName) {
    String secondResponse = findResponse(ws, Constants.USER_INFO_OPERATION_TYPE)
    JSONObject jsonObj = new JSONObject(secondResponse)

    jsonObj.get("type") == Constants.USER_INFO_OPERATION_TYPE
    jsonObj.get("type") == Constants.USER_INFO_OPERATION_TYPE
    ((JSONObject) jsonObj.get("data")).get("name") == userName
    ((JSONObject) jsonObj.get("data")).get("score") == 700
    ((JSONObject) jsonObj.get("data")).get("wins") == 0
  }

  private void assertAdditionalData(WebsocketClient ws) throws JSONException {
    ws.getServerResponses().size() == 3

    assertResponseContains(ws, Constants.SEND_CHAT_OPERATION_TYPE)
    assertResponseContains(ws, Constants.RATING_TABLE_OPERATION_TYPE)
  }

}
