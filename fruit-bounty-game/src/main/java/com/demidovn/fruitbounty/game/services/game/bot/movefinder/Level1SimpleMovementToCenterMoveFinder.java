package com.demidovn.fruitbounty.game.services.game.bot.movefinder;

import com.demidovn.fruitbounty.game.converters.bot.MoveActionConverter;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.Randomizer;
import com.demidovn.fruitbounty.game.services.game.bot.TypeStatistics;
import com.demidovn.fruitbounty.game.services.game.bot.boardrate.MovementToCenterBoardRater;
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

@Slf4j
public class Level1SimpleMovementToCenterMoveFinder {

  private static final String CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME = "Can't find any move for bot, game=%s";

  private static final MoveActionConverter moveActionConverter = new MoveActionConverter();
  private static final MoveCorrectness moveCorrectness = new MoveCorrectness();
  private static final CellsFinder cellsFinder = new CellsFinder();
  private static final Randomizer randomizer = new Randomizer();
  private static final MovementToCenterBoardRater movementToCenterBoardRater = new MovementToCenterBoardRater();

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

    String errMsg = String.format(CAN_NOT_FIND_ANY_MOVE_FOR_BOT_GAME, game.toFullString());
    log.error(errMsg);
    throw new IllegalStateException(errMsg);
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
    typeStatistics.setCost(typeStatistics.getCost() + movementToCenterBoardRater.rateCell(cell, cells));
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

}
