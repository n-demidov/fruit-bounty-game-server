package com.demidovn.fruitbounty.server.services.auth;

import com.demidovn.fruitbounty.server.AppConfigs;
import com.demidovn.fruitbounty.server.entities.Connection;
import com.demidovn.fruitbounty.server.exceptions.auth.AuthValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthAttemptsValidator {

  private static final String TOO_MUCH_AUTH_ATTEMPTS_ERR = "Too much authAttempts: %s!";

  public void valid(Connection connection) {
    int authAttempts = connection.getAuthAttempts().get();

    if (authAttempts > AppConfigs.MAX_AUTH_ATTEMPTS) {
      String errorMessage = String.format(TOO_MUCH_AUTH_ATTEMPTS_ERR, authAttempts);

      throw new AuthValidationException(errorMessage);
    }
  }

}
