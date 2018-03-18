package com.demidovn.fruitbounty.server.services.operations.handlers;

import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.dto.operations.request.RequestOperation;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthValidationException;
import com.demidovn.fruitbounty.server.services.ConnectionService;
import com.demidovn.fruitbounty.server.services.auth.AuthAttemptsValidator;
import com.demidovn.fruitbounty.server.services.auth.AuthExecutor;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthOperationHandler implements OperationHandler {

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  private ExecutorService authExecutorService;

  @Autowired
  private ApplicationContext ctx;

  @Autowired
  private AuthAttemptsValidator authAttemptsValidator;

  @Override
  public OperationType getOperationType() {
    return OperationType.Auth;
  }

  @Override
  public void process(RequestOperation operation) {
    Connection connection = operation.getConnection();
    try {
      log.trace("process operation={}", operation);

      connection.getAuthAttempts().incrementAndGet();
      authAttemptsValidator.valid(connection);

      AuthExecutor authExecutor = ctx.getBean(AuthExecutor.class);
      authExecutor.setOperation(operation);

      authExecutorService.submit(authExecutor);
    } catch (AuthValidationException e) {
      log.warn("process: auth failed: {}", e.getMessage());
      connectionService.killConnection(connection);
    }
  }

}
