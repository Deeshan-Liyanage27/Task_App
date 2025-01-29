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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import com.example.task_app.databinding.ActivityEditTaskBinding;

import java.util.Calendar;
import java.util.Objects;

public class EditTask extends AppCompatActivity {
    private Task task;
    private ActivityEditTaskBinding binding;
    private String titleText,timeText,dateText;
    private int mon=-1;
    private int day=-1;
    private int yr=-1;
    private int min=-1;
    private int hour=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar =  findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).show();
        //match the background color with the color toolbar
        ConstraintLayout main = findViewById(R.id.main);
        ImageButton back = findViewById(R.id.backButton);
        int mainColor = main.getSolidColor();
        toolbar.setBackgroundColor(mainColor);
        back.setBackgroundColor(mainColor);
        toolbar.setPadding(145,0,0,25);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        EditText title =  findViewById(R.id.title2);
        EditText time = findViewById(R.id.editTextTime3);
        EditText date = findViewById(R.id.editTextDate3);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            titleText = bundle.getString("title");
            timeText = bundle.getString("time");
            dateText = bundle.getString("date");

            title.setText(titleText);
            time.setText(timeText);
            time.setGravity(Gravity.CENTER);

            String[] timeSplit = timeText.split(":",2);
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if(minute<10){
                                time.setText(hourOfDay + ":0" + minute);
                            }
                            else {
                                time.setText(hourOfDay + ":" + minute);
                            }

                            hour = hourOfDay;
                            min = minute;
                        }
                    },Integer.parseInt(timeSplit[0]),Integer.parseInt(timeSplit[1]),true);

                    timePickerDialog.show();
                }
           });

            date.setText(dateText);
            date.setFocusable(false);
            date.setClickable(false);
            date.setGravity(Gravity.CENTER);

            //Tracks the user interaction with the calendar and changes the date field accordingly
            CalendarView calendar = findViewById(R.id.calendarView3);
            String[] dates = dateText.split("/",3);
            day = Integer.parseInt(dates[0]);
            mon = Integer.parseInt(dates[1]);
            yr = Integer.parseInt(dates[2]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, yr);
            cal.set(Calendar.DAY_OF_MONTH, day);
            cal.set(Calendar.MONTH, mon);
            long milliTime = cal.getTimeInMillis();
            calendar.setDate(milliTime);

            calendar.setOnDateChangeListener(
                    new CalendarView.OnDateChangeListener() {
                        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                            month = month+1;
                            CharSequence dateFull =  dayOfMonth +"/"+ month +"/" + year;
                            day = dayOfMonth;
                            mon = month-1;
                            yr = year;
                            date.setText(dateFull);
                            date.setGravity(Gravity.CENTER);
                        }
                    }
            );

        }
        binding.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.saveButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(day == -1 || mon == -1 || yr == -1) {
                    Toast.makeText(v.getContext(), "Select a Date", Toast.LENGTH_SHORT).show();
                }
                else if(hour == -1 || min == -1){
                    Toast.makeText(v.getContext(), "Select a Time", Toast.LENGTH_SHORT).show();

                }
                else if (binding.title2.getText().toString().matches("")) {
                    Toast.makeText(v.getContext(), "Add a Title", Toast.LENGTH_SHORT).show();

                } else{
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // Gets the alarm manager
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) { // Check if the user gave permission
                            Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM); // Opens the permissions of the app
                            startActivity(i);
                        }

                        Editable taskText = binding.title2.getText(); // Get the title
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
            EditText timeText = findViewById(R.id.editTextTime3);
            EditText dateText = findViewById(R.id.editTextDate3);
            EditText title = findViewById(R.id.title2);

            int id = Task.tasks.size();

            Task task = new Task(id, title.getText().toString(), dateText.getText().toString(), timeText.getText().toString(), 0); // Create a task
            Task.tasks.add(task); // Add the activity to the list
            db.addTaskToDataBase(task); // Add the activity to the database
        }
        Toast.makeText(v.getContext(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }
    public void goBack (View v){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // Gets the alarm manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            Editable taskText = binding.title2.getText(); // Get the title
            Intent intent = new Intent(v.getContext(), Notification.class);
            intent.putExtra("TaskTitle", taskText.toString()); // Set the title
            PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT); // Create a pending intent

            long time = getTime(hour, min, day, mon, yr);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent); // Set the reminder
            try (SQLiteManager db = SQLiteManager.instanceOfDatabase(this)) {

                int id = Task.tasks.size();

                Task task = new Task(id, titleText, dateText, timeText, 0); // Create a task
                Task.tasks.add(task); // Add the activity to the list
                db.addTaskToDataBase(task); // Add the activity to the database
            }
        }

        finish();
    }
}
