package com.demidovn.fruitbounty.gameapi.model;

public enum GameActionType {

  Move, Surrender;

  public static boolean contains(String value) {
    for (GameActionType gameActionType : GameActionType.values()) {
      if (gameActionType.name().equals(value)) {
        return true;
      }
    }

    return false;
  }

}
