package com.github.gestureback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build;

import java.lang.reflect.Method;

public class ActivityUtils {

    public static void convertFromTranslucent(Activity activity) {
        try {
            Method convertFromTranslucent = Activity.class.getDeclaredMethod("convertFromTranslucent");
            convertFromTranslucent.setAccessible(true);
            convertFromTranslucent.invoke(activity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void convertToTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            convertToTranslucentAfterL(activity);
        } else {
            convertToTranslucentBeforeL(activity);
        }
    }

    private static void convertToTranslucentBeforeL(Activity activity) {
        try {
            Class<? extends Activity> clazz = activity.getClass();
            Class<?>[] declaredClazz = clazz.getDeclaredClasses();
            Class<?> translucentConversionListener = null;
            for (Class<?> cls : declaredClazz) {
                if (cls.getName().contains("TranslucentConversionListener")) {
                    translucentConversionListener = cls;
                }
            }
            Method convertToTranslucent = clazz.getDeclaredMethod("convertToTranslucent", translucentConversionListener);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, new Object[]{null});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("PrivateApi")
    private static void convertToTranslucentAfterL(Activity activity) {
        try {
            Class<?>[] declaredClasses = Activity.class.getDeclaredClasses();
            Class<?> translucentConversionListener = null;
            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.getName().contains("TranslucentConversionListener")) {
                    translucentConversionListener = declaredClass;
                }
            }
            Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            getActivityOptions.setAccessible(true);
            Object activityOptions = getActivityOptions.invoke(activity);

            Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListener, ActivityOptions.class);
            convertToTranslucent.setAccessible(true);
            convertToTranslucent.invoke(activity, null, activityOptions);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
