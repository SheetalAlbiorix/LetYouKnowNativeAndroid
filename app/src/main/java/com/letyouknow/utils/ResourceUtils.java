package com.letyouknow.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ResourceUtils {

    public static Drawable getDrawableByName(Context context, String resourceName) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(resourceName, "drawable",
                context.getPackageName());
        return resources.getDrawable(resourceId);
    }
}