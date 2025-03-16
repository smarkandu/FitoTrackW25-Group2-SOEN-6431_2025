package de.tadris.fitness.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import de.tadris.fitness.R;
import de.tadris.fitness.export.BackupController;
import de.tadris.fitness.export.RestoreController;
import de.tadris.fitness.recording.announcement.VoiceAnnouncements;
import de.tadris.fitness.util.FileUtils;
import de.tadris.fitness.util.UtilsForNumber;
import de.tadris.fitness.util.unit.UnitUtils;
import de.tadris.fitness.view.ProgressDialogController;

public class SettingsActivity extends FitoTrackSettingsActivity {

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(); 

        setTitle(R.string.settings);

        addPreferencesFromResource(R.xml.preferences_main);

        bindPreferenceSummaryToValue(findPreference("unitSystem"));
        bindPreferenceSummaryToValue(findPreference("mapStyle"));
        bindPreferenceSummaryToValue(findPreference("themeSetting"));
        findPreference("themeSetting").setOnPreferenceChangeListener((preference, newValue) -> {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
            Toast.makeText(SettingsActivity.this, R.string.hintRestart, Toast.LENGTH_LONG).show();
            return true;
        });

        findPreference("weight").setOnPreferenceClickListener(preference -> {
            showWeightPicker();
            return true;
        });
        findPreference("speech").setOnPreferenceClickListener(preference -> {
            checkTTSandShowConfig();
            return true;
        });
        findPreference("import").setOnPreferenceClickListener(preference -> {
            showImportDialog();
            return true;
        });
        findPreference("export").setOnPreferenceClickListener(preference -> {
            showExportDialog();
            return true;
        });

    }

    private VoiceAnnouncements voiceAnnouncements;

    private void checkTTSandShowConfig() {
        voiceAnnouncements = new VoiceAnnouncements(this, available -> {
            if (available) {
                showSpeechConfig();
            } else {
                // TextToSpeech is not available
                Toast.makeText(SettingsActivity.this, R.string.ttsNotAvailable, Toast.LENGTH_LONG).show();
            }
            if (voiceAnnouncements != null) {
                voiceAnnouncements.destroy();
            }
        });
    }

    private void showSpeechConfig() {
        startActivity(new Intent(this, VoiceAnnouncementsSettingsActivity.class));
    }

    private void showExportDialog() {
        if (!hasPermission()) {
            requestPermissions();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.exportData)
                .setMessage(R.string.exportDataSummary)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.backup, (dialog, which) -> exportBackup()).create().show();
    }

    private void exportBackup(){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.backup));
        dialogController.show();
        new Thread(() -> {
            try{
                String file= getFilesDir().getAbsolutePath() + "/shared/backup.ftb";
                File parent = new File(file).getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new IOException("Cannot write");
                }
                Uri uri= FileProvider.getUriForFile(getBaseContext(), "de.tadris.fitness.fileprovider", new File(file));

                BackupController backupController= new BackupController(getBaseContext(), new File(file), (progress, action) -> mHandler.post(() -> dialogController.setProgress(progress, action)));
                backupController.exportData();

                mHandler.post(() -> {
                    dialogController.cancel();
                    FileUtils.saveOrShareFile(this, uri, "ftb");
                });
            }catch (Exception e){
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorExportFailed);
                });
            }
        }).start();
    }

    private void showImportDialog() {
        if(!hasPermission()){
            requestPermissions();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.importBackup)
                .setMessage(R.string.importBackupMessage)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.restore, (dialog, which) -> importBackup()).create().show();
    }

    private void requestPermissions() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static final int FILE_SELECT_CODE= 21;
    private void importBackup(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("/");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.chooseBackupFile)), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ignored) {
            //Log.e("ImportBackup", "No file picker available", e);
         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                importBackup(data.getData());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importBackup(Uri uri){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.backup));
        dialogController.show();
        new Thread(() -> {
            try{
                RestoreController restoreController= new RestoreController(getBaseContext(), uri,
                        (progress, action) -> mHandler.post(() -> dialogController.setProgress(progress, action)));
                restoreController.restoreData();

                mHandler.post(dialogController::cancel);
            }catch (Exception e){
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorImportFailed);
                });
            }
        }).start();
    }

    private void showWeightPicker() {
        UnitUtils.setUnit(this); 

        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        d.setTitle(getString(R.string.pref_weight));

        View v = getLayoutInflater().inflate(R.layout.dialog_weight_picker, null);
        NumberPicker np = v.findViewById(R.id.weightPicker);

        int minValue = (int) UnitUtils.CHOSEN_SYSTEM.getWeightFromKilogram(20);
        int maxValue = (int) UnitUtils.CHOSEN_SYSTEM.getWeightFromKilogram(150);
        String weightUnit = UnitUtils.CHOSEN_SYSTEM.getWeightUnit();

        UtilsForNumber.setUpNumberPicker(np, minValue, maxValue, weightUnit);

        final String preferenceVariable = "weight";
        np.setValue((int) Math.round(UnitUtils.CHOSEN_SYSTEM.getWeightFromKilogram(preferences.getInt(preferenceVariable, 80))));

        UtilsForNumber.setUpDialog(d, v, getString(R.string.okay), (dialog, which) -> {
        
            int unitValue = np.getValue();
            int kilograms = (int) Math.round(UnitUtils.CHOSEN_SYSTEM.getKilogramFromUnit(unitValue));
            preferences.edit().putInt(preferenceVariable, kilograms).apply();
        });

    d.create().show();
}

}