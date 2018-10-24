package com.demidovn.fruitbounty.server.integrationtests.bases

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext
abstract class AbstractWebsocketClientSpecification extends AbstractSpecification {

  protected static final int DEFAULT_NOTIFICATION_SLEEP = 600

  @LocalServerPort
  protected int randomServerPort

  protected WebsocketClient websocketClient

  def setup() {
    websocketClient = new WebsocketClient(randomServerPort)
    websocketClient.connect()
  }

  def cleanup() {
    if (websocketClient != null) {
      websocketClient.close()
    }
  }

  protected void waitForNotifications() {
    waitForNotifications(DEFAULT_NOTIFICATION_SLEEP)
  }

  protected void waitForNotifications(int waitTime) {
    try {
      Thread.sleep(waitTime)
    } catch (InterruptedException e) {
      throw new RuntimeException(e)
    }
  }

}
