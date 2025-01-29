package com.example.task_app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task_app.databinding.ActivityAddTaskBinding;

import java.util.Calendar;
import java.util.Objects;

public class AddTask extends AppCompatActivity {
    private Task selectedTask;
    private ActivityAddTaskBinding binding;
    private int mon=-1;
    private int day=-1;
    private int yr=-1;
    private int min=-1;
    private int hour=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel(); //Calls the method

        EdgeToEdge.enable(this);
        
        //Create toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).show();


        //match the background color with the color toolbar
        ConstraintLayout main = findViewById(R.id.main);
        ImageButton back = findViewById(R.id.imageButton);
        int mainColor = main.getSolidColor();
        toolbar.setBackgroundColor(mainColor);
        back.setBackgroundColor(mainColor);
        toolbar.setPadding(145,0,0,25);
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
        // Sets the notification after the save button is clicked
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(day == -1 || mon == -1 || yr == -1) {
                    Toast.makeText(v.getContext(), "Select a Date", Toast.LENGTH_SHORT).show();
                }
                else if(hour == -1 || min == -1){
                    Toast.makeText(v.getContext(), "Select a Time", Toast.LENGTH_SHORT).show();

                }
                else if (binding.editTextText.getText().toString().matches("")) {
                    Toast.makeText(v.getContext(), "Add a Title", Toast.LENGTH_SHORT).show();

                } else{
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // Gets the alarm manager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) { // Check if the user gave permission
                            Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM); // Opens the permissions of the app
                            startActivity(i);
                        }

                        Editable taskText = binding.editTextText.getText(); // Get the title
                        Intent intent = new Intent(v.getContext(), Notification.class);
                        intent.putExtra("TaskTitle", taskText.toString()); // Set the title
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); // Create a pending intent

                        long time = getTime(hour, min, day, mon, yr);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent); // Set the reminder
                        saveTask(v); // Calls the method to add the task to the database
                    }
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

            NotificationManager notificationManager = getSystemService(NotificationManager.class); // Register the channel with the system
            notificationManager.createNotificationChannel(channel); // Create the channel
        }
    }

    public void saveTask (View v){
        try (SQLiteManager db = SQLiteManager.instanceOfDatabase(this)) {
            // Get the inputs
            EditText timeText = findViewById(R.id.editTextTime);
            EditText dateText = findViewById(R.id.editTextDate);
            EditText title = findViewById(R.id.editTextText);

            int id = Task.tasks.size();

            Task task = new Task(id, title.getText().toString(), dateText.getText().toString(), timeText.getText().toString(), 0); // Create a task
            Task.tasks.add(task); // Add the activity to the list
            db.addTaskToDataBase(task); // Add the activity to the database
        }
        Toast.makeText(v.getContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }

    public void goBack(View v){
        finish();
    }


}