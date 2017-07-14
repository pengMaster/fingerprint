package com.mtm.fingermanager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    FingerDialogFragment mFragment;
    FingerprintManagerCompat mFingerprintManager;
    private String DIALOG_FRAGMENT_TAG = "myFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chooseFinger();
    }

    /**
     * 指纹锁
     *
     * @param view
     */
    public void FingerprintPassword(View view) {
        //用API版本23  限定指纹识别设备
        if (Integer.valueOf(Build.VERSION.SDK) >= 23) {
            Intent mIntent = new Intent();
            ComponentName comp = new ComponentName("com.android.settings",
                    "com.android.settings.SecuritySettings");
            mIntent.setComponent(comp);
            mIntent.setAction("android.intent.action.VIEW");
            Toast.makeText(getApplicationContext(), "请设置你的指纹", Toast.LENGTH_SHORT).show();
            startActivity(mIntent);
            finish();
        } else {
            Toast.makeText(this, "你的设备版本过低", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当已经设置指纹密码时候提供解锁Dialog 当没有设置指纹锁进入主界面提示
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void chooseFinger() {
        mFragment = FingerDialogFragment.newInstance();
        if (Integer.valueOf(Build.VERSION.SDK) >= 22) {
            mFingerprintManager = FingerprintManagerCompat.from(this);
            if (!mFingerprintManager.hasEnrolledFingerprints()
                    || !mFingerprintManager.isHardwareDetected()) {
                Toast.makeText(getApplicationContext(), "请在'设置 -> 安全 -> 指纹'中注册至少一个指纹", Toast.LENGTH_LONG).show();
            } else {
                mFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
                try {
                    CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
                    mFingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0, null,
                            new MyFingerSprintCallBack(mFragment, getApplicationContext(), mFingerprintManager), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "你的设备API低于23，无法指纹识别", Toast.LENGTH_LONG).show();
        }

    }

}
