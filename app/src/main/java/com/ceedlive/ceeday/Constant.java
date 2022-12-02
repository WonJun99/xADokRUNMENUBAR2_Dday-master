package com.ceedlive.ceeday;

public class Constant {

    public static final int LOADING_DELAY_MILLIS = 3000;

    /**
     * 인텐트: 메인화면 리퀘스트 코드
     */
    public static final int REQUEST_CODE_MAIN_ACTIVITY = 10;

    public static final String SHARED_PREFERENCES_NAME = "ceedliveAppDday";

    public static final String SHARED_PREFERENCES_KEY_PREFIX = "ceedlive.dday";

    public static final String INTENT_DATA_NAME_SHARED_PREFERENCES = "sharedPreferencesDataKey";

    public static final String ACTION_INTENT_FILTER_NOTIFICATION_ON_START_COMMAND = "ddayitem";

    public static final String CALENDAR_STRING_FORMAT_SLASH = "%d/%d/%d";

    public static final String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

    public static class ADMOB {
        public static final String APP_ID = "ca-app-pub-3687086103436819~2067765843";
    }

    public static class INTENT {
        public static class REQUEST_CODE {
            public static final int MAIN_ACTIVITY = 10;
        }
        public static class EXTRA {
            public static class KEY {
                public static final String SQLITE_TABLE_CLT_DDAY_ROWID = "dday_id";
                public static final String SQLITE_TABLE_CLT_DDAY_ITEM = "ddayitem";
            }
        }
    }

    public static class SHARED_PREFERENCES {
        public static final String NAME = "ceedliveAppDday";
        public static final String KEY_PREFIX = "ceedlive.dday";
    }

    public static class SQLITE {
        public static final String DB_FILE_NAME = "dday.db";
        public static final int DB_VERSION = 1;
    }

    public static class SIMPLE_DATE_FORMAT {
        public static class PATTERN {
            public static final String YYYY년_MM월_DD일_E요일 = "yyyy년 MM월 dd일 E요일";
            public static final String YYYYMMDD = "yyyyMMdd";
        }
    }

    public static class NOTIFICATION {
        public static final int UNREGISTERED = 0;
        public static final int REGISTERED = 1;

        public static final boolean TO_BE_NOTIFIED = true;
        public static final boolean TO_BE_CANCELLED = false;
    }

    public static class FLOATING_ACTION_BUTTON {
        public static final boolean VISIBLE = true;
        public static final boolean INVISIBLE = false;
    }

    public static class DDAY {
        public static final int NEW = 0;
    }

    public static class DIRECTION {
        public static final int FORWARD = 1;
        public static final int REVERSE = 0;
    }

    public static class REGEX {
        public static final String SLASH = "/";
    }

}
