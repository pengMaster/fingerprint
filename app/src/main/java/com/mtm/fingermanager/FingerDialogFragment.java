package com.mtm.fingermanager;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FingerDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@SuppressLint("NewApi") public class FingerDialogFragment extends DialogFragment implements FingerprintUiHelper.Callback  {
    FingerprintUiHelper.FingerprintUiHelperBuilder mFingerprintUiHelperBuilder;
    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerprintManagerCompat.CryptoObject mCryptoObject;

    public FingerDialogFragment() {
        // Required empty public constructor
    }


    public static FingerDialogFragment newInstance() {
        FingerDialogFragment fragment = new FingerDialogFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        FingerprintManagerCompat manager = FingerprintManagerCompat.from(getActivity());

        mFingerprintUiHelperBuilder = new FingerprintUiHelper.FingerprintUiHelperBuilder(getActivity(), manager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("验证指纹");
        getDialog().setOnKeyListener(keylistener);
        getDialog().setCanceledOnTouchOutside(false);
        View view = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        //设置忘记密码
        TextView tvTextView = (TextView)view.findViewById(R.id.fingerprint_forget);
        tvTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		          forgetData(getActivity());
		          getDialog().dismiss();
			}
		});
        mFingerprintUiHelper = mFingerprintUiHelperBuilder.build(
                (ImageView) view.findViewById(R.id.fingerprint_icon),
                (TextView) view.findViewById(R.id.fingerprint_status), this);
        return view;
    }
 
    /**
     * 忘记密码
     * @param context
     */
    private void forgetData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", 0);
        sp.edit().clear().commit();
    }
    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener(){
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            Log.e("Dialog.onKey","OnKeyListener");
            if (keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
            {
                System.exit(0);
                Log.e("Dialog.OnKeyListener","OnKeyListener");
                return false;
            }
            else
            {
                return false;
            }
        }
    } ;

    @Override
    public void onAuthenticated() {
    }



    @Override
    public void onError() {

    }

    /**
     * 设置CryptObject对象,将用于FingerprintManager用于识别指纹
     */
    public void setCryptoObject(FingerprintManagerCompat.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }
}
