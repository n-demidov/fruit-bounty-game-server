package com.demidovn.fruitbounty.server.integrationtests.opeartions;

import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.integrationtests.websocket.client.WebsocketClient;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class OperationExecutor {

  @Autowired
  private OperationsCreator operationsCreator;

  public void auth(WebsocketClient websocketClient, int userId) {
    Map<String, String> payloadDetails = new LinkedHashMap<>();
    AuthOperation authOperation = operationsCreator.getAuthOperation(userId, AuthType.FB);

    payloadDetails.put("type", authOperation.getType().getStringRepresentation());
    payloadDetails.put("accessToken", authOperation.getAccessToken());
    payloadDetails.put("userId", String.valueOf(authOperation.getUserId()));

    sendPayload(websocketClient, payloadDetails, "Auth");
  }

  public void sendChatMessage(WebsocketClient websocketClient, String message) {
    Map<String, String> payloadDetails = new LinkedHashMap<>();
    payloadDetails.put("msg", message);

    sendPayload(websocketClient, payloadDetails, "SendChat");
  }

  public void sendGameRequest(WebsocketClient websocketClient) {
    sendGameRequest(websocketClient, "y");
  }

  public void rejectGameRequest(WebsocketClient websocketClient) {
    sendGameRequest(websocketClient, "n");
  }

  private void sendPayload(WebsocketClient websocketClient, Map<String, String> payloadDetails,
      String operationType) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("type", operationType);
    payload.put("data", payloadDetails);

    websocketClient.sendToServer(new JSONObject(payload).toString());
  }

  private void sendGameRequest(WebsocketClient websocketClient, String acknowledge) {
    Map<String, String> payloadDetails = new LinkedHashMap<>();
    payloadDetails.put("ack", acknowledge);

    sendPayload(websocketClient, payloadDetails, "GameRequest");
  }

}
