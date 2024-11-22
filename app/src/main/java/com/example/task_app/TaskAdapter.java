package com.example.task_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
        Task note = getItem(position); // Get the current task

        if (convertView == null) { // Creates a new view if it doesn't exist
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        // Find the elements in the task_item.xml layout
        TextView title = convertView.findViewById(R.id.cellTitle);
        TextView date = convertView.findViewById(R.id.date);
        TextView time = convertView.findViewById(R.id.time);

        // Set to display
        title.setText(note.getTitle());
        date.setText((note.getDate()));
        time.setText(note.getTime());

        return convertView; // Returns the updated view
    }
}
