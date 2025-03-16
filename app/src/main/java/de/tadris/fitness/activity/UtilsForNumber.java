package de.tadris.fitness.util;

import android.widget.NumberPicker;
import android.view.View;
import android.app.AlertDialog;

import de.tadris.fitness.R;

public class UtilsForNumber {

    public static void setUpDialog(AlertDialog.Builder dialogBuilder, View v, String positiveButtonText, DialogOnClickListener onPositiveClick) {
        dialogBuilder.setView(v);
        dialogBuilder.setNegativeButton(R.string.cancel, null);
        dialogBuilder.setPositiveButton(positiveButtonText, (dialog, which) -> {
            if (onPositiveClick != null) {
                onPositiveClick.onClick((AlertDialog) dialog, which);
            }
        });
    }

    public static void setUpNumberPicker(NumberPicker np, int minValue, int maxValue, String formatterText) {
        np.setMinValue(minValue);
        np.setMaxValue(maxValue);
        np.setFormatter(value -> value == 0 ? "No speech" : value + formatterText);
        np.setWrapSelectorWheel(false);  
    }

    public interface DialogOnClickListener {
        void onClick(AlertDialog dialog, int which);
    }
}