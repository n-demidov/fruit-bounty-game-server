package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

@Component
public class YandexThirdPartyUserAuthenticator implements ThirdPartyUserAuthenticator {

  @Override
  public AuthType getAuthType() {
    return AuthType.YANDEX;
  }

  @Override
  public ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation) {
    ThirdPartyAuthedUserInfo result = new ThirdPartyAuthedUserInfo();
    result.setThirdPartyType(AuthType.YANDEX.getStringRepresentation());
    result.setThirdPartyId(String.valueOf(authOperation.getUserId()));

    return result;
  }

}
