package com.demidovn.fruitbounty.server;

public interface AppConfigs {

  boolean isDev = true;

  String CONNECT_WEBSOCKET_URL = "/connect-app";
  String WS_FROM_USER_QUEUE_NAME = "/from_client";
  String WS_TOPIC_NAME = "/topic/broadcast";
  String WS_USER_QUEUE_NAME = "/queue/to_client";
  String WS_USER_ERRORS_QUEUE_NAME = "/queue/errors";

  int AUTH_THREAD_POOL_CORE = 4;
  int AUTH_THREAD_POOL_MAX = 10;
  long AUTH_THREAD_POOL_THREAD_TTL = 60 * 5;
  int AUTH_THREAD_POOL_QUEUE_SIZE = 200;

  int GAME_NOTIFIER_THREAD_POOL_CORE = 4;
  int GAME_NOTIFIER_THREAD_POOL_MAX = 12;
  long GAME_NOTIFIER_THREAD_POOL_THREAD_TTL = 60 * 10;
  int GAME_NOTIFIER_THREAD_POOL_QUEUE_SIZE = 500;

  int MAX_AUTH_ATTEMPTS = 1;
  int CHAT_HUB_LIMIT = 50;

  int INITIAL_USER_SCORE = 700;
  int MIN_USER_SCORE = 500;
  int RATING_TABLE_PLAYERS_COUNT = 100;
  int RATING_TABLE_MIN_PLAYERS_RATING = 600;
  int MIN_RATING_WITHOUT_BOT = 850;

  int MAX_GAME_REQUEST_ITERATIONS_BEFORE_BOT_PLAY = 5;
  int MIN_BOT_SCORE = MIN_USER_SCORE;
  int MAX_BOT_SCORE = RATING_TABLE_MIN_PLAYERS_RATING;

  // It is necessary because there is a limit of rows for my free DB account.
  int DELETTING_USERS_COUNT_LIMIT_TO_CLEAR_DB = 8000;

}
