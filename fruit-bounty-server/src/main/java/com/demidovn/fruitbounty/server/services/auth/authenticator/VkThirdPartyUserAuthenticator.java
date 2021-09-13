package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthFailedException;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import com.demidovn.fruitbounty.server.services.hashing.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class VkThirdPartyUserAuthenticator implements ThirdPartyUserAuthenticator {

  private static final String VK_ACCESS_TOKEN_TEMPLATE = "%s_%s_%s";
  private static final String VK_QUERY_URL =
          "https://api.vk.com/method/users.get?user_id={user_id}&access_token={access_token}&v=5.131&fields=id,first_name,last_name,photo_50";

  private static final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Value("${game-server.vk.access-token}")
  private String VK_SERVER_ACCESS_TOKEN;

  @Value("${game-server.vk.application-id}")
  private String VK_APP_ID;

  @Value("${game-server.vk.secret-key}")
  private String VK_APP_SECRET_KEY;

  @Override
  public AuthType getAuthType() {
    return AuthType.VK;
  }

  @Override
  public ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation) {
    Map<String, List<Map<String, Object>>> vkResponse = restTemplate.getForObject(
            VK_QUERY_URL,
            Map.class,
            authOperation.getUserId(),
            VK_SERVER_ACCESS_TOKEN);

    log.trace("vkResponse={}", vkResponse);

    if (!isValidAuthKey(authOperation.getAuthKey(), authOperation.getUserId())) {
      log.debug("client's authKey not valid, authOperation={}", authOperation);
      throw new AuthFailedException();
    }

    return conversionService.convert(vkResponse, ThirdPartyAuthedUserInfo.class);
  }

  private boolean isValidAuthKey(String authKey, long userId) {
    String rawExpectedAuthKey = String.format(
            VK_ACCESS_TOKEN_TEMPLATE,
            VK_APP_ID,
            userId,
            VK_APP_SECRET_KEY);
    String expectedAuthKey = Hashing.getMd5Hash(rawExpectedAuthKey);

    return Objects.equals(authKey, expectedAuthKey);
  }

}
