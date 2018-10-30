package com.demidovn.fruitbounty.server.integrationtests.opeartions;

import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

@Component
public class OperationsCreator {

  public AuthOperation getAuthOperation(long userId, AuthType authType) {
    AuthOperation defaultAuthOperation = new AuthOperation();

    defaultAuthOperation.setAccessToken("accessToken");
    defaultAuthOperation.setType(authType);
    defaultAuthOperation.setUserId(userId);

    return defaultAuthOperation;
  }

}
