package com.demidovn.fruitbounty.server.dto.operations.request;

import lombok.Data;

@Data
public class AuthOperation {

  String type;
  long userId;
  String accessToken;

}
