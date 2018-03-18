package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.server.dto.operations.request.OperationType;
import com.demidovn.fruitbounty.server.services.operations.handlers.OperationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class OperationDispatcherConfig {

  @Autowired
  private List<OperationHandler> operationHandlers;

  @Bean(name = "operationDispatcherHandlers")
  public Map<OperationType, OperationHandler> operationDispatcherHandlers() {
    return operationHandlers.stream()
            .collect(Collectors.toMap(OperationHandler::getOperationType, Function.identity()));
  }

}
