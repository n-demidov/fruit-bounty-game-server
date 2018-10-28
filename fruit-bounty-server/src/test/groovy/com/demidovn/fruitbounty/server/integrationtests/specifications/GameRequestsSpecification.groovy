package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractAuthMockedSpecification
import spock.lang.Stepwise

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "game-server.schedule-delay.process-game-requests=300"
])
@Stepwise
class GameRequestsSpecification extends AbstractAuthMockedSpecification {

  private static final String GAME_STARTED_NOTIFICATION = "GameStarted"
  private static final int GAME_ASPIRANTS_COUNT = 40, INIT_MOCK_USER_ID_INDEX = 1000
  private static final int MIN_WAIT_TIME = 20
  private static final int LONG_WAIT_TIME = DEFAULT_NOTIFICATION_SLEEP * 1.5

  private List<WebsocketClient> gameAspirants = new ArrayList<>()

  def setup() {
    for (int i = 0; i < GAME_ASPIRANTS_COUNT; i++) {
      int userId = i + INIT_MOCK_USER_ID_INDEX
      mockAuth(userId)

      WebsocketClient ws = new WebsocketClient(randomServerPort).connect()

      waitForNotifications(MIN_WAIT_TIME)

      operationExecutor.auth(ws, userId)

      gameAspirants.add(ws)
    }
  }

  def cleanup() {
    gameAspirants.each{ws -> ws.close()}
  }

  def "should create games between submitted requests; and not between cancelled"() {
    given:
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect()

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID)

    when:
    waitForNotifications()

    operationExecutor.sendGameRequest(websocketClient)
    waitForNotifications(MIN_WAIT_TIME)
    operationExecutor.rejectGameRequest(websocketClient)
    operationExecutor.rejectGameRequest(websocketClient)
    operationExecutor.rejectGameRequest(websocketClient)
    waitForNotifications(MIN_WAIT_TIME)

    operationExecutor.sendGameRequest(secondWebsocketClient)
    operationExecutor.sendGameRequest(secondWebsocketClient)
    operationExecutor.sendGameRequest(secondWebsocketClient)
    waitForNotifications(MIN_WAIT_TIME * 2)
    operationExecutor.rejectGameRequest(secondWebsocketClient)
    waitForNotifications(MIN_WAIT_TIME)

    gameAspirants.forEach({ws ->
      operationExecutor.sendGameRequest(ws)})
    waitForNotifications(LONG_WAIT_TIME)

    then:
    websocketClient.notContainsResponse(GAME_STARTED_NOTIFICATION)
    secondWebsocketClient.notContainsResponse(GAME_STARTED_NOTIFICATION)

    gameAspirants.forEach({ws -> assert ws.containsResponse(GAME_STARTED_NOTIFICATION)})

    assertAllConnectionsAlive(Arrays.asList(websocketClient, secondWebsocketClient))
    assertAllConnectionsAlive(gameAspirants)
  }

  def "shouldn't create new game when user already playing"() {
    when:
    waitForNotifications()

    for (ws in gameAspirants) {
      assert ws.containsResponse(GAME_STARTED_NOTIFICATION)

      ws.getServerResponses().clear()
      operationExecutor.sendGameRequest(ws)
    }

    then:
    waitForNotifications()

    gameAspirants.forEach({ws ->
      assert ws.notContainsResponse(GAME_STARTED_NOTIFICATION)})

    assertAllConnectionsAlive(gameAspirants)
  }

  private void assertAllConnectionsAlive(List<WebsocketClient> assertingWebsocketClients) {
    assertingWebsocketClients.forEach({ws -> assert ws.isConnected()})
  }

}
