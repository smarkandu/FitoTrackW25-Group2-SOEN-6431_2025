package de.tadris.fitness.activity;

import android.app.Activity;
import android.view.MenuItem;

public class MenuUtils {
    public static boolean handleHomeButton(Activity activity, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
            return true;
        }
        return false;
    }
}
