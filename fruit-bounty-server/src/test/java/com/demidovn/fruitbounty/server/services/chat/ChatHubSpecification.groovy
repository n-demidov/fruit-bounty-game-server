package com.demidovn.fruitbounty.server.services.chat

import com.demidovn.fruitbounty.server.AppConfigs
import spock.lang.Specification

class ChatHubSpecification extends Specification {

  static final PREFIX = "a-"

  ChatHub chatHub = new ChatHub()

  def "should store only limited number of messages"() {
    when:
      for (int i = 0; i < AppConfigs.CHAT_HUB_LIMIT * 2; i++) {
        def msg = PREFIX + i
        chatHub.push(msg)
      }

    then:
      chatHub.get().size() == AppConfigs.CHAT_HUB_LIMIT
      chatHub.get().get(0) == PREFIX + AppConfigs.CHAT_HUB_LIMIT
      chatHub.get().get(AppConfigs.CHAT_HUB_LIMIT - 1) ==
              PREFIX + (AppConfigs.CHAT_HUB_LIMIT * 2 - 1)
  }

}
