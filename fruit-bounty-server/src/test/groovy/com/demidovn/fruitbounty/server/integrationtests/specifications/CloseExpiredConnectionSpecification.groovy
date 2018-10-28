package com.demidovn.fruitbounty.server.integrationtests.specifications

import com.demidovn.fruitbounty.server.integrationtests.Constants
import com.demidovn.fruitbounty.server.integrationtests.bases.AbstractAuthMockedSpecification
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = [
    "game-server.schedule-delay.valid-not-authed-connections=91",
    "game-server.ttl.not-authed-connection=0",
    "game-server.schedule-delay.valid-authed-connections=30",
    "game-server.ttl.authed-inactive-connection=0"
])
class CloseExpiredConnectionSpecification extends AbstractAuthMockedSpecification {

  private static final String CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME =
      "Connection will close, because you wasn't auth a long time."
  private static final String CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE =
      "Connection will close, because you was not active a long time."
  private static final int WAIT_TIME = 1100

  def "should disconnect not-authed connection when it didn't auth too long time"() {
    when:
    waitForNotifications(WAIT_TIME)

    then:
    !websocketClient.isConnected()
    websocketClient.containsResponse(CONNECTION_WILL_CLOSE_BECAUSE_WAS_NOT_AUTHED_LONG_TIME)

    websocketClient.getServerResponses().size() == 1
  }

  def "should disconnect authed connection when it was inactive too long time"() {
    given:
    operationExecutor.auth(websocketClient, DEFAULT_USER_ID)

    when: "wait mocked TTL to expire authed connection"
    waitForNotifications(WAIT_TIME)

    then:
    !websocketClient.isConnected()
    websocketClient.containsResponse(Constants.USER_INFO_OPERATION_TYPE)
    websocketClient.containsResponse(CONNECTION_WILL_CLOSE_BECAUSE_USER_WAS_NOT_ACTIVE)

    websocketClient.getServerResponses().size() == 4
  }

}
