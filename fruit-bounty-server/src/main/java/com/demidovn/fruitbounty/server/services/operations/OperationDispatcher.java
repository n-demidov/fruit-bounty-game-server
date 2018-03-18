package com.demidovn.fruitbounty.server.services.operations;

import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.exceptions.RequestOperationValidationException;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.operations.handlers.OperationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class OperationDispatcher {

  private static final String UNKNOWN_OPERATION_TYPE = "Unknown operation type: '%s'";
  private static final String NOT_AUTHED_OPERATION_TYPE = "Attempt of sending not authed operation: '%s'";

  @Resource(name = "operationDispatcherHandlers")
  private Map<OperationType, OperationHandler> operationDispatcherHandlers;

  @Autowired
  private ConnectionService connectionService;

  public void process(RequestOperation operation) {
    log.trace("process, operation={}", operation);

    try {
      validConnectionAuth(operation);
      validOperationType(operation);

      runHandler(operation);
      operation.getConnection().updateLastActionTime();
    } catch (RequestOperationValidationException e) {
      log.debug("Connection '{}' will close without any message to a client: {}",
              operation.getConnection().getId(), e.getMessage());
      connectionService.killConnection(operation.getConnection());
    }
  }

  private void validConnectionAuth(RequestOperation operation) {
    if (operation.getType() == OperationType.Auth) {
      return;
    }

    if (operation.getConnection().isAuthed()) {
      return;
    }

    throw new RequestOperationValidationException(String.format(NOT_AUTHED_OPERATION_TYPE, operation));
  }

  private void validOperationType(RequestOperation operation) {
    if (!operationDispatcherHandlers.containsKey(operation.getType())) {
      throw new RequestOperationValidationException(String.format(UNKNOWN_OPERATION_TYPE, operation));
    }
  }

  private void runHandler(RequestOperation requestOperation) {
    OperationHandler operationHandler = operationDispatcherHandlers.get(requestOperation.getType());
    operationHandler.process(requestOperation);
  }

}
