package com.demidovn.fruitbounty.server.services.chat;

import com.demidovn.fruitbounty.server.AppConfigs;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatHubTest {

  private static final String PREFIX = "a-";

  private ChatHub chatHub = new ChatHub();

  @Test
  public void testMessagesLimit() {
    for (int i = 0; i < AppConfigs.CHAT_HUB_LIMIT * 2; i++) {
      String msg = PREFIX + i;
      chatHub.push(msg);
    }

    assertThat(chatHub.get().size()).isEqualTo(AppConfigs.CHAT_HUB_LIMIT);
    assertThat(chatHub.get().get(0)).isEqualTo("a-" + AppConfigs.CHAT_HUB_LIMIT);
    assertThat(chatHub.get().get(AppConfigs.CHAT_HUB_LIMIT - 1))
      .isEqualTo(PREFIX + (AppConfigs.CHAT_HUB_LIMIT * 2 - 1));
  }

}
