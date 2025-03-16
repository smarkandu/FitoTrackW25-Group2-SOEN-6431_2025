package de.tadris.fitness.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.NumberPicker;

import de.tadris.fitness.R;
import de.tadris.fitness.util.UtilsForNumber;
import de.tadris.fitness.util.unit.UnitUtils;

public class VoiceAnnouncementsSettingsActivity extends FitoTrackSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setTitle(R.string.voiceAnnouncementsTitle);

        addPreferencesFromResource(R.xml.preferences_voice_announcements);

        bindPreferenceSummaryToValue(findPreference("announcementMode"));

        findPreference("speechConfig").setOnPreferenceClickListener(preference -> {
            showSpeechConfig();
            return true;
        });

    }

    private void showSpeechConfig() {
        UnitUtils.setUnit(this); 

        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        d.setTitle(getString(R.string.pref_voice_announcements_summary));
        View v = getLayoutInflater().inflate(R.layout.dialog_spoken_updates_picker, null);

        NumberPicker npT = v.findViewById(R.id.spokenUpdatesTimePicker);
        String timeFormatterText = " min";
        UtilsForNumber.setUpNumberPicker(npT, 0, 60, timeFormatterText);
        final String updateTimeVariable = "spokenUpdateTimePeriod";
        npT.setValue(preferences.getInt(updateTimeVariable, 0));

        String distanceUnit = " " + UnitUtils.CHOSEN_SYSTEM.getLongDistanceUnit();
        NumberPicker npD = v.findViewById(R.id.spokenUpdatesDistancePicker);
        UtilsForNumber.setUpNumberPicker(npD, 0, 10, distanceUnit);
        final String updateDistanceVariable = "spokenUpdateDistancePeriod";
        npD.setValue(preferences.getInt(updateDistanceVariable, 0));

        UtilsForNumber.setUpDialog(d, v, getString(R.string.okay), (dialog, which) -> {
            preferences.edit()
                    .putInt(updateTimeVariable, npT.getValue())
                    .putInt(updateDistanceVariable, npD.getValue())
                    .apply();
        });

        d.create().show();
}

}