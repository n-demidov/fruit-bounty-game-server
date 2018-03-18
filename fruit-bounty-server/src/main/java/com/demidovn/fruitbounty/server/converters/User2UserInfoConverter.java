package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.server.dto.operations.response.UserInfo;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import org.springframework.stereotype.Component;

@Component
public class User2UserInfoConverter implements FruitServerConverter<User, UserInfo> {

  @Override
  public UserInfo convert(User user) {
    return new UserInfo(user.getId(), user.getPublicName(), user.getImg(),
      user.getScore(), user.getWins(), user.getDefeats(), user.getDraws());
  }

}
