package com.example.task_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteManager extends SQLiteOpenHelper {
    private static SQLiteManager sqLiteManager;
    private static final String DATABASE_NAME = "tasksDB";
    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static SQLiteManager instanceOfDatabase(Context context)
    {
        if(sqLiteManager == null)
            sqLiteManager = new SQLiteManager(context);

        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(DATABASE_NAME)
                .append("(")
                .append("Counter")
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append("id")
                .append(" INT, ")
                .append("title")
                .append(" TEXT, ")
                .append("date")
                .append(" TEXT, ")
                .append("time")
                .append(" TEXT, ")
                .append("deleted")
                .append(" INT)");

        db.execSQL(sql.toString());


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTaskToDataBase(Task task){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", task.getId());
        values.put("title", task.getTitle());
        values.put("date", task.getDate());
        values.put("time", task.getTime());
        values.put("deleted", task.getDeleted());

        db.insert(DATABASE_NAME,null,values);

    }

    public void populateTaskListArray(){
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor result = db.rawQuery("SELECT * FROM " + DATABASE_NAME, null)) {
            if(result.getCount() != 0 ) {
                while(result.moveToNext()){
                    int id = result.getInt(1);
                    String title = result.getString(2);
                    String date = result.getString(3);
                    String time = result.getString(4);
                    int deleted = result.getInt(5);
                    if (deleted == 0) {
                        Task task = new Task(id,title,date,time,deleted);
                        Task.tasks.add(task);
                    }

                }
            }
        }
    }
    public void updateNoteInDB(Task task)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", task.getId());
        values.put("title", task.getTitle());
        values.put("date", task.getDate());
        values.put("time", task.getTime());
        values.put("deleted", task.getDeleted());

        sqLiteDatabase.update(DATABASE_NAME, values, "id" + " =? ", new String[]{String.valueOf(task.getId())});
    }
}
