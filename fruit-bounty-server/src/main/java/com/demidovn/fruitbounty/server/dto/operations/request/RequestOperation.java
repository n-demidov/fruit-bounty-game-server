package com.demidovn.fruitbounty.server.dto.operations.request;

import com.demidovn.fruitbounty.server.entities.Connection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOperation {

  private OperationType type;
  private LinkedHashMap<String, String> data;
  private Connection connection;

}
