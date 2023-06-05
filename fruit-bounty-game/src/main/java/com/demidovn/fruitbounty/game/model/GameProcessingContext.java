package com.demidovn.fruitbounty.game.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Data
public class GameProcessingContext {

  @Setter(AccessLevel.NONE)
  private boolean isGameChanged;

  private boolean collapseBoard = false;

  public GameProcessingContext(boolean collapseBoard) {
    this.collapseBoard = collapseBoard;
  }

  public void markGameChanged() {
    isGameChanged = true;
  }

}
