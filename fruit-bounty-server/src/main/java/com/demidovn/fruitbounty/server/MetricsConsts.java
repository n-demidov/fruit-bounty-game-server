package com.demidovn.fruitbounty.server;

public class MetricsConsts {

    public static class MATCH3 {
        public static class REQUEST {
            public static final String ALL_STAT = "MATCH3.REQUEST.PAGE.ALL";
        }
    }

    public static class REQUEST {
        public static final String ALL_STAT = "REQUEST.PAGE.ALL";
        public static final String VK_STAT = "REQUEST.PAGE.VK";
        public static final String VK_MOBILE_STAT = "REQUEST.PAGE.VK-MOBILE";
        public static final String YANDEX_STAT = "REQUEST.PAGE.YANDEX";
        public static final String SB_STAT = "REQUEST.PAGE.SB";
        public static final String FACEBOOK_GET_STAT = "REQUEST.PAGE.FACEBOOK.GET";
        public static final String FACEBOOK_POST_STAT = "REQUEST.PAGE.FACEBOOK.POST";
    }

    public static class GAME {
        public static final String ALL_STAT = "GAME.START.ALL";
        public static final String BETWEEN_PLAYERS_STAT = "GAME.START.BETWEEN_PLAYERS";
        public static final String TUTORIAL_STAT = "GAME.START.TUTORIAL";
        public static final String WITH_BOT_STAT = "GAME.START.WITH_BOT";
        public static final String END_TUTORIAL_WITH_WIN = "GAME.END.TUTORIAL.WITH_WIN";
    }

    public static class AUTH {
        public static final String ALL_TRIES_STAT = "AUTH.ALL_TRIES";
        public static final String SUCCESS_ALL_STAT = "AUTH.SUCCESS.ALL";
        public static final String SUCCESS_BY_TYPE_STAT = "AUTH.SUCCESS.";
        public static final String ERRORS_STAT = "AUTH.ERRORS";
        public static final String DEVICE_STAT = "AUTH.DEVICE.";
    }

    public static class SERVER {
        public static final String UPTIME_MINUTES_STAT = "SERVER.UPTIME_MINUTES";
    }

    public static class OTHER {
        public static final String CHAT_SENT_STAT = "CHAT.SENT";
        public static final String PLAYER_RENAME_TRY_STAT = "PLAYER.RENAME.TRY";
        public static final String PLAYER_RENAME_SUCCESS_STAT = "PLAYER.RENAME.SUCCESS";
        public static final String PLAYER_RENAME_NOT_CHANGED_STAT = "PLAYER.RENAME.NOT_CHANGED";
    }

}
