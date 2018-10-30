package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.request.AuthOperation;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import java.util.LinkedHashMap;

import com.demidovn.fruitbounty.server.services.auth.AuthType;
import org.springframework.stereotype.Component;

@Component
public class RequestOperation2AuthOperationConverter implements FruitServerConverter<RequestOperation, AuthOperation> {

  @Override
  public AuthOperation convert(RequestOperation requestOperation) {
    LinkedHashMap<String, String> data = requestOperation.getData();
    AuthOperation authOperation = new AuthOperation();

    authOperation.setType(AuthType.fromString(data.get("type")));
    authOperation.setUserId(Long.valueOf(data.get("userId")));
    authOperation.setAccessToken(data.get("accessToken"));
    authOperation.setAuthKey(data.get("authKey"));

    return authOperation;
  }

}
