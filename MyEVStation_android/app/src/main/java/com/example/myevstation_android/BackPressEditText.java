package com.example.myevstation_android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class BackPressEditText extends androidx.appcompat.widget.AppCompatEditText {
    private OnBackPressListener mListener;


    public BackPressEditText(Context context)
    {
        super(context);
    }


    public BackPressEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public BackPressEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mListener != null) {
            mListener.onBackPress(keyCode);
        }

        return super.onKeyPreIme(keyCode, event);
    }


    public void setOnBackPressListener(OnBackPressListener listener) {
        mListener = listener;
    }

    public interface OnBackPressListener {
        public void onBackPress(int keyCode);
    }
}