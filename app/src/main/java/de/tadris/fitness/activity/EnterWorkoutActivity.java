package de.tadris.fitness.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.tadris.fitness.R;
import de.tadris.fitness.data.WorkoutBuilder;
import de.tadris.fitness.data.WorkoutType;
import de.tadris.fitness.dialog.DatePickerFragment;
import de.tadris.fitness.dialog.DurationPickerDialogFragment;
import de.tadris.fitness.dialog.SelectWorkoutTypeDialog;
import de.tadris.fitness.dialog.TimePickerFragment;
import de.tadris.fitness.util.unit.UnitUtils;

public class EnterWorkoutActivity extends InformationActivity implements SelectWorkoutTypeDialog.WorkoutTypeSelectListener, DatePickerFragment.DatePickerCallback, TimePickerFragment.TimePickerCallback, DurationPickerDialogFragment.DurationPickListener {

    WorkoutBuilder workoutBuilder = new WorkoutBuilder();
    TextView typeTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView durationTextView;

    EditText distanceEditText;
    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_workout);

        initRoot();

        addTitle(getString(R.string.info));
        setupActionBar();

        KeyValueLine typeLine = addKeyValueLine(getString(R.string.type));
        typeTextView = typeLine.value;
        typeLine.lineRoot.setOnClickListener(v -> showTypeSelection());

        distanceEditText = new EditText(this);
        distanceEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        distanceEditText.setSingleLine(true);
        distanceEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        distanceEditText.setOnEditorActionListener((v, actionId, event) -> {
            if ((actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                    && (event == null || !event.isShiftPressed())) {
                showDateSelection();
                return true;
            }
            return false;
        });

        addKeyValueLine(getString(R.string.workoutDistance), distanceEditText, UnitUtils.CHOSEN_SYSTEM.getLongDistanceUnit());

        KeyValueLine dateLine = addKeyValueLine(getString(R.string.workoutDate));
        dateLine.lineRoot.setOnClickListener(v -> showDateSelection());
        dateTextView = dateLine.value;

        KeyValueLine timeLine = addKeyValueLine(getString(R.string.workoutStartTime));
        timeLine.lineRoot.setOnClickListener(v -> showTimeSelection());
        timeTextView = timeLine.value;

        KeyValueLine durationLine = addKeyValueLine(getString(R.string.workoutDuration));
        durationLine.lineRoot.setOnClickListener(v -> showDurationSelection());
        durationTextView = durationLine.value;

        addTitle(getString(R.string.comment));

        commentEditText = new EditText(this);
        commentEditText.setSingleLine(true);
        root.addView(commentEditText);

        updateTextViews();
    }

    private void saveWorkout() {
        workoutBuilder.setComment(commentEditText.getText().toString());
        try {
            workoutBuilder.setLength((int) (Double.parseDouble(distanceEditText.getText().toString()) * 1000));
        } catch (NumberFormatException ignored) {
            distanceEditText.requestFocus();
            distanceEditText.setError(getString(R.string.errorEnterValidNumber));
            return;
        }
        if (workoutBuilder.getStart().getTimeInMillis() > System.currentTimeMillis()) {
            Toast.makeText(this, R.string.errorWorkoutAddFuture, Toast.LENGTH_LONG).show();
            return;
        }
        if (workoutBuilder.getDuration() < 1000) {
            Toast.makeText(this, R.string.errorEnterValidDuration, Toast.LENGTH_LONG).show();
            return;
        }
        WorkoutActivity.setSelectedWorkout(workoutBuilder.insertWorkout(this));
        startActivity(new Intent(this, ShowWorkoutActivity.class));
        finish();
    }

    private void updateTextViews() {
        typeTextView.setText(getString(workoutBuilder.getWorkoutType().title));
        dateTextView.setText(DateFormat.getDateInstance().format(workoutBuilder.getStart().getTime()));
        timeTextView.setText(DateFormat.getTimeInstance().format(workoutBuilder.getStart().getTime()));
        durationTextView.setText(UnitUtils.getHourMinuteSecondTime(workoutBuilder.getDuration()));
    }

    private void showTypeSelection() {
        new SelectWorkoutTypeDialog(this, this).show();
    }

    @Override
    public void onSelectWorkoutType(WorkoutType workoutType) {
        workoutBuilder.setWorkoutType(workoutType);
        updateTextViews();
        distanceEditText.requestFocus();
    }

    private void showDateSelection() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.callback = this;
        fragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDatePick(int year, int month, int day) {
        workoutBuilder.getStart().set(year, month, day);
        updateTextViews();
        showTimeSelection();
    }

    private void showTimeSelection() {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.callback = this;
        fragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePick(int hour, int minute) {
        workoutBuilder.getStart().set(Calendar.HOUR_OF_DAY, hour);
        workoutBuilder.getStart().set(Calendar.MINUTE, minute);
        workoutBuilder.getStart().set(Calendar.SECOND, 0);
        updateTextViews();
        showDurationSelection();
    }

    private void showDurationSelection() {
        DurationPickerDialogFragment fragment = new DurationPickerDialogFragment(this, this, workoutBuilder.getDuration());
        fragment.listener = this;
        fragment.initialDuration = workoutBuilder.getDuration();
        fragment.show();
    }

    @Override
    public void onDurationPick(long duration) {
        workoutBuilder.setDuration(duration);
        updateTextViews();
        commentEditText.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enter_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionEnterWorkoutAdd:
                saveWorkout();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    void initRoot() {
        root = findViewById(R.id.enterWorkoutRoot);
    }
}
