package com.demidovn.fruitbounty.server.integrationtests.bases

import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.test.annotation.DirtiesContext

import static org.junit.Assert.assertEquals

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

  protected void assertResponseContains(WebsocketClient websocketClient, String content,
      int expectedCount) {
    long actualCounted = websocketClient.getServerResponses()
        .stream()
        .filter{msg -> msg.contains(content)}
        .count()

    String assertionMsg = createAssertionMessage(content, expectedCount, actualCounted)

    assertEquals(assertionMsg, expectedCount, actualCounted)
  }

  protected void assertResponseContains(WebsocketClient ws, String content) {
    assertResponseContains(ws, content, 1)
  }

  protected void assertResponseNotContains(WebsocketClient websocketClient, String content) {
    assertResponseContains(websocketClient, content, 0)
  }

  protected String findResponse(WebsocketClient ws, String content) {
    return ws.getServerResponses()
        .stream()
        .filter{msg -> msg.contains(content)}
        .findFirst()
        .orElseThrow({new IllegalStateException(String.format(
            "Can't find '%s' in responses", content))})
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

  private String createAssertionMessage(String content, int expectedCount, long actualCounted) {
    return String.format("Expected %d match: '%s' in response, but was %d.",
        expectedCount,
        content,
        actualCounted)
  }

}
