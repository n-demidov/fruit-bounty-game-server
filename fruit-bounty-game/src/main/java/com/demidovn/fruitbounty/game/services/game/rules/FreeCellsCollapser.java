package com.demidovn.fruitbounty.game.services.game.rules;

import com.demidovn.fruitbounty.gameapi.model.Cell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FreeCellsCollapser {

  private static final CellsFinder cellsFinder = new CellsFinder();

  public Map<Long, List<Figure>> getCollapsedFigures(Cell[][] cells) {
    Set<Cell> freeCells = getFreeCells(cells);
    Map<Long, List<Figure>> figuresByPlayer = new HashMap<>();

    while (!freeCells.isEmpty()) {
      Cell cell = freeCells.iterator().next();
      Figure figure = new Figure();

      processCell(cell, figure, freeCells, cells);

      if (figure.players.size() > 1) {
        List<Figure> figures = figuresByPlayer.getOrDefault(0L, new ArrayList<>());
        figures.add(figure);
        figuresByPlayer.put(0L, figures);
      } else if (figure.players.size() == 1) {
        Long player = figure.players.iterator().next();
        List<Figure> figures = figuresByPlayer.getOrDefault(player, new ArrayList<>());
        figures.add(figure);
        figuresByPlayer.put(player, figures);
      } else {
        throw new IllegalStateException("figure.players.size() < 1");
      }
    }

    return figuresByPlayer;
  }

  public void collapseBoard(Cell[][] cells, Map<Long, List<Figure>> figuresByPlayer) {
    for (Entry<Long, List<Figure>> entry : figuresByPlayer.entrySet()) {
      Long playerId = entry.getKey();
      List<Figure> figures = entry.getValue();
      if (playerId == 0) {
        continue;
      }
      int playerCellType = cellsFinder.getOwnedCell(playerId, cells).getType();

      for (Figure figure : figures) {
        for (Cell cell : figure.cells) {
          cells[cell.getX()][cell.getY()].setOwner(playerId);
          cells[cell.getX()][cell.getY()].setType(playerCellType);
        }
      }
    }
  }

  private void processCell(Cell cell, Figure figure, Set<Cell> freeCells, Cell[][] cells) {
    if (!isCellFree(cell)) {
      figure.addPlayer(cell.getOwner());
    }

    if (!freeCells.contains(cell)) {
      return;
    }
    freeCells.remove(cell);

    if (isCellFree(cell)) {
      figure.add(cell);
    }

    List<Cell> neighborCells = cellsFinder.getNeighborCells(cells, cell.getX(), cell.getY());
    for (Cell neighborCell : neighborCells) {
      processCell(neighborCell, figure, freeCells, cells);
    }
  }

  private Set<Cell> getFreeCells(Cell[][] cells) {
    Set<Cell> freeCells = new HashSet<>();
    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];
        if (isCellFree(cell)) {
          freeCells.add(cell);
        }
      }
    }
    return freeCells;
  }

  private boolean isCellFree(Cell cell) {
    return cell.getOwner() == 0;
  }

  public static class Figure {
    private final List<Cell> cells = new ArrayList<>();
    private final Set<Long> players = new HashSet<>();

    public Set<Long> getPlayers() {
      return players;
    }

    public void addPlayer(long owner) {
      if (owner == 0) {
        throw new IllegalArgumentException("owner = 0");
      }
      players.add(owner);
    }

    public void add(Cell cell) {
      cells.add(cell);
    }
  }

}
