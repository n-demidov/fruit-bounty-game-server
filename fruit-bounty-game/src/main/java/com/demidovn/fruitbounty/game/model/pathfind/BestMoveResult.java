package com.demidovn.fruitbounty.game.model.pathfind;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import lombok.Data;

@Data
public class BestMoveResult {

  private Cell move;
  private Game resultedGame;

}
