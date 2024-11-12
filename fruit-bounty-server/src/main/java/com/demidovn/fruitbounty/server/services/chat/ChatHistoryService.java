package com.demidovn.fruitbounty.server.services.chat;

import com.demidovn.fruitbounty.server.persistence.entities.ChatHistory;
import com.demidovn.fruitbounty.server.persistence.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatHistoryService {

  @Autowired
  private ChatHistoryRepository chatHistoryRepository;

  public void save(String chatMessage, long userId) {
    ChatHistory chatHistory = new ChatHistory();
    chatHistory.setMessage(chatMessage);
    chatHistory.setSenderId(userId);

    chatHistoryRepository.save(chatHistory);
  }

}
