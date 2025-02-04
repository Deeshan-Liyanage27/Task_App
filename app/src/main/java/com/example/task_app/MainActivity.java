package com.example.task_app;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Create the list view
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<Task> arrayAdapter = new TaskAdapter(this,Task.tasks);
        listView.setAdapter(arrayAdapter); // Sets the adapter for the list view
        loadFromDBToMemory(); // Loads the tasks from the database to the memory

        //Checks for user permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat notification = NotificationManagerCompat.from(this);
            if(!notification.areNotificationsEnabled()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 0);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // Gets the alarm manager
            if (!alarmManager.canScheduleExactAlarms()) { // Check if the user gave permission
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM); // Opens the permissions of the app
                startActivity(i);
            }
        }

    }

    //This method is used to start the add task activity when the button add button is pressed
    public void addTask_window (View v){
        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    public void loadFromDBToMemory(){ // Loads the tasks form the database to the memory
        try (SQLiteManager db = SQLiteManager.instanceOfDatabase(this)) {
            db.populateTaskListArray();
        }
    }

    //This method is used to update the progress bar
    public void updateProgress(){
        int tobeCompleted = Task.tasks.size();

        ProgressBar progress = findViewById(R.id.progressBar2);
        TextView text = findViewById(R.id.progressText);
        TextView taskNum = findViewById(R.id.tasks);
        progress.setProgress(25 - tobeCompleted);
        progress.setMax(25);
        progress.animate().setDuration(1000).rotation(390).start();



        if(tobeCompleted == 0){
            taskNum.setText(R.string.NoTasks);
            taskNum.setTextSize(25);
            text.setVisibility(View.INVISIBLE);
            progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(16, 240, 90)));
        } else if (tobeCompleted<13) {
            text.setVisibility(View.VISIBLE);
            taskNum.setText(String.valueOf(tobeCompleted));
            text.setText(R.string.HaveTasks);
            progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 193, 7)));
            progress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 241, 196)));
        } else {
            taskNum.setText(String.valueOf(tobeCompleted));
            text.setText(R.string.HaveTasks);
            progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(244, 67, 54)));
            progress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 193, 189)));
        }

        refresh();

    }

   @Override
    protected void onResume() {
        super.onResume();
        ListView taskListView = findViewById(R.id.listView);
        ArrayAdapter<Task> arrayAdapter = new TaskAdapter(this, Task.tasks);
        taskListView.setAdapter(arrayAdapter); // Updates the list view tasks
        updateProgress();
    }


    //This method is used to refresh the progress bar every 500 milliseconds
    private void refresh(){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();

            }
        };

        handler.postDelayed(runnable,500);
    }

}