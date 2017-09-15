package iii.ideas.moredemo;

/**
 * Created by joe on 2017/9/11.
 */

public abstract class Parameters
{
    public static final String STRING_SERVICE_START_UP_GREETINGS = "您好，需要什麼服務?";
    public static final String ID_SERVICE_START_UP_GREETINGS = "1dfdabd7-cbc8-4432-a407-11a1d9494b6c";
    
    public static final String STRING_SERVICE_UNKNOWN = "我不太清楚您在講什麼";
    
    public static final String ID_SERVICE_UNKNOWN = "281fd4bc-b13d-4431-bf20-6c6bff4cbac2";
    
    public static final String STRING_SERVICE_SPOTIFY_UNAUTHORIZED = "Spotify未授權此歌手";
    
    public static final String ID_SERVICE_SPOTIFY_UNAUTHORIZED = "281fd4bc-b13d-4431-bf20-3c3bff4cbac2";
    
    public static final String STRING_SERVICE_IO_EXCEPTION = "目前連線有些問題";
    public static final String ID_SERVICE_IO_EXCEPTION = "281fd4bc-b13d-4431-ff20-6c6bfa4cbcc1";
    
    public static final String STRING_SERVICE_INIT_SUCCESS = "初始化已成功，開始接收指令";
    public static final String ID_SERVICE_INIT_SUCCESS = "d3515c80-14ed-4379-43ad-bd98-df97eed73491";
    
    
    public static final String ID_SERVICE_TTS_BEGIN = "d8762071-4379-43ad-bd98-df97eed73491";
    
    public static final String ID_SERVICE_MUSIC_BEGIN = "8feefaaa-14ed-441e-85e5-e9c9e378a77f";
    
    public static final String ID_SERVICE_START_UP_GREETINGS_GAME_MODE = "f129cecf-0f0c-4f8d-b636-94b691fd6a42";
    public static final String STRING_SERVICE_START_UP_GREETINGS_GAME_MODE = "切換至遊戲模式";
    public static final String ID_SERVICE_GAME_BEGIN = "d3515c80-e737-454e-899e-9e0af2010b98";
    public static final String STRING_SERVICE_GAME_BEGIN = "要玩啥遊戲阿";
    
    public static final String ID_SERVICE_START_UP_GREETINGS_FRIEND_MODE = "da46a525-fd50-4349-983f-af9b02c22097";
    public static final String STRING_SERVICE_START_UP_GREETINGS_FRIEND_MODE = "切換至交友模式";
    public static final String ID_SERVICE_FRIEND_BEGIN = "d3515c80-e737-454e-899e-9e0af2010b98";
    public static final String STRING_SERVICE_FRIEND_BEGIN = "要交啥朋友阿";
    
    public static final String ID_SERVICE_FRIEND_RESPONSE = "d3515c80-e737-454e-811e-9e0af2010b98";
    
    public static final String ID_SERVICE_START_UP_GREETINGS_STORY_MODE = "9f0a1ece-775a-4bb2-a8a6-daacc6c5be66";
    public static final String STRING_SERVICE_START_UP_GREETINGS_STORY_MODE = "切換至故事模式";
    public static final String ID_SERVICE_STORY_BEGIN = "d3515c80-e737-454e-899e-9e0af2010b98";
    public static final String STRING_SERVICE_STORY_BEGIN = "要聽什麼故事阿";
    
    
    public static final String CMP_HOST_IP = "175.98.119.121";
    public static final int CMP_HOST_PORT = 2310;
    
    public static final String DEFAULT_DEVICE_ID = "dhbf0qX7T9";
    public static final String DMP_HOST_IP = "203.66.168.239";
    public static final int DMP_HOST_PORT = 5377;
    
    
    // for HTC10 1e-10f;  //for sampo 1e-25f;
    public static final float MEDIA_PLAYED_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//// for HTC10 1e-15f;
    public static final float DEFAULT_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//1e-11f;
    
    public static final String IDEAS_SPHINX_KEY_WORD = "hi ideas";
    
    public static final int MESSAGE_END_WELCOME_LAYOUT = 1232;
    
    
    public static final String ALERT_DIALOG_WRITE_PERMISSION = "b173de1a-7666-4dfd-8f96-7e8bae023636";
    public static final String ALERT_DIALOG_CONNECTING_DEVICE = "c4b008ba-8b2f-404e-9e44-dd0605486441";
    public static final String ALERT_DIALOG_ENTER_DEVICE_ID = "c4b008ba-8b2f-404e-9e44-dd0605486226";
    public static final String ALERT_DIALOG_CONFIRM_CONNECT_DEVICE = "c4b008ca-8b2f-404e-9e44-dd0605482229";
    
    public static final int MODE_NOT_CONNECT_DEVICE = -1;
    public static final int MODE_CONNECT_DEVICE = 1;
    public static final int MODE_UNKNOWN_DEVICE = 0;
    private static int modeFlag = MODE_UNKNOWN_DEVICE;
    
    public static void setModeFlag(int flag)
    {
        modeFlag = flag;
    }
    
    public static int getModeFlag()
    {
        return modeFlag;
    }
    
    
    
    
}
