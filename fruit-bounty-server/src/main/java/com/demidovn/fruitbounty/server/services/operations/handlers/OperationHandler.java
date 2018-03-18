package com.demidovn.fruitbounty.server.services.operations.handlers;

import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;

public interface OperationHandler {

  OperationType getOperationType();

  void process(RequestOperation requestOperation);

}
