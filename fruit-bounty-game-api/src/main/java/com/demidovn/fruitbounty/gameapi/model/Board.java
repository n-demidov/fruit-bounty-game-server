package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;

@Data
public class Board {

  private Cell[][] cells;

  public Board(Cell[][] cells) {
    this.cells = cells;
  }

}
