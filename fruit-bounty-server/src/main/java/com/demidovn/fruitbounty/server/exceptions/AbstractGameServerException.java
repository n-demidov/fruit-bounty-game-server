package com.demidovn.fruitbounty.server.exceptions;

public class AbstractGameServerException extends RuntimeException {

  public AbstractGameServerException() {
    super();
  }

  public AbstractGameServerException(String message) {
    super(message);
  }

  public AbstractGameServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public AbstractGameServerException(Throwable cause) {
    super(cause);
  }

}
