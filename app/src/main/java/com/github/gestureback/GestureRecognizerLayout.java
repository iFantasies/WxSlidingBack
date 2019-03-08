package com.github.gestureback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class GestureRecognizerLayout extends FrameLayout {
    private ViewDragHelper mViewDragHelper;

    private Activity mActivity;

    private View mContentView;

    private int mContentLeft;
    private float mScrollPercent;

    private float mScrimOpacity;

    public GestureRecognizerLayout(@NonNull Context context) {
        this(context, null);
    }

    public GestureRecognizerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureRecognizerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViewDragHelper = ViewDragHelper.create(this, new Callback());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public void attachToActivity(Activity activity) {
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        View decorChild = decorView.getChildAt(0);//LinearLayout
        decorChild.setBackgroundColor(Color.WHITE);
        decorView.removeView(decorChild);
        mContentView = decorChild;
        addView(decorChild);
        decorView.addView(this);
        Log.e("attachTo", "true");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mContentView != null) {
            Log.e("onLayout", "true");
            mContentView.layout(mContentLeft, top, mContentLeft + mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
        }
    }

    private class Callback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View view, int pointId) {
            boolean edgeTouched = mViewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointId);
            boolean tryCapture = edgeTouched && ActivityStacker.size() > 1;

            if (edgeTouched && tryCapture) {
                if (mActivity != null) {
                    ActivityUtils.convertToTranslucent(mActivity);
                }
            }
            Log.e("tag", "" + tryCapture);
            return tryCapture;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            Log.e("clampViewPosition", "changed");
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.e("onViewPositionChanged", "changed");
            if (changedView == mContentView) {
                mContentLeft = left;
                mScrollPercent = Math.abs((float) left / mContentView.getWidth());
                linkagePreLayout(getPreLayout());
                invalidate();
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int childWidth = releasedChild.getWidth();
            int left = 0;
            if (mContentLeft > 500) {
                left = childWidth;
            }
            Log.e("onViewReleased", "xvel=" + xvel + ",yvel=" + yvel);
            mViewDragHelper.settleCapturedViewAt(left, 0);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            Log.e("onViewDragStateChanged", "" + state);
            if (mScrollPercent >= 1) {
                mActivity.finish();
            }
        }
    }

    private void linkagePreLayout(GestureRecognizerLayout layout) {
        if (layout != null) {
//            float translationX = (float) (0.4 / 0.95 * (mScrollPercent - 0.95) * layout.getWidth());
            float translationX = (float) (0.4 / 0.9 * (mScrollPercent - 0.9) * layout.getWidth());
            if (translationX > 0) {
                translationX = 0;
            }
            layout.setTranslationX(translationX);
        }
    }

    private Drawable getShadowLeft() {
        return ContextCompat.getDrawable(getContext(), R.drawable.bg_shadow_left);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
//        drawShadow(canvas, child);
        final int baseAlpha = (0x99000000 & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24 | (0x99000000 & 0xffffff);
        canvas.clipRect(0, 0, child.getLeft(), getHeight());
        canvas.drawColor(color);

        drawShadow(canvas, child);
        return ret;
    }

    private void drawShadow(Canvas canvas, View child) {
        getShadowLeft().setBounds(0, 0, child.getLeft(), getHeight());
        getShadowLeft().setAlpha((int) (mScrimOpacity * 255));
        getShadowLeft().draw(canvas);
    }

    public GestureRecognizerLayout getPreLayout() {
        return ((ISwipeBackLayout) mActivity).getPreActivity().getLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        mScrimOpacity = 1 - mScrollPercent;
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
