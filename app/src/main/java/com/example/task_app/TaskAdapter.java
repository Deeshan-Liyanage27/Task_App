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
        super(context,0,tasks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Task note = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }
        TextView title = convertView.findViewById(R.id.cellTitle);

        title.setText(note.getTitle());

        return convertView;
    }
}
