package com.demidovn.fruitbounty.game.services.game.bot;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.converters.bot.MoveActionConverter;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.Randomizer;
import com.demidovn.fruitbounty.game.services.game.bot.level.BotMover;
import com.demidovn.fruitbounty.game.services.game.bot.level.L1BotMover;
import com.demidovn.fruitbounty.game.services.game.bot.level.L2BotMover;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBotService implements BotService {

  private static final int BOT_WAITING_MOVE_TIME = 1300;
  private static final int MAX_BOT_WINS = 10;
  private static final int MIN_BOT_DEFEATS = 20;
  private static final int MAX_BOT_DEFEATS = 100;

  private static final String TRAINER_NAME = "Trainer";
  private static final int TRAINER_SCORE = 2000;
  private static final int TRAINER_WINS = 10_000;
  private static final int TRAINER_DEFEATS = 1_000;
  private static final int TRAINER_DRAWS = 100;

  private final Random rand = new Random();
  private final MoveActionConverter moveActionConverter = new MoveActionConverter();
  private final Randomizer randomizer = new Randomizer();

  @Autowired
  private BotNameGenerator botNameGenerator;

  private Integer level2BotScoreThreshold;
  private final Map<Long, BotMover> botMoversById = new HashMap<>();
  private final Map<Long, Pair<Integer, Integer>> botScoreById = new HashMap<>();

  @PostConstruct
  public void init() {
    botMoversById.put(-1L, new L1BotMover());
    botMoversById.put(-2L, new L2BotMover(0));
    botMoversById.put(-3L, new L2BotMover(2));
    botMoversById.put(-4L, new L2BotMover(4));

    botScoreById.put(-1L, new Pair<>(1, 99));
    botScoreById.put(-2L, new Pair<>(100, 199));
    botScoreById.put(-3L, new Pair<>(200, 499));
    botScoreById.put(-4L, new Pair<>(600, 899));
  }

  @Override
  public void setLevel2BotScoreThreshold(int minBotScore) {
    this.level2BotScoreThreshold = minBotScore;
  }

  @Override
  public Player createNewBot(int playerScore) {
    Player bot = new Player();

    long botId = getBotId(playerScore);
    bot.setId(botId);

    int botScore = randomizer.generateFromRange(botScoreById.get(botId).getKey(), botScoreById.get(botId).getValue());
    bot.setScore(botScore);

    bot.setImg(GameOptions.UNKNOWN_PERSON_IMG);
    bot.setPublicName(botNameGenerator.getRandomName());

    bot.setWins(rand.nextInt(MAX_BOT_WINS));
    bot.setDefeats(rand.nextInt(MAX_BOT_DEFEATS - MIN_BOT_DEFEATS) + MIN_BOT_DEFEATS);

    return bot;
  }

  @Override
  public Player createTrainer() {
    Player bot = createNewBot(0);

    bot.setImg(GameOptions.TRAINER_IMG);

    bot.setPublicName(TRAINER_NAME);

    bot.setWins(TRAINER_WINS);
    bot.setDefeats(TRAINER_DEFEATS);
    bot.setDraws(TRAINER_DRAWS);

    return bot;
  }

  @Override
  public boolean isPlayerBot(Player player) {
    return player.getId() < 0;
  }

  @Override
  public void actionIfBot(Game game) {
    Player currentPlayer = game.getCurrentPlayer();
    if (game.isFinished() ||
      !isPlayerBot(currentPlayer) ||
      !isWaitEnoughTime(game)) {
      return;
    }

    long id = currentPlayer.getId();
    BotMover botMover = botMoversById.get(id);
    Pair<Integer, Integer> generatedMove = botMover.findMove(game);

    game.getGameActions().add(
      moveActionConverter.convert2MoveAction(game, generatedMove.getKey(), generatedMove.getValue()));
  }

  private Integer getLevel2BotScoreThreshold() {
    if (level2BotScoreThreshold == null) {
      throw new IllegalStateException("getLevel2BotScoreThreshold should be initialized first");
    }

    return level2BotScoreThreshold;
  }

  private boolean isWaitEnoughTime(Game game) {
    return Instant.now().toEpochMilli() - game.getCurrentMoveStarted() > BOT_WAITING_MOVE_TIME;
  }

  private long getBotId(int playerScore) {
    if (playerScore <= 10) {
      return -1;
    } else if (playerScore <= 40) {
      return randomizer.generateFromRange(1, 2) * -1;
    } else if (playerScore <= 100) {
      return randomizer.generateFromRange(1, 3) * -1;
    } else {
      return randomizer.generateFromRange(1, 4) * -1;
    }
  }

}
