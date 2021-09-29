package com.demidovn.fruitbounty.game.services.game.bot;

import com.demidovn.fruitbounty.game.converters.bot.MoveActionConverter;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.Randomizer;
import com.demidovn.fruitbounty.game.services.game.rules.CellsFinder;
import com.demidovn.fruitbounty.game.services.game.rules.MoveCorrectness;
import com.demidovn.fruitbounty.gameapi.model.Cell;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.GameAction;
import com.demidovn.fruitbounty.gameapi.model.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BotMoveFinder {

  private static final String CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME = "Can't find any move for bot, game=%s";

  @Autowired
  private MoveActionConverter moveActionConverter;

  private final MoveCorrectness moveCorrectness = new MoveCorrectness();
  private final CellsFinder cellsFinder = new CellsFinder();
  private final Randomizer randomizer = new Randomizer();

  public Pair<Integer, Integer> findMove(Game game) {
    Cell[][] cells = game.getBoard().getCells();
    List<Cell> maybeCapturedCells = findMaybeCapturedCells(game);

    List<Cell> sortedMaybeCapturedCells;
    if (game.isTutorial() && randomizer.generateFromRange(1, 3) == 1) {
      sortedMaybeCapturedCells = maybeCapturedCells;
    } else {
      sortedMaybeCapturedCells = calculateSortedStatistics(maybeCapturedCells, cells);
    }

    for (Cell cell : sortedMaybeCapturedCells) {
      GameAction gameAction = moveActionConverter.convert2MoveAction(game, cell.getX(), cell.getY());
      if (moveCorrectness.isMoveValid(gameAction)) {
        return new Pair<>(cell.getX(), cell.getY());
      }
    }

    String err = String.format(CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME, game.toFullString());
    log.error(err);
    throw new IllegalStateException(err);
  }

  private List<Cell> findMaybeCapturedCells(Game game) {
    List<Cell> maybeCapturedCells = new ArrayList<>();
    Cell[][] cells = game.getBoard().getCells();
    Player player = game.getCurrentPlayer();

    for (int x = 0; x < cells.length; x++) {
      for (int y = 0; y < cells[x].length; y++) {
        Cell cell = cells[x][y];

        if (cell.getOwner() == 0 &&
          cellsFinder.isBordersWithPlayerCell(cell, cells, player.getId())) {
          maybeCapturedCells.add(cell);
        }
      }
    }

    return maybeCapturedCells;
  }

  private List<Cell> calculateSortedStatistics(List<Cell> maybeCapturedCells, Cell[][] cells) {
    Map<Integer, TypeStatistics> cellTypes = new HashMap<>();

    for (Cell cell : maybeCapturedCells) {
      TypeStatistics typeStatistics = cellTypes.computeIfAbsent(
        cell.getType(), cellType -> new TypeStatistics(cell.getX(), cell.getY()));

      addSameCells(cell, typeStatistics, cells);
    }

    return cellTypes
      .entrySet()
      .stream()
      .map(Entry::getValue)
      .sorted(Comparator.comparing(typeStatistics -> ((TypeStatistics) typeStatistics).getCost()).reversed())
      .map(typeStatistics -> typeStatistics.getCells().get(0))
      .collect(Collectors.toList());
  }

  private void addSameCells(Cell cell, TypeStatistics typeStatistics, Cell[][] cells) {
    typeStatistics.setCost(typeStatistics.getCost() + countCellCost(cell, cells));
    typeStatistics.getCells().add(cell);

    List<Cell> neighborCells = cellsFinder.getNeighborCells(cells, cell.getX(), cell.getY());

    for (Cell neighborCell : neighborCells) {
      if (neighborCell.getOwner() == 0 &&
        neighborCell.getType() == cell.getType() &&
        !typeStatistics.getCells().contains(neighborCell)) {
        addSameCells(neighborCell, typeStatistics, cells);
      }
    }
  }

  private int countCellCost(Cell cell, Cell[][] cells) {
    return countCoordinateCost(cell.getX(), cells.length - 1) +
           countCoordinateCost(cell.getY(), cells[0].length - 1);
  }

  private int countCoordinateCost(int coord, int maxIndex) {
    int maxIterationsCosts = maxIndex / 2;

    for (int i = 0; i < maxIterationsCosts; i++) {
      if (coord == i || coord == maxIndex - i) {
        return i * i * maxIndex;
      }
    }

    return maxIterationsCosts * maxIterationsCosts * maxIndex;
  }

}
