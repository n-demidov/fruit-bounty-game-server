package com.demidovn.fruitbounty.server.integrationtests;

import static org.junit.Assert.assertTrue;

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.TestPropertySource;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestPropertySource(properties = {
    "game-server.schedule-delay.process-game-requests=300"
})
public class GameRequestsTest extends AbstractAuthMockedTest {

  private static final String GAME_STARTED_NOTIFICATION = "GameStarted";
  private static final int GAME_ASPIRANTS_COUNT = 40, INIT_MOCK_USER_ID_INDEX = 1000;
  private static final int MIN_WAIT_TIME = 10;
  private static final int LONG_WAIT_TIME = DEFAULT_NOTIFICATION_SLEEP + 250;

  private List<WebsocketClient> gameAspirants = new ArrayList<>();

  @Before
  public void setupGameAspirants() {
    for (int i = 0; i < GAME_ASPIRANTS_COUNT; i++) {
      WebsocketClient ws = new WebsocketClient(randomServerPort).connect();

      int userId = i + INIT_MOCK_USER_ID_INDEX;
      mockAuth(userId);
      operationExecutor.auth(ws, userId);

      gameAspirants.add(ws);
    }
  }

  @After
  public void disconnectGameAspirants() {
    gameAspirants.forEach(WebsocketClient::close);
  }

  @Test
  public void a_whenSendMultipleRequests() {
    WebsocketClient secondWebsocketClient = new WebsocketClient(randomServerPort).connect();

    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);
    operationExecutor.auth(secondWebsocketClient, SECOND_USER_ID);

    waitForNotifications();

    operationExecutor.sendGameRequest(websocketClient);
    operationExecutor.rejectGameRequest(websocketClient);
    operationExecutor.rejectGameRequest(websocketClient);

    operationExecutor.sendGameRequest(secondWebsocketClient);
    operationExecutor.sendGameRequest(secondWebsocketClient);
    operationExecutor.rejectGameRequest(secondWebsocketClient);

    waitForNotifications(MIN_WAIT_TIME);

    gameAspirants.forEach(ws ->
        operationExecutor.sendGameRequest(ws));

    waitForNotifications(LONG_WAIT_TIME);

    assertResponseNotContains(websocketClient, GAME_STARTED_NOTIFICATION);
    assertResponseNotContains(secondWebsocketClient, GAME_STARTED_NOTIFICATION);

    gameAspirants.forEach(ws ->
        assertResponseContains(ws, GAME_STARTED_NOTIFICATION));

    assertAllConnectionsAlive(Arrays.asList(websocketClient, secondWebsocketClient));
    assertAllConnectionsAlive(gameAspirants);
  }

  @Test
  public void b_noNewGame_whenSendRepeatedRequests() {
    waitForNotifications();

    gameAspirants.forEach(ws -> {
      assertResponseContains(ws, GAME_STARTED_NOTIFICATION);

      ws.getServerResponses().clear();
      operationExecutor.sendGameRequest(ws);
    });

    waitForNotifications();

    gameAspirants.forEach(ws ->
        assertResponseNotContains(ws, GAME_STARTED_NOTIFICATION));

    assertAllConnectionsAlive(gameAspirants);
  }

  private void assertAllConnectionsAlive(List<WebsocketClient> assertingWebsocketClients) {
    assertingWebsocketClients.forEach(ws -> assertTrue(ws.isConnected()));
  }

}
