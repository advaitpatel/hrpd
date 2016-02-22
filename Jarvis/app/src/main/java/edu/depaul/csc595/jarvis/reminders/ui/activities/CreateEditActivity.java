package edu.depaul.csc595.jarvis.reminders.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;

import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.depaul.csc595.jarvis.R;
import edu.depaul.csc595.jarvis.reminders.adapters.ColoursAdapter;
import edu.depaul.csc595.jarvis.reminders.adapters.IconsAdapter;
import edu.depaul.csc595.jarvis.reminders.database.DatabaseHelper;
import edu.depaul.csc595.jarvis.reminders.models.Reminder;
import edu.depaul.csc595.jarvis.reminders.receivers.AlarmReceiver;
import edu.depaul.csc595.jarvis.reminders.utils.AlarmUtil;
import edu.depaul.csc595.jarvis.reminders.utils.AnimationUtil;
import edu.depaul.csc595.jarvis.reminders.utils.DateAndTimeUtil;
import edu.depaul.csc595.jarvis.reminders.models.Icon;


/**
 * Created by Advait on 18-02-2016.
 */
public class CreateEditActivity extends AppCompatActivity
{

    @Bind(R.id.create_coordinator) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.notification_title) EditText mTitleEditText;
    @Bind(R.id.notification_content) EditText mContentEditText;
    @Bind(R.id.time)
    TextView mTimeText;
    @Bind(R.id.date) TextView mDateText;
    @Bind(R.id.repeat_day) TextView mRepeatText;
    @Bind(R.id.switch_toggle)
    SwitchCompat mForeverSwitch;
    @Bind(R.id.show_times_number) EditText mTimesEditText;
    @Bind(R.id.forever_row)
    TableRow mForeverRow;
    @Bind(R.id.bottom_row) TableRow mBottomRow;
    @Bind(R.id.bottom_view)
    View mBottomView;
    @Bind(R.id.show) TextView mShowText;
    @Bind(R.id.times) TextView mTimesText;
    @Bind(R.id.select_icon_text) TextView mIconText;
    @Bind(R.id.select_colour_text) TextView mColourText;
    @Bind(R.id.colour_icon)
    ImageView mImageColourSelect;
    @Bind(R.id.selected_icon) ImageView mImageIconSelect;
    @Bind(R.id.error_time) ImageView mImageWarningTime;
    @Bind(R.id.error_date) ImageView mImageWarningDate;
    @Bind(R.id.error_show) ImageView mImageWarningShow;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private AlertDialog mIconSelectorDialog;
    private AlertDialog mColourSelectorDialog;
    private Calendar mCalendar;
    private int mTimesShown;
    private int mRepeatType;
    private boolean[] mDaysOfWeek;
    private String mIcon;
    private String mColour;
    private boolean newNotification;
    private int mId;
    private String[] mColourNames;
    private String[] mColoursArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_reminder_create);

        ButterKnife.bind(this);

        //setSupportActionBar(mToolbar);
        //mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        //if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        mColourNames = getResources().getStringArray(R.array.colour_names_array);
        mColoursArray = getResources().getStringArray(R.array.colours_array);

        mCalendar = Calendar.getInstance();
        mId = getIntent().getIntExtra("NOTIFICATION_ID", 0);

        // Check whether to edit or create a new notification
        if (mId == 0) {
            assignDefaultValues();
        } else {
            assignReminderValues();
        }

    }

    public void assignDefaultValues()
    {
        //if (getSupportActionBar() != null)
            //getSupportActionBar().setTitle(getResources().getString(R.string.create_notification));
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        mId = database.getLastNotificationId() + 1;
        database.close();
        mCalendar.set(Calendar.SECOND, 0);
        mTimesEditText.setText("1");
        newNotification = true;
        mRepeatType = 0;
        mTimesShown = 0;
        mDaysOfWeek = new boolean[7];
        Arrays.fill(mDaysOfWeek, false);
        mIcon = getResources().getString(R.string.default_icon_value);
        mColour = getResources().getString(R.string.default_colour_value);
    }

    public void assignReminderValues() {
        // Prevent keyboard from opening automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //if (getSupportActionBar() != null)
          //  getSupportActionBar().setTitle(getResources().getString(R.string.edit_notification));
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        Reminder reminder = database.getNotification(mId);
        database.close();

        mShowText.setText(String.format(getResources().getString(R.string.times_shown_edit), reminder.getNumberShown()));
        mTimesEditText.setText(Integer.toString(reminder.getNumberToShow()));
        mCalendar = DateAndTimeUtil.parseDateAndTime(reminder.getDateAndTime());
        mTitleEditText.setText(reminder.getTitle());
        mContentEditText.setText(reminder.getContent());
        mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
        mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar, this));
        mTimesEditText.setText(Integer.toString(reminder.getNumberToShow()));
        mTimesShown = reminder.getNumberShown();
        mRepeatType = reminder.getRepeatType();
        mIcon = reminder.getIcon();
        mColour = reminder.getColour();
        mTimesText.setVisibility(View.VISIBLE);
        mCalendar.set(Calendar.SECOND, 0);
        newNotification = false;

        if (!getResources().getString(R.string.default_icon).equals(mIcon)) {
            int iconResId = getResources().getIdentifier(reminder.getIcon(), "drawable", getPackageName());
            mImageIconSelect.setImageResource(iconResId);
            mIconText.setText(getResources().getString(R.string.custom_icon));
        }

        if (!getResources().getString(R.string.default_colour).equals(mColour)) {
            mImageColourSelect.setColorFilter(Color.parseColor(mColour));
            mColourText.setText(mColourNames[Arrays.asList(mColoursArray).indexOf(mColour)]);
        }

        if (reminder.getRepeatType() != 0) {
            mForeverRow.setVisibility(View.VISIBLE);
            mBottomRow.setVisibility(View.VISIBLE);
            mBottomView.setVisibility(View.VISIBLE);
            String[] mRepeatTexts = getResources().getStringArray(R.array.repeat_array);
            mRepeatText.setText(mRepeatTexts[reminder.getRepeatType()]);
        }

        if (reminder.getRepeatType() == 5) {
            mDaysOfWeek = reminder.getDaysOfWeek();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getResources().getString(R.string.repeats_on));
            stringBuilder.append(" ");
            String[] shortWeekDays = DateAndTimeUtil.getShortWeekDays();
            for (int i = 0; i < mDaysOfWeek.length; i++) {
                if (mDaysOfWeek[i]) {
                    stringBuilder.append(shortWeekDays[i]);
                    stringBuilder.append(" ");
                }
            }
            mRepeatText.setText(stringBuilder);
        } else {
            mDaysOfWeek = new boolean[7];
            Arrays.fill(mDaysOfWeek, false);
        }

        if (Boolean.parseBoolean(reminder.getForeverState())) {
            mForeverSwitch.setChecked(true);
            toggleTextColour();
        }
    }

    public void timePicker(View view) {
        TimePickerDialog TimePicker = new TimePickerDialog(CreateEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                mCalendar.set(Calendar.MINUTE, minute);
                mTimeText.setText(DateAndTimeUtil.toStringReadableTime(mCalendar, getApplicationContext()));
            }
        }, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        TimePicker.show();
    }

    public void datePicker(View view) {
        DatePickerDialog DatePicker = new DatePickerDialog(CreateEditActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker DatePicker, int year, int month, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDateText.setText(DateAndTimeUtil.toStringReadableDate(mCalendar));
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        DatePicker.show();
    }

    public void iconSelector(View view) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.create_coordinator);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_custom_reminder_view_dialog_icons, coordinatorLayout, false);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.icons_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), getResources().getInteger(R.integer.grid_columns)));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        recyclerView.setAdapter(new IconsAdapter(this, R.layout.activity_custom_reminder_item_icon_grid, database.getIconList()));
        database.close();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.select_icon));
        builder.setView(dialogView);
        mIconSelectorDialog = builder.show();
    }

    public void iconSelected(int iconResId, Icon icon) {
        mIcon = icon.getName();
        if (!mIcon.equals(getResources().getString(R.string.default_icon_value))) {
            mIconText.setText(getResources().getString(R.string.custom_icon));
        } else {
            mIconText.setText(getResources().getString(R.string.default_icon));
        }
        mImageIconSelect.setImageResource(iconResId);
        mIconSelectorDialog.cancel();
        DatabaseHelper database = DatabaseHelper.getInstance(this);
        icon.setUseFrequency(icon.getUseFrequency() + 1);
        database.updateIcon(icon);
        database.close();
    }

    public void colourSelector(View view) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.create_coordinator);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_custom_reminder_view_dialog_color, coordinatorLayout, false);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.colours_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new ColoursAdapter(this, R.layout.activity_custom_reminder_item_color_list, mColoursArray, mColourNames));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.select_colour));
        builder.setView(dialogView);
        mColourSelectorDialog = builder.show();
    }

    public void colourSelected(String name, String colour) {
        mColour = colour;
        mImageColourSelect.setColorFilter(Color.parseColor(colour));
        mColourText.setText(name);
        mColourSelectorDialog.cancel();
    }

    public void repeatSelector(View view) {
        final String[] repeatArray = getResources().getStringArray(R.array.repeat_array);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setItems(repeatArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 5) {
                    daysOfWeekSelector();
                } else {
                    if (which == 0) {
                        mForeverSwitch.setChecked(false);
                        mForeverRow.setVisibility(View.GONE);
                        mBottomRow.setVisibility(View.GONE);
                        mBottomView.setVisibility(View.GONE);
                    } else {
                        mForeverRow.setVisibility(View.VISIBLE);
                        mBottomRow.setVisibility(View.VISIBLE);
                        mBottomView.setVisibility(View.VISIBLE);
                    }
                    mRepeatType = which;
                    mRepeatText.setText(repeatArray[which]);
                }
            }
        }).create().show();
    }

    public void daysOfWeekSelector() {
        final boolean[] values = mDaysOfWeek;
        final String[] shortWeekDays = DateAndTimeUtil.getShortWeekDays();
        String[] weekDays = DateAndTimeUtil.getWeekDays();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setMultiChoiceItems(weekDays, mDaysOfWeek, new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                values[which] = isChecked;
            }
        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (Arrays.toString(values).contains("true")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(getResources().getString(R.string.repeats_on));
                    stringBuilder.append(" ");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i]) {
                            stringBuilder.append(shortWeekDays[i]);
                            stringBuilder.append(" ");
                        }
                    }
                    mRepeatText.setText(stringBuilder);
                    mDaysOfWeek = values;
                    mRepeatType = 5;
                    mForeverRow.setVisibility(View.VISIBLE);
                    mBottomRow.setVisibility(View.VISIBLE);
                    mBottomView.setVisibility(View.VISIBLE);
                } else {
                    mRepeatType = 0;
                    mForeverSwitch.setChecked(false);
                    mForeverRow.setVisibility(View.GONE);
                    mBottomRow.setVisibility(View.GONE);
                    mBottomView.setVisibility(View.GONE);
                    mRepeatText.setText(getResources().getStringArray(R.array.repeat_array)[0]);
                }
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        }).create().show();
    }

    public void saveNotification() {
        Calendar nowCalendar = Calendar.getInstance();

        // Assign time depending on whether a value was selected
        String time;
        if (mTimeText.getText().equals(getResources().getString(R.string.time_now))) {
            time = DateAndTimeUtil.toStringTime(nowCalendar);
        } else {
            time = DateAndTimeUtil.toStringTime(mCalendar);
        }

        // Assign date depending on whether a value was selected
        String date;
        if (mDateText.getText().equals(getResources().getString(R.string.date_today))) {
            date = DateAndTimeUtil.toStringDate(nowCalendar);
        } else {
            date = DateAndTimeUtil.toStringDate(mCalendar);
        }

        // Show notification once if set to not repeat
        int timesToShow = Integer.parseInt(mTimesEditText.getText().toString());
        if (mRepeatType == 0) {
            timesToShow = mTimesShown + 1;
        }

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        Reminder reminder = new Reminder()
                .setId(mId)
                .setTitle(mTitleEditText.getText().toString())
                .setContent(mContentEditText.getText().toString())
                .setDateAndTime(date + time)
                .setRepeatType(mRepeatType)
                .setForeverState(Boolean.toString(mForeverSwitch.isChecked()))
                .setNumberToShow(timesToShow)
                .setNumberShown(mTimesShown)
                .setIcon(mIcon)
                .setColour(mColour);

        if (newNotification) {
            database.addNotification(reminder);
        } else {
            database.updateNotification(reminder);
        }
        if (mRepeatType == 5) {
            reminder.setDaysOfWeek(mDaysOfWeek);
            if (database.isDaysOfWeekPresent(reminder)) {
                database.updateDaysOfWeek(reminder);
            } else {
                database.addDaysOfWeek(reminder);
            }
        }
        database.close();
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        AlarmUtil.setAlarm(getApplicationContext(), alarmIntent, reminder.getId(), mCalendar);
        finish();
    }

    public void toggleSwitch(View view) {
        mForeverSwitch.toggle();
        toggleTextColour();
    }

    public void switchClicked(View view) {
        toggleTextColour();
    }

    public void toggleTextColour() {
        if (mForeverSwitch.isChecked()) {
            mShowText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesEditText.setTextColor(getResources().getColor(R.color.textLightGray));
            mTimesText.setTextColor(getResources().getColor(R.color.textLightGray));
        } else {
            mShowText.setTextColor(Color.BLACK);
            mTimesEditText.setTextColor(Color.BLACK);
            mTimesText.setTextColor(Color.BLACK);
        }
    }

    public void validateInput() {
        Calendar nowCalendar = Calendar.getInstance();
        Boolean isTimeNow = mTimeText.getText().equals(getResources().getString(R.string.time_now));
        Boolean isToday = mDateText.getText().equals(getResources().getString(R.string.date_today));

        if (isTimeNow) {
            mCalendar.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY));
            mCalendar.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE));
        }

        if (isToday) {
            mCalendar.set(Calendar.YEAR, nowCalendar.get(Calendar.YEAR));
            mCalendar.set(Calendar.MONTH, nowCalendar.get(Calendar.MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH));
        }

        // Check if the number of times to show notification is empty
        String times;
        if (mTimesEditText.getText().toString().isEmpty()) {
            times = "0";
            mTimesEditText.append(times);
        } else {
            times = mTimesEditText.getText().toString();
        }

        // Check if selected date is before today's date
        if (DateAndTimeUtil.toLongDateAndTime(mCalendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_past_date), Snackbar.LENGTH_SHORT).show();
            mImageWarningTime.setVisibility(View.VISIBLE);
            mImageWarningDate.setVisibility(View.VISIBLE);

            // Check if title is empty
        } else if (mTitleEditText.getText().toString().trim().isEmpty()) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_title_empty), Snackbar.LENGTH_SHORT).show();
            AnimationUtil.shakeView(mTitleEditText, getApplicationContext());

            // Check if times to show notification is too low
        } else if (Integer.parseInt(times) <= mTimesShown && !mForeverSwitch.isChecked() && mRepeatType != 0) {
            Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.toast_higher_number), Snackbar.LENGTH_SHORT).show();
            mImageWarningShow.setVisibility(View.VISIBLE);
        } else {
            saveNotification();
        }
    }

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {
        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_reminder_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                validateInput();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
