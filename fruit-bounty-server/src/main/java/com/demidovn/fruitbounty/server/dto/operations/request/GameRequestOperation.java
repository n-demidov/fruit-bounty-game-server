package com.demidovn.fruitbounty.server.dto.operations.request;

import com.demidovn.fruitbounty.gameapi.model.Game;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameRequestOperation extends RequestOperation {

  private Game game;

  public GameRequestOperation(RequestOperation operation, Game game) {
    super(operation.getType(), operation.getData(), operation.getConnection());

    this.game = game;
  }

}
