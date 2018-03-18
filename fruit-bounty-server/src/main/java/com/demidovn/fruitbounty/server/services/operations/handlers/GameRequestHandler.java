package com.demidovn.fruitbounty.server.services.operations.handlers;

import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.services.GameRequests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class GameRequestHandler implements OperationHandler {

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Autowired
  private GameRequests gameRequests;

  @Override
  public OperationType getOperationType() {
    return OperationType.GameRequest;
  }

  @Override
  public void process(RequestOperation requestOperation) {
    boolean isSubmit = conversionService.convert(requestOperation, Boolean.class);
    long userId = requestOperation.getConnection().getUserId();

    if (isSubmit) {
      gameRequests.userSubmitRequest(userId);
    } else {
      gameRequests.userCancelRequest(userId);
    }
  }

}
