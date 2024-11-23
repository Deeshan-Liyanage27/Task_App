package com.example.task_app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter arrayAdapter = new TaskAdapter(this,Task.tasks);
        listView.setAdapter(arrayAdapter);
        loadFromDBToMemory();




    }

    public void addTask_window (View v){ // Starts the add task activity
        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    public void loadFromDBToMemory(){
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);
        db.populateTaskListArray();
    }

    public void updateProgress(){
        int tobeCompleted = Task.tasks.size();

        ProgressBar progress = findViewById(R.id.progressBar2);
        TextView text = findViewById(R.id.progressText);
        TextView taskNum = findViewById(R.id.tasks);
        progress.setProgress(25 - tobeCompleted);
        progress.setMax(25);

        if(tobeCompleted == 0){
            taskNum.setText("All Tasks are completed!!!");
            taskNum.setTextSize(25);
            text.setVisibility(View.INVISIBLE);
        } else if (tobeCompleted>0 && tobeCompleted<13) {
            taskNum.setText(String.valueOf(tobeCompleted));
            text.setText("More Task to complete");
            progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(255, 193, 7)));
            progress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 241, 196)));
        } else if (tobeCompleted>13) {
            taskNum.setText(String.valueOf(tobeCompleted));
            text.setText("More Tasks to complete");
            progress.setProgressTintList(ColorStateList.valueOf(Color.rgb(244, 67, 54)));
            progress.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 193, 189)));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView taskListView = findViewById(R.id.listView);
        ArrayAdapter<Task> arrayAdapter = new TaskAdapter(this, Task.tasks);
        taskListView.setAdapter(arrayAdapter); // Updates the list view tasks
        updateProgress();

    }
}