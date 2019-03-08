package com.github.gestureback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

@SuppressLint("Registered")
public class GestureBackActivity extends AppCompatActivity implements ISwipeBackLayout {
    private GestureRecognizerLayout mRecognizerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecognizerLayout = new GestureRecognizerLayout(this);
        mRecognizerLayout.setLayoutParams(params);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mRecognizerLayout.attachToActivity(this);
    }


    @Override
    public GestureRecognizerLayout getLayout() {
        return mRecognizerLayout;
    }

    @Override
    public ISwipeBackLayout getPreActivity() {
        return (ISwipeBackLayout) ActivityStacker.getPreActivity();
    }
}
