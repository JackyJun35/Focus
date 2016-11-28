package com.example.jackyjun.focus.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.Model.ToDo;
import com.example.jackyjun.focus.util.CalendarUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackyjun on 16/8/5.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION      = 2;

    /**
     * PLan建表语句
     */
    private static final String CREATE_PLAN = "create table plans(" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "deadline text, " +
            "duration integer, " +
            "description text, " +
            "time_used_second integer, "+
            "start_date text) ";

    // the same as CREATE_PLAN
    private static final String CREATE_PLAN_DONE = "create table plans_done(" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "deadline text, " +
            "duration integer, " +
            "description text, " +
            "time_used_second integer, "+
            "start_date text) ";


    private static final String CREATE_TODO = "create table Todo (" +
            "id integer primary key autoincrement, " +
            "todo_name text, " +
            "todo_deadline text, " +
            "todo_estimated_time integer, " +
            "plan_name text, " +
            "todo_time_used, " +
            "todo_or_done integer default 1, "+
            "last_changed_time integer) ";

    private static final String CREATE_TIME_DATA = "create table TimeData(" +
            "id integer primary key autoincrement, " +
            "time_data_plan_name text," +
            "date text," +
            "second integer )";

    // Plans table name
    private static final String TABLE_PLANS = "plans";
    private static final String TABLE_TODO = "Todo";
    private static final String TABLE_TIME_DATA = "TimeData";

    // Plans Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DEADLINE = "deadline";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TIME_USED = "time_used_second";
    private static final String[] COLUMNS_PLAN = {KEY_ID,KEY_NAME,KEY_DEADLINE,KEY_DURATION,KEY_DESCRIPTION,KEY_TIME_USED};

    //TODO Table Colunms names
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_NAME = "todo_name";
    private static final String KEY_TODO_DEADLINE = "todo_deadline";
    private static final String KEY_TODO_DURATION = "todo_estimated_time";
    private static final String KEY_TODO_PLANNAME = "plan_name";
    private static final String KEY_TODO_TIMEUSED = "todo_time_used";
    private static final String KEY_TODO_TYPE = "todo_or_done";
    private static final String[] COLUMNS_TODO = {KEY_TODO_ID,KEY_TODO_NAME,KEY_TODO_DEADLINE,KEY_TODO_DURATION,KEY_TODO_PLANNAME,KEY_TODO_TIMEUSED,KEY_TODO_TYPE};

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    public MyDatabaseHelper(Context context, String name, int version){
        this(context, name, null,version);
    }

    public MyDatabaseHelper(Context context){
        this(context,"Plan_db",null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAN);
        db.execSQL(CREATE_TODO);
        db.execSQL(CREATE_TIME_DATA);
        db.execSQL(CREATE_PLAN_DONE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS plans");
        db.execSQL("DROP TABLE IF EXISTS Todo");
        db.execSQL("DROP TABLE IF EXISTS TimeData");
        db.execSQL("DROP TABLE IF EXISTS plans_done");
        this.onCreate(db);
    }

    public void addPlan(Plan plan){
        //for logging
        Log.d("addPlan", plan.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, plan.getName()); // get title
        values.put(KEY_DEADLINE, CalendarUtil.fromCalenderToString(plan.getDeadline()));
        values.put(KEY_DURATION,plan.getDuration());
        values.put(KEY_DESCRIPTION,plan.getDescription());

        // 3. insert
        db.insert(TABLE_PLANS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Plan getPlan(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_PLANS, // a. table
                        COLUMNS_PLAN, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build plan object
        Plan plan = new Plan();
        plan.setId(cursor.getInt(0));
        plan.setName(cursor.getString(1));
        plan.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
        plan.setDuration(cursor.getInt(3));
        plan.setDescription(cursor.getString(4));
        //log
        Log.d("getPlan("+id+")", plan.toString());

        // 5. return plan
        return plan;
    }

    public List<Plan> getAllPlans() {
        List<Plan> plans = new LinkedList<Plan>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PLANS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build plan and add it to list
        Plan plan= null;
        if (cursor.moveToFirst()) {
            do {
                plan = new Plan();
                plan.setId(Integer.parseInt(cursor.getString(0)));
                plan.setName(cursor.getString(1));
                plan.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                plan.setDuration(cursor.getInt(3));
                plan.setDescription(cursor.getString(4));

                // Add plan to plans
                plans.add(plan);
            } while (cursor.moveToNext());
        }

        Log.d("getAllPlans()", plans.toString());

        // return plans
        return plans;
    }

    public int updatePlan(Plan plan) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, plan.getName());
        values.put(KEY_DEADLINE, CalendarUtil.fromCalenderToString(plan.getDeadline()));
        values.put(KEY_DURATION,plan.getDuration());
        values.put(KEY_DESCRIPTION,plan.getDescription());

        // 3. updating row
        int i = db.update(TABLE_PLANS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(plan.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    public void deletePlan(Plan plan) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_PLANS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(plan.getId()) }); //selections args

        // 3. close
        db.close();

        //log
        Log.d("deleteBook", plan.toString());

    }

    public void addTodo(ToDo toDo){
        //for logging
        Log.d("addTodo", toDo.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_NAME, toDo.getName()); // get title
        values.put(KEY_TODO_DEADLINE, CalendarUtil.fromCalenderToString(toDo.getDeadline()));
        values.put(KEY_TODO_DURATION,toDo.getEstimatedTime());
        values.put(KEY_TODO_PLANNAME,toDo.getPlanName());
        values.put(KEY_TODO_TYPE,1);
        // 3. insert
        db.insert(TABLE_TODO, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();

    }

    public List<ToDo> getAllTodos(){
        List<ToDo> toDos = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TODO;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build plan and add it to list
        ToDo toDo= null;
        if (cursor.moveToFirst()) {
            do {
                toDo = new ToDo();
                toDo.setId(Integer.parseInt(cursor.getString(0)));
                toDo.setName(cursor.getString(1));
                toDo.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                toDo.setEstimatedTime(cursor.getInt(3));
                toDo.setPlanName(cursor.getString(4));

                // Add todo to TODOS
                toDos.add(toDo);

            } while (cursor.moveToNext());
        }

        Log.d("getAllTodos()", toDo.toString());

        // return plans
        return toDos;
    }

    public List<ToDo> queryTodoByPlanName(String planName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODO,
                                COLUMNS_TODO,
                                KEY_TODO_PLANNAME +"=?",
                                new String[]{planName},
                                null,
                                null,
                                null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        List<ToDo> list = new LinkedList<>();
        while(cursor.moveToNext()){
            //1 means type is To-do,so it gets all of the TO-DO that is not done
            if(cursor.getInt(5)==1){
                ToDo toDo = new ToDo();
                toDo.setId(cursor.getInt(0));
                toDo.setName(cursor.getString(1));
                toDo.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                toDo.setEstimatedTime(cursor.getInt(3));
                toDo.setPlanName(cursor.getString(4));
                list.add(toDo);
            }
        }
        return list;
    }


}


