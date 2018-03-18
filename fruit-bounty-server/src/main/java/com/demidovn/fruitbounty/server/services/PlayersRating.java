package com.demidovn.fruitbounty.server.services;

import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperation;
import com.demidovn.fruitbounty.server.dto.operations.response.ResponseOperationType;
import com.demidovn.fruitbounty.server.dto.operations.response.UserInfo;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlayersRating {

  @Autowired
  private UserService userService;

  @Autowired
  private ConnectionService connectionService;

  @Autowired
  @Qualifier("serverConversionService")
  private ConversionService conversionService;

  @Getter
  private List<UserInfo> topRatedPlayers;

  @PostConstruct
  public void postConstruct() {
    updateTopRated();
  }

  public void notifyAllWithTopRated() {
    log.trace("notifyAllWithTopRated");

    updateTopRated();

    publicizeAllWithTopRated();
  }

  private void updateTopRated() {
    topRatedPlayers = userService.getTopRated()
      .stream()
      .map(user -> conversionService.convert(user, UserInfo.class))
      .collect(Collectors.toList());
  }

  private void publicizeAllWithTopRated() {
    ResponseOperation notification = new ResponseOperation(
      ResponseOperationType.RatingTable,
      topRatedPlayers);

    connectionService.sendToAll(notification);
  }

}
