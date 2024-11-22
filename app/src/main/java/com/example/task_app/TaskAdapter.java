package com.example.task_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.*;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter (Context context, List<Task> tasks){
        super(context,0,tasks); // Passes the list of tasks to the adapter.
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Task task = getItem(position); // Get the current task

        if (convertView == null) { // Creates a new view if it doesn't exist
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        // Find the elements in the task_item.xml layout
        TextView title = convertView.findViewById(R.id.cellTitle);
        TextView date = convertView.findViewById(R.id.date);
        TextView time = convertView.findViewById(R.id.time);
        CheckBox deleteCheckBox = convertView.findViewById(R.id.checkBox3);

        // Set to display
        title.setText(task.getTitle());
        date.setText((task.getDate()));
        time.setText(task.getTime());

        deleteCheckBox.setChecked(false);
        deleteCheckBox.setOnClickListener(v -> {

            if (task != null) {
                task.setDeleted(1); // Mark task as deleted
                SQLiteManager db = SQLiteManager.instanceOfDatabase(getContext());
                db.updateNoteInDB(task); // Update the database
                Task.tasks.remove(task); // Remove task from the list
                notifyDataSetChanged(); // Update the ListView
            }
        });

        return convertView; // Returns the updated view
    }
}
