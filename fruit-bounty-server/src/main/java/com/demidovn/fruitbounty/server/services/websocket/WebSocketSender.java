package com.demidovn.fruitbounty.server.services.websocket;

import com.demidovn.fruitbounty.server.AppConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSender {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public void sendToAll(Object payload) {
        messagingTemplate.convertAndSend(AppConfigs.WS_TOPIC_NAME, payload);
    }

    public void sendToSession(String sessionId, Object payload) {
        messagingTemplate.convertAndSendToUser(
                sessionId,
                AppConfigs.WS_USER_QUEUE_NAME,
                payload,
                createMessageHeaders(sessionId));
    }

    private MessageHeaders createMessageHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

}
