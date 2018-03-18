package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.dto.operations.response.UserInfo;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class ClientNotifier {

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private ConnectionService connectionService;

  public void sendSelfUserInfo(User authedUser) {
    ResponseOperation responseOperation = new ResponseOperation(
      ResponseOperationType.UserInfo,
      conversionService.convert(authedUser, UserInfo.class));

    connectionService.send(authedUser.getId(), responseOperation);
  }

  public void sendSelfUserInfo(User authedUser, Connection connection) {
    UserInfo userInfo = conversionService.convert(authedUser, UserInfo.class);

    connectionService.send(connection, userInfo, ResponseOperationType.UserInfo);
  }

}
