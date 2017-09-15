package iii.ideas.moredemo;

import android.Manifest;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import iii.ideas.moredemo.alterdialog.AlertDialogHandler;
import iii.ideas.moredemo.cmp.CMPParameters;
import iii.ideas.moredemo.cmp.semantic.SemanticWordCMPHandler;
import iii.ideas.moredemo.cmp.semantic.SemanticWordCMPParameters;
import iii.ideas.moredemo.init.InitCheckBoardHandler;
import iii.ideas.moredemo.init.InitCheckBoardParameters;
import iii.ideas.moredemo.logic.LogicHandler;
import iii.ideas.moredemo.logic.LogicParameters;
import iii.ideas.moredemo.progressDialog.ProgressDialog;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.premisson.RuntimePermissionHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private RuntimePermissionHandler mRuntimePermissionHandler = null;
    
    private LogicHandler mLogicHandler = null;
    
    private ProgressDialog mProgressDialog = null;
    
    private InitCheckBoardHandler mInitCheckBoardHandler = null;
    
    private AlertDialogHandler mAlertDialogHandler = null;
    
    //Jugo server connect
    private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[MainActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            switch (msg.what)
            {
                case CtrlType.MSG_RESPONSE_PERMISSION_HANDLER:
                    
                    if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                    {
                        //start to init
                        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                        for (String key : message.keySet())
                        {
                            if (!message.get(key).equals("1"))
                            {
                                finish();
                            }
                        }
                        
                        Logs.showTrace("[MainActivity] END Permission Check");
                        
                        Logs.showTrace("[MainActivity] Start to init!");
                        init();
                    }
                    else
                    {
                        //if not permission, close app
                        Logs.showError("[MainActivity] not grand permission,end app!");
                        finish();
                    }
                    
                    
                    break;
                case CMPParameters.CLASS_CMP_SEMANTIC_WORD:
                    handleMessageSWCMP(msg);
                    break;
                
                case LogicParameters.CLASS_LOGIC:
                    
                    if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                    {
                        
                        if (msg.arg2 == LogicParameters.METHOD_VOICE)
                        {
                            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                            mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                                    SemanticWordCMPParameters.TYPE_REQUEST_UNKNOWN, message.get("message"));
                            
                        }
                        
                        
                    }
                    break;
                
                case InitCheckBoardParameters.CLASS_INIT_CHECK_BOARD:
                    if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                    {
                        mProgressDialog.dismiss();
                    }
                    else if (msg.arg1 == ResponseCode.ERR_UNKNOWN)
                    {
                        if (msg.arg2 == InitCheckBoardParameters.METHOD_SPOTIFY)
                        {
                            mProgressDialog.dismiss();
                            mAlertDialogHandler.setText("spotify error", "初始化錯誤", "Spotfity初始化錯誤", "退出", "", false);
                        }
                        else
                        {
                            mProgressDialog.dismiss();
                            mAlertDialogHandler.setText("spotify error", "初始化錯誤", "Google TTS 初始化錯誤", "退出", "", false);
                            
                        }
                        
                        
                    }
                    
                    
                    break;
                
            }
            
            
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<String> permissions = new ArrayList<>();
        
        permissions.add(Manifest.permission.RECORD_AUDIO);
        
        mRuntimePermissionHandler = new RuntimePermissionHandler(this, permissions);
        mRuntimePermissionHandler.setHandler(mHandler);
        mRuntimePermissionHandler.startRequestPermissions();
        
    }
    
    
    @Override
    protected void onDestroy()
    {
        if (null != mLogicHandler)
        {
            mLogicHandler.killAll();
        }
        super.onDestroy();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mRuntimePermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mLogicHandler.onActivityResult(requestCode, resultCode, data);
    }
    
    private void init()
    {
        mAlertDialogHandler = new AlertDialogHandler(this);
        mAlertDialogHandler.setHandler(mHandler);
        mAlertDialogHandler.init();
        
        
        mInitCheckBoardHandler = new InitCheckBoardHandler(this);
        mInitCheckBoardHandler.setHandler(mHandler);
        mInitCheckBoardHandler.init();
        
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setHandler(mHandler);
        mProgressDialog.init();
        mProgressDialog.show();
        
        mLogicHandler = new LogicHandler(this);
        mLogicHandler.setHandler(mHandler);
        mLogicHandler.init();
        
        mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
        mSemanticWordCMPHandler.setIPAndPort(Parameters.CMP_HOST_IP, Parameters.CMP_HOST_PORT);
        mSemanticWordCMPHandler.setHandler(mHandler);
        
        
        ConstraintLayout mConstraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        mConstraintLayout.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        Logs.showTrace("[MainActivity] view onClick!");
        if (null != mLogicHandler)
        {
            mLogicHandler.endAll();
            mLogicHandler.startUp();
        }
        
    }
    
    //from jugo server
    public void handleMessageSWCMP(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("Get Response from CMP_SEMANTIC_WORD");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            if (message.containsKey("message"))
            {
                analysisSemanticWord(message.get("message"));
            }
            else
            {
                mLogicHandler.onError(Parameters.ID_SERVICE_UNKNOWN);
            }
        }
        else
        {
            //異常例外處理
            Logs.showError("[MainActivity] ERROR while sending message to CMP Controller");
            
            //call logicHandler onERROR
            mLogicHandler.onError(Parameters.ID_SERVICE_UNKNOWN);
        }
        
    }
    
    
    public void analysisSemanticWord(String data)
    {
        try
        {
            JSONObject responseData = new JSONObject(data);
            
            
            if (responseData.has("activity"))
            {
                Logs.showTrace("[MainActivity] activity Data:" + responseData.getJSONObject("activity").toString());
                if (responseData.getJSONObject("activity").length() != 0)
                {
                    mLogicHandler.setActivityJson(responseData.getJSONObject("activity"));
                    mLogicHandler.startActivity();
                }
                else
                {
                    Logs.showError("[MainActivity] No Activity Data!!");
                }
                
            }
            else
            {
                Logs.showError("[MainActivity] No Activity Data!!");
                
            }
        }
        catch (JSONException e)
        {
            mLogicHandler.onError(Parameters.ID_SERVICE_IO_EXCEPTION);
            Logs.showError("[MainActivity] analysisSemanticWord Exception:" + e.toString());
        }
        
    }
    
    private void handleMessageInitCheckBoard(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("[MainActivity] InitCheckBoard INIT SUCCESSFUL!");
            
        }
        else
        {
            if (msg.arg1 == ResponseCode.ERR_NOT_INIT)
            {
                HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                switch (message.get("message"))
                {
                    case "Spotify not init":
                        
                        break;
                    case "TTS not init":
                        
                        break;
                    
                }
            }
            
            
        }
    }
    
}
