package com.example.task_app;

import static java.lang.Math.random;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.PendingIntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task_app.databinding.ActivityAddTaskBinding;
import com.example.task_app.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Objects;

public class AddTask extends AppCompatActivity {
    private Task selectedTask;
    private ActivityAddTaskBinding binding;
    private int mon=0;
    private int day=0;
    private int yr=0;
    private int min=0;
    private int hour=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        EdgeToEdge.enable(this); //
        //setContentView(R.layout.activity_add_task);
        //Create toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add the back button

        //match the background color with the color toolbar
        ConstraintLayout main = findViewById(R.id.main);
        int mainColor = main.getSolidColor();
        toolbar.setBackgroundColor(mainColor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Disables the text box to enter the date
        EditText dateText = findViewById(R.id.editTextDate);
        dateText.setText("Select Date");
        dateText.setFocusable(false);
        dateText.setClickable(false);

        //Tracks the user interaction with the calendar and changes the date field accordingly
        CalendarView calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        EditText dateText = findViewById(R.id.editTextDate);
                        month = month+1;
                        CharSequence date =  dayOfMonth +"/"+ month +"/" + year;
                        day = dayOfMonth;
                        mon = month-1;
                        yr = year;
                        dateText.setText(date);
                        dateText.setGravity(Gravity.CENTER);
                    }
                }
        );
        EditText timeText = findViewById(R.id.editTextTime);
        timeText.setText("Choose the time:");
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(minute<10){
                            timeText.setText(hourOfDay + ":0" + minute);
                        }
                        else {
                            timeText.setText(hourOfDay + ":" + minute);
                        }
                        timeText.setGravity(Gravity.CENTER);
                        hour = hourOfDay;
                        min = minute;
                    }
                },0,0,true);
                timePickerDialog.show();
            }
        });
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(i);
                    }
                    Editable taskText = binding.editTextText.getText();
                    Intent intent = new Intent(v.getContext(), Notification.class);
                    intent.putExtra("Task", taskText);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);



                    long time = getTime(hour, min, day, mon, yr);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                    Log.d("set", "its set");
                    Toast.makeText(v.getContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
                    saveTask(v);
                }
            }
        });

    }

    private long getTime(int hour, int minute, int day, int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day,hour,minute);
        return calendar.getTimeInMillis();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task";
            String description = "Task Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(){

    }

    public void saveTask (View v){
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);
        // Get the inputs
        EditText timeText = findViewById(R.id.editTextTime);
        EditText dateText = findViewById(R.id.editTextDate);
        EditText title = findViewById(R.id.editTextText);

        int id = Task.tasks.size();

        Task task  = new Task(id,title.getText().toString(),dateText.getText().toString(), timeText.getText().toString(),0); // Create a task
        Task.tasks.add(task); // Add the activity to the list
        db.addTaskToDataBase(task); // Add the activity to the database
        finish(); // Close the activity
    }


}