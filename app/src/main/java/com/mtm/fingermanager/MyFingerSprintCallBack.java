package com.mtm.fingermanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by wangpeng on 2016/9/9.
 */

@SuppressLint("NewApi")
public class MyFingerSprintCallBack extends FingerprintManagerCompat.AuthenticationCallback {
    private static final String TAG = "MyCallBack";
    FingerDialogFragment mFinger;
    View ivsuccess;
    Context mContext;
    FingerprintManagerCompat mfingerprintManagerCompat;


    public MyFingerSprintCallBack(FingerDialogFragment mFragment,
                                  Context context,
                                  FingerprintManagerCompat mFingerprintManager) {
        // TODO Auto-generated constructor stub
        mFinger = mFragment;
        mContext = context;
        mfingerprintManagerCompat = mFingerprintManager;
    }

    // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        handler.sendMessageDelayed(new Message(), 100 * 30);
        Toast.makeText(mContext, "验证错误次数过多   请点击   忘记密码？", Toast.LENGTH_SHORT).show();

    }

    // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, "指纹验证失败", Toast.LENGTH_SHORT).show();


        Log.d(TAG, "onAuthenticationFailed: " + "指纹验证失败");
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.d(TAG, "onAuthenticationHelp: " + helpString);
    }

    // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult
                                                  result) {
        Log.d(TAG, "result.getCryptoObject(): " + result.getCryptoObject().getCipher()+"");
        Log.d(TAG, "result.getSignature(): " + result.getCryptoObject().getSignature()+"");
        Log.d(TAG, "result.getMac(): " + result.getCryptoObject().getMac()+"");
        Toast.makeText(mContext, "指纹验证成功", Toast.LENGTH_SHORT).show();
        mFinger.dismiss();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            Log.e(TAG, "handleMessage: 重启指纹模块");
            mfingerprintManagerCompat.authenticate(null, 0, null, new MyFingerSprintCallBack(mFinger, mContext, mfingerprintManagerCompat), handler);
        }
    };

    public void release() {
        handler.removeCallbacks(null);
    }

    //提供给使用这个类的用于处理识别成功和失败的类的接口
    public interface Callback {

        void onAuthenticated();

        void onError();
    }


}