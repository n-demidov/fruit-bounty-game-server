package com.demidovn.fruitbounty.server.services.auth.authenticator;

import com.demidovn.fruitbounty.server.dto.operations.ThirdPartyAuthedUserInfo;
import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;

public interface ThirdPartyUserAuthenticator {

  ThirdPartyAuthedUserInfo authenticate(AuthOperation authOperation);

}
