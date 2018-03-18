package com.demidovn.fruitbounty.game.services.game.bot;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.services.list.ListExtractor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class BotNameGenerator {

  private static final String GAME_BOT_NAMES = "game.bot.names";
  private static final String GAME_BOT_SURNAMES = "game.bot.surnames";
  private static final String SPLIT_SYMBOL = ",";
  private static final String BOT_FULL_NAME_TEMPLATE = "%s %s";

  private final ListExtractor listExtractor = new ListExtractor();
  private List<String> names;
  private List<String> surnames;

  @PostConstruct
  public void postConstruct() {
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(GameOptions.CONFIG_PROPERTIES)) {
      Properties prop = new Properties();
      prop.load(input);

      String namesString = prop.getProperty(GAME_BOT_NAMES);
      names = Collections.unmodifiableList(Arrays.asList(namesString.split(SPLIT_SYMBOL)));

      String surnamesString = prop.getProperty(GAME_BOT_SURNAMES);
      surnames = Collections.unmodifiableList(Arrays.asList(surnamesString.split(SPLIT_SYMBOL)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getRandomName() {
    String name = listExtractor.getRandomValue(names).trim();
    String surname = listExtractor.getRandomValue(surnames).trim();

    return String.format(BOT_FULL_NAME_TEMPLATE, name, surname);
  }

}
