package com.example.task_app;

import java.util.ArrayList;

public class Task {
    public static ArrayList<Task> tasks = new ArrayList<>();
    private int id;
    private String title;
    private String date;
    private String time;
    private  int deleted;

    public Task(int id, String title, String date, String time, int deleted) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public static ArrayList<Task> nonDeletedNotes()
    {
        ArrayList<Task> nonDeleted = new ArrayList<>();
        for(Task task : tasks)
        {
            if(task.getDeleted() == 0)
                nonDeleted.add(task);
        }

        return nonDeleted;
    }

}
