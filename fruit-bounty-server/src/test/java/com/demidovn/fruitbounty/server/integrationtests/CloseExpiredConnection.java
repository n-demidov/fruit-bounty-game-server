package com.demidovn.fruitbounty.server.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "game-server.schedule-delay.valid-not-authed-connections=91",
    "game-server.ttl.not-authed-connection=0",
    "game-server.schedule-delay.valid-authed-connections=91",
    "game-server.ttl.authed-inactive-connection=0"
})
public class CloseExpiredConnection extends AbstractAuthMockedTest {

  private static final String CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME =
      "Connection will close, because you wasn't auth a long time.";
  private static final String CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE =
      "Connection will close, because you was not active a long time.";
  private static final int WAIT_TIME = 1100;

  @Test
  public void whenNotAuthedExpired() {
    waitForNotifications(WAIT_TIME);

    assertFalse(websocketClient.isConnected());
    assertResponseContains(websocketClient, CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME);

    assertThat(websocketClient.getServerResponses().size()).isEqualTo(1);
  }

  @Test
  public void whenAuthedInactiveExpired() {
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID);

    waitForNotifications(WAIT_TIME);

    assertFalse(websocketClient.isConnected());
    assertResponseContains(websocketClient, Constants.USER_INFO_OPERATION_TYPE);
    assertResponseContains(websocketClient, CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE);

    assertThat(websocketClient.getServerResponses().size()).isEqualTo(4);
  }

}
