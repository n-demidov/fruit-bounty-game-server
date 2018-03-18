package com.demidovn.fruitbounty.game.services.game.bot;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TypeStatistics {

  private int x;
  private int y;
  private int cost;
  private List<Cell> cells = new ArrayList<>();

  public TypeStatistics(int x, int y) {
    this.x = x;
    this.y = y;
  }

}
