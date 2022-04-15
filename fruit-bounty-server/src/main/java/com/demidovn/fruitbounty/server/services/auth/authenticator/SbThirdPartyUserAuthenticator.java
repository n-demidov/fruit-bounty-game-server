package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

@Component
public class SbThirdPartyUserAuthenticator implements ThirdPartyUserAuthenticator {

  @Override
  public AuthType getAuthType() {
    return AuthType.SB;
  }

  @Override
  public ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation) {
    //todo: like yandex. Need to generalize it would equals
    ThirdPartyAuthedUserInfo result = new ThirdPartyAuthedUserInfo();
    result.setThirdPartyType(AuthType.SB.getStringRepresentation());
    result.setThirdPartyId(String.valueOf(authOperation.getUserId()));

    return result;
  }

}
