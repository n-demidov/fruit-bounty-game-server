package com.demidovn.fruitbounty.server.dto.operations.response;

import lombok.Data;

@Data
public class ResponseOperation {

  private ResponseOperationType type;
  private Object data;

  public ResponseOperation(ResponseOperationType type, Object data) {
    this.type = type;
    this.data = data;
  }

}
