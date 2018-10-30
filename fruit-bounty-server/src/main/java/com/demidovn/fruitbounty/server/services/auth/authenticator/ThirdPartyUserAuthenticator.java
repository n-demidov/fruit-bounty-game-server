package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.services.auth.AuthType;

public interface ThirdPartyUserAuthenticator {

  AuthType getAuthType();

  ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation);

}
