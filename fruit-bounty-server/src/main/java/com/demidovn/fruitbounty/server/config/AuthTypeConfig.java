package com.demidovn.fruitbounty.server.config;

import com.demidovn.fruitbounty.server.services.auth.AuthType;
import com.demidovn.fruitbounty.server.services.auth.authenticator.ThirdPartyUserAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AuthTypeConfig {

  @Autowired
  private List<ThirdPartyUserAuthenticator> thirdPartyUserAuthenticators;

  @Bean(name = "thirdPartyUserAuthenticators")
  public Map<AuthType, ThirdPartyUserAuthenticator> thirdPartyUserAuthenticators() {
    return thirdPartyUserAuthenticators.stream()
            .collect(Collectors.toMap(ThirdPartyUserAuthenticator::getAuthType, Function.identity()));
  }

}
