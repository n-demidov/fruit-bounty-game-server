package com.demidovn.fruitbounty.server;

public class MetricsConsts {

    public static class REQUEST {
        public static final String ALL_STAT = "REQUEST.PAGE.ALL";
        public static final String VK_STAT = "REQUEST.PAGE.VK";
        public static final String VK_MOBILE_STAT = "REQUEST.PAGE.VK-MOBILE";
        public static final String YANDEX_STAT = "REQUEST.PAGE.YANDEX";
        public static final String FACEBOOK_GET_STAT = "REQUEST.PAGE.FACEBOOK.GET";
        public static final String FACEBOOK_POST_STAT = "REQUEST.PAGE.FACEBOOK.POST";
    }

    public static class GAME {
        public static final String ALL_STAT = "GAME.START.ALL";
        public static final String BETWEEN_PLAYERS_STAT = "GAME.START.BETWEEN_PLAYERS";
        public static final String TUTORIAL_STAT = "GAME.START.TUTORIAL";
        public static final String WITH_BOT_STAT = "GAME.START.WITH_BOT";
    }

    public static class OTHER {
        public static final String CHAT_SENT_STAT = "CHAT.SENT";
    }

}
