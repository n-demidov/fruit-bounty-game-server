package com.demidovn.fruitbounty.game.services;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Randomizer {
    private final Random random = new Random();

    public int generateRandomInt(int upperRange) {
        return random.nextInt(upperRange);
    }

    public int generateFromRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public <T> void shuffle(List<T> collection) {
        Collections.shuffle(collection);
    }
}
