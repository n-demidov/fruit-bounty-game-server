package com.demidovn.fruitbounty.server.converters;

import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.server.persistence.entities.User;
import com.demidovn.fruitbounty.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserId2PlayerConverter implements FruitServerConverter<Long, Player> {

  @Autowired
  private UserService userService;

  @Override
  public Player convert(Long userId) {
    User user = userService.get(userId);

    Player player = new Player();

    player.setId(user.getId());
    player.setPublicName(user.getPublicName());
    player.setScore(user.getScore());

    player.setWins(user.getWins());
    player.setDefeats(user.getDefeats());
    player.setDraws(user.getDraws());
    player.setImg(user.getImg());

    return player;
  }

}
