package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.Constants
import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractAuthMockedSpecification
import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient

class CloseConnectionSpecification extends AbstractAuthMockedSpecification {

  private static final String OLD_CONNECTION_WILL_CLOSE_MESSAGE =
      "Connection will close, because new was started."

  def "should disconnect when client send unknown operation"() {
    given:
    String message = "{ \"name\" : \"Nick\" }"

    when:
    websocketClient.sendToServer(message)
    waitForNotifications()

    then:
    !websocketClient.isConnected()
  }

  def "should disconnect when user connects from new device"() {
    when: "auth from old connection"
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    waitForNotifications()

    then:
    websocketClient.isConnected()

    when: "auth from new connection"
    WebsocketClient newWebsocketClient = new WebsocketClient(randomServerPort).connect()
    operationExecutor.auth(newWebsocketClient, DEFAULT_USER_ID)
    waitForNotifications()

    then:
    !websocketClient.isConnected()
    websocketClient.containsResponse(OLD_CONNECTION_WILL_CLOSE_MESSAGE)
    websocketClient.containsResponse(Constants.USER_INFO_OPERATION_TYPE)

    newWebsocketClient.isConnected()
    newWebsocketClient.notContainsResponse(OLD_CONNECTION_WILL_CLOSE_MESSAGE)
    newWebsocketClient.containsResponse(Constants.USER_INFO_OPERATION_TYPE)
  }

  def "should disconnect when user authed twice"() {
    when: "first connection"
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    waitForNotifications()

    then:
    websocketClient.isConnected()
    assertResponses()

    when: "second connection"
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    waitForNotifications()

    then:
    !websocketClient.isConnected()
    assertResponses()
  }

  private void assertResponses() {
    assert websocketClient.containsResponse(Constants.USER_INFO_OPERATION_TYPE)
    assert websocketClient.getServerResponses().size() == 3
  }

}
