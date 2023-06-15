package com.demidovn.fruitbounty.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameOptions {

  public static final String UNKNOWN_PERSON_IMG = "../img/components/unknown_user.png";
  public static final String TRAINER_IMG = "../img/components/trainer.png";
  public static final int SCORE_FOR_WIN_TUTORIAL_GAME = 1;

  public static final String CONFIG_PROPERTIES = "config.properties";
  public static final int BOARD_WIDTH = 12;
  public static final int BOARD_HEIGHT = BOARD_WIDTH;
  public static final int TUTORIAL_BOARD_WIDTH = 9;
  public static final int TUTORIAL_BOARD_HEIGHT = TUTORIAL_BOARD_WIDTH;

  public static final int CELL_TYPES_COUNT = 9;
  public static final int CELL_TYPES_MIN = 1;

  public static final String GAME_LOOP_SCHEDULE_DELAY = "30";
  public static final int TIME_PER_MOVE_MS = 1000 * 20;
  public static final int TUTORIAL_TIME_PER_MOVE_MS = 1000 * 60 * 3;
  public static final int MOVE_TIME_DELAY_CORRECTION = 800;
  public static final int MAX_GAME_MISSED_MOVES = 3;

  public static final List<Integer> ALL_CELL_TYPES;

  static {
    List<Integer> possibleCellTypes = new ArrayList<>();

    for (int i = GameOptions.CELL_TYPES_MIN; i <= GameOptions.CELL_TYPES_COUNT; i++) {
      possibleCellTypes.add(i);
    }

    ALL_CELL_TYPES = Collections.unmodifiableList(possibleCellTypes);
  }

}
