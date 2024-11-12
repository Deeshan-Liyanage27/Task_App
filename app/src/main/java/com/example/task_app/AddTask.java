package com.example.task_app;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
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

import java.util.Objects;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_task);
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
        dateText.setEnabled(false);
        dateText.setTextColor(Color.BLACK);

        //Tracks the user interaction with the calendar and changes the date field accordingly
        CalendarView calendar = findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(
                new CalendarView.OnDateChangeListener() {
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        EditText dateText = findViewById(R.id.editTextDate);
                        CharSequence date =  dayOfMonth +"/"+ month +"/" + year;
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
                    }
                },0,0,true);
                timePickerDialog.show();
            }
        });
    }

    public void addDate(View v){
        CalendarView calendar = findViewById(R.id.calendarView);
        EditText date = findViewById(R.id.editTextDate);
        String dateC = String.valueOf(calendar.getDate());
        Toast.makeText(this,dateC,Toast.LENGTH_LONG).show();
        //date.setText(dateC);
       //calendar.setDate(calendar.getDate());
        //Log.d("presss", String.valueOf(calendar.getDate()));
    }
}