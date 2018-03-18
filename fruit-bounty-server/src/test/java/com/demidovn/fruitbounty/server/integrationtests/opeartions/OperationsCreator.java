package com.demidovn.fruitbounty.server.integrationtests.opeartions;

import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import org.springframework.stereotype.Component;

@Component
public class OperationsCreator {

  public AuthOperation getAuthOperation(int userId) {
    AuthOperation defaultAuthOperation = new AuthOperation();

    defaultAuthOperation.setAccessToken("accessToken");
    defaultAuthOperation.setType("integration-test");
    defaultAuthOperation.setUserId(userId);

    return defaultAuthOperation;
  }

}
