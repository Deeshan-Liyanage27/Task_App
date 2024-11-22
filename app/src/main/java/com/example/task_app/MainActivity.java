package com.example.task_app;

import android.content.Intent;
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
        ListView noteListView = findViewById(R.id.listView);
        ArrayAdapter arrayAdapter = new TaskAdapter(this,Task.tasks);
        noteListView.setAdapter(arrayAdapter);
        loadFromDBToMemory();
        ListView listView = findViewById(R.id.listView);
        ProgressBar progress = findViewById(R.id.progressBar2);
        progress.setIndeterminate(false);
        progress.setProgress(30);
        progress.setMax(100);


    }

    public void addTask_window (View v){ // Starts the add task activity
        Intent i = new Intent(this, AddTask.class);
        startActivity(i);
    }

    public void loadFromDBToMemory(){
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);
        db.populateTaskListArray();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView taskListView = findViewById(R.id.listView);
        ArrayAdapter<Task> arrayAdapter = new TaskAdapter(this, Task.tasks);
        taskListView.setAdapter(arrayAdapter); // Updates the list view tasks
    }
}