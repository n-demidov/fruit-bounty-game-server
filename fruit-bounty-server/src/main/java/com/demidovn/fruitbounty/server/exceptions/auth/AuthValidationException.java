package com.demidovn.fruitbounty.server.exceptions.auth;

import com.demidovn.fruitbounty.server.exceptions.AbstractGameServerException;

public class AuthValidationException extends AbstractGameServerException {

  public AuthValidationException(String errorMessage) {
    super(errorMessage);
  }

}
