package com.demidovn.fruitbounty.game.services.list;

import java.util.List;
import java.util.Random;

public class ListExtractor {

  private final Random rand = new Random();

  public <T> T extractRandomValue(List<T> extractingList) {
    int randomIndex = rand.nextInt(extractingList.size());
    return extractingList.remove(randomIndex);
  }

  public <T> T getRandomValue(List<T> values) {
    int randomIndex = rand.nextInt(values.size());
    return values.get(randomIndex);
  }

}
