package com.demidovn.fruitbounty.game.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class GameProcessingContext {

  @Setter(AccessLevel.NONE)
  private boolean isGameChanged;

  public void markGameChanged() {
    isGameChanged = true;
  }

}
