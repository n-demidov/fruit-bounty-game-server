package com.demidovn.fruitbounty.gameapi.model;

import lombok.Data;

@Data
public class Board {

  private Cell[][] cells;
  private int allCellsNum;

  public Board(Cell[][] cells) {
    this.cells = cells;
    this.allCellsNum = findAllCellsNum();
  }

  public int findAllCellsNum() {
    int result = 0;

    for (int i = 0; i < cells.length; i++) {
      Cell[] row = cells[i];
      for (int j = 0; j < row.length; j++) {
        result++;
      }
    }

    return result;
  }

}
