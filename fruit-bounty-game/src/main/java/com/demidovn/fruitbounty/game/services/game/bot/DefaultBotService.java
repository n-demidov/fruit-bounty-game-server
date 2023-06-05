package com.demidovn.fruitbounty.game.services.game.bot;

import com.demidovn.fruitbounty.game.GameOptions;
import com.demidovn.fruitbounty.game.converters.bot.MoveActionConverter;
import com.demidovn.fruitbounty.game.model.Pair;
import com.demidovn.fruitbounty.game.services.game.bot.movefinder.Level2ThresholdDeepMoveFinder;
import com.demidovn.fruitbounty.game.services.game.bot.movefinder.Level1SimpleMovementToCenterMoveFinder;
import com.demidovn.fruitbounty.gameapi.model.Game;
import com.demidovn.fruitbounty.gameapi.model.Player;
import com.demidovn.fruitbounty.gameapi.services.BotService;
import java.time.Instant;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBotService implements BotService {

  private static final int BOT_ID = -2000;
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

  @Autowired
  private BotNameGenerator botNameGenerator;

  private static final Level1SimpleMovementToCenterMoveFinder l1MoveFinder = new Level1SimpleMovementToCenterMoveFinder();
  private static final Level2ThresholdDeepMoveFinder l2MoveFinder = new Level2ThresholdDeepMoveFinder();

  private Integer level2BotScoreThreshold;

  @Override
  public void setLevel2BotScoreThreshold(int minBotScore) {
    this.level2BotScoreThreshold = minBotScore;
  }

  @Override
  public Player createNewBot(int botScore) {
    Player bot = new Player();

    bot.setId(BOT_ID);
    bot.setImg(GameOptions.UNKNOWN_PERSON_IMG);

    bot.setPublicName(botNameGenerator.getRandomName());
    bot.setScore(botScore);

    bot.setWins(rand.nextInt(MAX_BOT_WINS));
    bot.setDefeats(rand.nextInt(MAX_BOT_DEFEATS - MIN_BOT_DEFEATS) + MIN_BOT_DEFEATS);

    return bot;
  }

  @Override
  public Player createTrainer() {
    Player bot = createNewBot(TRAINER_SCORE);

    bot.setImg(GameOptions.TRAINER_IMG);

    bot.setPublicName(TRAINER_NAME);

    bot.setWins(TRAINER_WINS);
    bot.setDefeats(TRAINER_DEFEATS);
    bot.setDraws(TRAINER_DRAWS);

    return bot;
  }

  @Override
  public boolean isPlayerBot(Player player) {
    return player.getId() == BOT_ID;
  }

  @Override
  public void actionIfBot(Game game) {
    if (game.isFinished() ||
      !isPlayerBot(game.getCurrentPlayer()) ||
      !isWaitEnoughTime(game)) {
      return;
    }

    Pair<Integer, Integer> generatedMove;
    int botScore = game.getCurrentPlayer().getScore();
    if (botScore >= getLevel2BotScoreThreshold()) {
      generatedMove = l2MoveFinder.findBestMove(game.getCurrentPlayer(), game);
    } else {
      generatedMove = l1MoveFinder.findMove(game);
    }

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

}
