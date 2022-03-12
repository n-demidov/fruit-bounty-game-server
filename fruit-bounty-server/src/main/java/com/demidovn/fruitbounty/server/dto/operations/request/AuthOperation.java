package com.demidovn.fruitbounty.server.dto.operations.request;

import com.demidovn.fruitbounty.server.services.auth.AuthType;
import lombok.Data;

@Data
public class AuthOperation {

  AuthType type;
  long userId;
  String accessToken;
  String authKey;
  String device;

}
