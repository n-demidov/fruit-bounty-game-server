package com.demidovn.fruitbounty.server.services.chat;

import com.demidovn.fruitbounty.server.AppConfigs;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ChatHub {

  private final String[] messages = new String[AppConfigs.CHAT_HUB_LIMIT];

  public synchronized void push(String newMessage) {
    moveMessages();
    addNewMessage(newMessage);
  }

  public List<String> get() {
    return Arrays.stream(messages)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  private void addNewMessage(String newMessage) {
    messages[messages.length - 1] = newMessage;
  }

  private void moveMessages() {
    System.arraycopy(messages, 1, messages, 0, messages.length - 1);
  }

}
