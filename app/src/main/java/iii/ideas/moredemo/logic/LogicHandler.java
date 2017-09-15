package iii.ideas.moredemo.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import iii.ideas.moredemo.Parameters;
import iii.ideas.moredemo.cmp.semantic.SemanticWordCMPParameters;
import iii.ideas.moredemo.init.InitCheckBoardParameters;
import iii.ideas.moredemo.spotify.SpotifyParameters;
import iii.ideas.moredemo.stream.WebMediaPlayerHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;


import iii.ideas.moredemo.spotify.SpotifyHandler;
import iii.ideas.moredemo.stream.WebMediaPlayerParameters;
import iii.ideas.moredemo.tts.TTSCache;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.tts.TextToSpeechHandler;
import sdk.ideas.tool.speech.voice.VoiceRecognition;


/**
 * Created by joe on 2017/7/26.
 */

public class LogicHandler extends BaseHandler
{
    private JSONObject mActivityJson = null;
    
    
    private VoiceRecognition mVoiceRecognition = null;
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    private TextToSpeechHandler mTextToSpeechHandler = null;
    private SpotifyHandler mSpotifyHandler = null;
    
    
    private Handler selfHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    
    private void handleMessages(Message msg)
    {
        
        switch (msg.what)
        {
            
            case WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER:
                handleMessageWebMediaPlayer(msg);
                break;
            
            case CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER:
                handleMessageTTS(msg);
                break;
            
            case CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER:
                handleMessageVoiceRecognition(msg);
                break;
            
            case SpotifyParameters.CLASS_SPOTIFY:
                handleMessageSpotify(msg);
                break;
            
            
            default:
                break;
        }
    }
    
    
    private void handleMessageWebMediaPlayer(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case WebMediaPlayerParameters.COMPLETE_PLAY:
                    mWebMediaPlayerHandler.stopPlayMediaStream();
                    // mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    break;
                case WebMediaPlayerParameters.START_PLAY:
                    
                    //callback to MainActivity to start display
                    
                    break;
                case WebMediaPlayerParameters.STOP_PLAY:
                    break;
                
                default:
                    break;
            }
        }
        else
        {
            //異常例外處理
            onError(Parameters.ID_SERVICE_IO_EXCEPTION);
        }
        
        
    }
    
    private void handleMessageVoiceRecognition(Message msg)
    {
        final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognition.stopListen();
            
            Logs.showTrace("[LogicHandler] Get voice Text: " + message.get("message"));
            
            if (!message.get("message").isEmpty())
            {
                //callback mainActivity
                HashMap<String, String> returnMessage = new HashMap<>();
                returnMessage.put("message", message.get("message"));
                callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC, LogicParameters.METHOD_VOICE, returnMessage);
                
                
            }
        }
        
        else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //startListen first handle
        }
        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
        {
            Logs.showTrace("get ERROR message: " + message.get("message"));
            mVoiceRecognition.stopListen();
            
            if (message.get("message").equals("No match") || message.get("message").equals("No speech input"))
            {
                //TTS again and listen again
                onError(Parameters.ID_SERVICE_UNKNOWN);
            }
            
        }
        else if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
        {
            onError(Parameters.ID_SERVICE_IO_EXCEPTION);
        }
        else
        {
            
        }
    }
    
    public void onError(String index)
    {
        endAll();
        switch (index)
        {
            case Parameters.ID_SERVICE_IO_EXCEPTION:
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_IO_EXCEPTION, Parameters.ID_SERVICE_IO_EXCEPTION);
                break;
            case Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED:
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_SPOTIFY_UNAUTHORIZED, Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED);
                break;
            default:
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
                break;
        }
    }
    
    private void handleMessageTTS(Message msg)
    {
        switch (msg.arg1)
        {
            case ResponseCode.ERR_SUCCESS:
                
                analysisTTSResponse((HashMap<String, String>) msg.obj);
                
                break;
            case ResponseCode.ERR_NOT_INIT:
                InitCheckBoardParameters.setTTSInitState(InitCheckBoardParameters.STATE_FAIL);
                Logs.showError("TTS not init success");
                break;
            case ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION:
                //InitCheckBoard.setTTSInit(false);
                //deal with not found Google TTS Exception
                InitCheckBoardParameters.setTTSInitState(InitCheckBoardParameters.STATE_FAIL);
                //mTextToSpeechHandler.downloadTTS();
                
                //deal with ACCESSIBILITY page can not open Exception
                //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(intent, 0);
                
                break;
            case ResponseCode.ERR_UNKNOWN:
                //  InitCheckBoard.setTTSInit(false);
                InitCheckBoardParameters.setTTSInitState(InitCheckBoardParameters.STATE_FAIL);
                break;
            default:
                break;
        }
        
    }
    
    private void handleMessageSpotify(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        Logs.showTrace("msg.arg2: " + String.valueOf(msg.arg2) + " message:" + message);
        if (msg.arg2 == SpotifyParameters.METHOD_INIT)
        {
            if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                Logs.showTrace("[LogicHandler] set Spotify INIT STATE SUCCESS");
                InitCheckBoardParameters.setSpotifyInitState(InitCheckBoardParameters.STATE_SUCCESS);
            }
            else
            {
                InitCheckBoardParameters.setSpotifyInitState(InitCheckBoardParameters.STATE_FAIL);
                Logs.showError("ERROR message" + message.get("message"));
            }
            
        }
        else
        {
            if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                
                
                if (message.get("message").equals("DONE"))
                {
                    //歌曲結束後
                    
                }
            }
            else
            {
                //異常例外處理
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_SPOTIFY_UNAUTHORIZED, Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED);
                
                
            }
        }
        
    }
    
    
    private void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            boolean textStatusStart = message.get("TextStatus").equals("START");
            if (textStatusDone)
            {
                switch (message.get("TextID"))
                {
                    case Parameters.ID_SERVICE_START_UP_GREETINGS:
                        mVoiceRecognition.startListen();
                        
                        break;
                    
                    
                    case Parameters.ID_SERVICE_FRIEND_RESPONSE:
                        
                        break;
                    
                    
                    case Parameters.ID_SERVICE_MUSIC_BEGIN:
                        
                        
                        break;
                    case Parameters.ID_SERVICE_STORY_BEGIN:
                        
                        mVoiceRecognition.startListen();
                        break;
                    case Parameters.ID_SERVICE_TTS_BEGIN:
                        //  mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        
                        break;
                    case Parameters.ID_SERVICE_UNKNOWN:
                        
                        
                        //callback to service something ERROR
                        
                        break;
                    case Parameters.ID_SERVICE_IO_EXCEPTION:
                        //  mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        break;
                    case Parameters.ID_SERVICE_INIT_SUCCESS:
                        Logs.showTrace("ID_SERVICE_INIT_SUCCESS");
                        //  mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        break;
                    case Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED:
                        //  mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    default:
                        break;
                    
                }
            }
            if (textStatusStart)
            {
                switch (message.get("TextID"))
                {
                    case Parameters.ID_SERVICE_START_UP_GREETINGS:
                        
                        
                        break;
                    default:
                        break;
                }
            }
        }
        else if (message.get("message").equals("init success"))
        {
            Logs.showTrace("[LogicHandler] set TTS INIT STATE SUCCESS");
            InitCheckBoardParameters.setTTSInitState(InitCheckBoardParameters.STATE_SUCCESS);
            TTSCache.setTTSHandlerInit(false);
            HashMap<String, String> ttsCache = TTSCache.getTTSCache();
            if (null != ttsCache)
            {
                mTextToSpeechHandler.textToSpeech(ttsCache.get("tts"), ttsCache.get("param"));
            }
        }
    }
    
    public LogicHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        
        mTextToSpeechHandler = new TextToSpeechHandler(mContext);
        mTextToSpeechHandler.setHandler(selfHandler);
        mTextToSpeechHandler.init();
        
        mVoiceRecognition = new VoiceRecognition(mContext);
        mVoiceRecognition.setHandler(selfHandler);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        
        mWebMediaPlayerHandler = new WebMediaPlayerHandler(mContext);
        mWebMediaPlayerHandler.setHandler(selfHandler);
        
        
        //init spotify
        mSpotifyHandler = new SpotifyHandler(mContext);
        mSpotifyHandler.setHandler(selfHandler);
        mSpotifyHandler.init();
        
        
    }
    
    public void startUp()
    {
        mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_START_UP_GREETINGS, Parameters.ID_SERVICE_START_UP_GREETINGS);
        
    }
    
    
    public void setActivityJson(@NonNull JSONObject activityJson)
    {
        mActivityJson = activityJson;
    }
    
    public void startActivity()
    {
        if (null != mActivityJson)
        {
            try
            {
                switch (mActivityJson.getInt(SemanticWordCMPParameters.STRING_JSON_KEY_TYPE))
                {
                    case SemanticWordCMPParameters.TYPE_RESPONSE_UNKNOWN:
                        //callback to mainActivity onERROR
                        
                        
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_LOCAL:
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_HOST) &&
                                mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_FILE))
                        {
                            mWebMediaPlayerHandler.setHostAndFilePath(mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_HOST),
                                    mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_FILE));
                            mWebMediaPlayerHandler.startPlayMediaStream();
                            
                        }
                        else
                        {
                            Logs.showError("[LogicHandler] ERROR while read Local Host OR File");
                        }
                        
                        
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_SPOTIFY:
                        
                        Logs.showTrace("[LogicHandler] in Spotify Activity: " + mActivityJson.toString());
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_ID))
                        {
                            mSpotifyHandler.playMusic(mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_ID));
                        }
                        else
                        {
                            onError(Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED);
                        }
                        
                        
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_TTS:
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_LANG) &&
                                mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_TTS))
                        {
                            Locale localeSet = null;
                            switch (mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_TTS))
                            {
                                case "zh":
                                    localeSet = Locale.TAIWAN;
                                    break;
                                case "en":
                                    localeSet = Locale.US;
                                    break;
                                default:
                                    localeSet = Locale.TAIWAN;
                                    break;
                            }
                            if (!mTextToSpeechHandler.getLocale().toString().equals(localeSet.toString()))
                            {
                                Logs.showTrace("[MainActivity] OLD getLocale():" + mTextToSpeechHandler.getLocale().toString());
                                mTextToSpeechHandler.setLocale(localeSet);
                                Logs.showTrace("[MainActivity] NEW getLocale():" + mTextToSpeechHandler.getLocale().toString());
                                TTSCache.setTTSHandlerInit(true);
                                mTextToSpeechHandler.init();
                            }
                            
                            if (TTSCache.getTTSHandlerInit())
                            {
                                TTSCache.setTTSCache(mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_TTS),
                                        Parameters.ID_SERVICE_TTS_BEGIN);
                            }
                            else
                            {
                                mTextToSpeechHandler.textToSpeech(mActivityJson.getString(SemanticWordCMPParameters.STRING_JSON_KEY_TTS),
                                        Parameters.ID_SERVICE_TTS_BEGIN);
                            }
                            
                        }
                        else
                        {
                            Logs.showError("[LogicHandler] ERROR while read TTS lang OR tts");
                        }
                        break;
                }
            }
            catch (JSONException e)
            {
                Logs.showError("[LogicHandler] ERROR:" + e.toString());
            }
        }
    }
    
    public void killAll()
    {
        endAll();
        if (null != mTextToSpeechHandler)
        {
            mTextToSpeechHandler.shutdown();
        }
        if (null != mSpotifyHandler)
        {
            mSpotifyHandler.closeSpotify();
        }
    }
    
    public void endAll()
    {
        
        
        if (null != mTextToSpeechHandler)
        {
            mTextToSpeechHandler.stop();
        }
        
        
        if (null != mSpotifyHandler)
        {
            mSpotifyHandler.pauseMusic();
        }
        
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.stopPlayMediaStream();
        }
        
        if (null != mVoiceRecognition)
        {
            mVoiceRecognition.stopListen();
        }
        
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (null != mSpotifyHandler)
        {
            mSpotifyHandler.onActivityResult(requestCode, resultCode, data);
        }
    }
    
}
