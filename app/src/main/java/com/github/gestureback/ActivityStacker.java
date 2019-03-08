package com.github.gestureback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

public class ActivityStacker implements Application.ActivityLifecycleCallbacks {
    private final static Object sTag = new Object();

    private static ActivityStacker sActivityStacker;

    private static Stack<Activity> mActivityStack;

    private ActivityStacker(Application app) {
        mActivityStack = new Stack<>();
        app.registerActivityLifecycleCallbacks(this);
    }

    public static void init(Application app) {
        if (sActivityStacker == null) {
            synchronized (sTag) {
                if (sActivityStacker == null) {
                    sActivityStacker = new ActivityStacker(app);
                }
            }
        }
    }

    public static int size() {
        return mActivityStack.size();
    }

    public static Activity getPreActivity() {
        if (mActivityStack.size() > 1) {
            return mActivityStack.get(mActivityStack.size() - 2);
        } else {
            return null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mActivityStack.push(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivityStack.pop();
    }
}
