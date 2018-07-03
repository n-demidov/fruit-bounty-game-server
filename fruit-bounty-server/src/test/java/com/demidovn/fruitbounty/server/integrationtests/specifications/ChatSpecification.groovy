package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractAuthMockedSpecification
import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient
import spock.lang.Stepwise

import static org.junit.Assert.assertTrue

@Stepwise
class ChatSpecification extends AbstractAuthMockedSpecification {

  private static final String SENT_MESSAGE = "Hello from integration test: ChatSpecification."

  def "new chat messages should be sent to all online users"() {
    given:
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect()

    when:
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID)

    then:
    waitForNotifications()
    sendAndAssertMessage(secondWebsocketClient, websocketClient, 1)
    sendAndAssertMessage(secondWebsocketClient, websocketClient, 2)
  }

  def "chat history should be sent to successfully authed user"() {
    given:
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect()

    when:
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID)

    then:
    waitForNotifications()
    assertMessageReceived(secondWebsocketClient, SENT_MESSAGE, 1)
  }

  private void sendAndAssertMessage(WebsocketClient firstWebsocket, WebsocketClient secondWebsocket,
                                    int expectedMsgCount) {
    operationExecutor.sendChatMessage(firstWebsocket, SENT_MESSAGE)

    waitForNotifications()

    assertMessageReceived(firstWebsocket, SENT_MESSAGE, expectedMsgCount)
    assertMessageReceived(secondWebsocket, SENT_MESSAGE, expectedMsgCount)
  }

  private void assertMessageReceived(WebsocketClient ws, String chatMessage, int expectedMsgCount) {
    assertTrue(ws.isConnected())

    assertResponseContains(ws, chatMessage, expectedMsgCount)
  }

}
