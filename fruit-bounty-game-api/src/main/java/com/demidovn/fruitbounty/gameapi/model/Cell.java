package com.demidovn.fruitbounty.gameapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Cell {

  private long owner;
  private int type;

  @JsonIgnore
  private final int x, y;

  public Cell(int type, int x, int y) {
    this.type = type;
    this.x = x;
    this.y = y;
  }

}
