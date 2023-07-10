package com.demidovn.fruitbounty.server.services.promotion;

import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ScorePromoter {

  private static final String VK_QUERY_URL =
      "https://api.vk.com/method/secure.addAppEvent?user_id={user_id}&access_token={access_token}&activity_id={activity_id}&value={value}&v=5.131";

  @Value("${game-server.vk.access-token}")
  private String VK_SERVER_ACCESS_TOKEN;

  private static final RestTemplate restTemplate = new RestTemplate();

  public void notifyAboutScore(User user) {
    if (!AuthType.VK.getStringRepresentation().equals(user.getThirdPartyType())) {
      return;
    }

    try {
      Map<String, List<Map<String, Object>>> vkResponse = restTemplate.getForObject(
          VK_QUERY_URL,
          Map.class,
          user.getThirdPartyId(),
          VK_SERVER_ACCESS_TOKEN,
          2,
          user.getScore()
      );

      log.debug("notifyAboutScore done, vkResponse={}", vkResponse);
    } catch (Exception e) {
      log.error("error in notifyAboutScore, user={}", user, e);
    }
  }

}
