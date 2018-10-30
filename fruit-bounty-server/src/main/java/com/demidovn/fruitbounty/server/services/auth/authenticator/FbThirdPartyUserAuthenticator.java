package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.dto.operations.thirdparties.FacebookAuthedUserInfo;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthFailedException;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FbThirdPartyUserAuthenticator implements ThirdPartyUserAuthenticator {

  private static final String FB_QUERY_URL =
      "https://graph.facebook.com/me?access_token={accessToken}&fields=id,first_name,last_name,picture";

  private static final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Override
  public AuthType getAuthType() {
    return AuthType.FB;
  }

  @Override
  public ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation) {
    FacebookAuthedUserInfo fbResponse = restTemplate.getForObject(
        FB_QUERY_URL,
        FacebookAuthedUserInfo.class,
        authOperation.getAccessToken());

    if (fbResponse.getId() != authOperation.getUserId()) {
      throw new AuthFailedException();
    }

    return conversionService.convert(fbResponse, ThirdPartyAuthedUserInfo.class);
  }

}
