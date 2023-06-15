package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.gameapi.services.BotService;
import com.demidovn.fruitbounty.gameapi.services.GameEventsSubscriptions;
import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.services.game.subscriber.DefaultGameEventsSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameIntegrationConfig {

  @Autowired
  private void makeSubscription(GameEventsSubscriptions gameEventsSubscriptions,
      DefaultGameEventsSubscriber defaultGameEventsSubscriber) {
    gameEventsSubscriptions.subscribe(defaultGameEventsSubscriber);
  }

  @Autowired
  private void configureBotService(BotService botService) {
    botService.setLevel2BotScoreThreshold(AppConfigs.MIN_BOT_SCORE);
  }

}
