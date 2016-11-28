package com.example.jackyjun.focus.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jackyjun.focus.Model.Done;
import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.Model.TimeData;
import com.example.jackyjun.focus.Model.ToDo;
import com.example.jackyjun.focus.util.CalendarUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackyjun on 16/8/13.
 */
public class MyDatabase {

    // Plans table name
    private static final String TABLE_PLANS = "plans";
    private static final String TABLE_TODO = "Todo";
    private static final String TABLE_TIME_DATA = "TimeData";
    private static final String TABLE_PLANS_DONE = "plans_done";

    // Plans Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DEADLINE = "deadline";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TIME_USED = "time_used_second";
    private static final String KEY_START_DATE = "start_date";
    private static final String[] COLUMNS_PLAN = {KEY_ID,KEY_NAME,KEY_DEADLINE,KEY_DURATION,KEY_DESCRIPTION,KEY_TIME_USED,KEY_START_DATE};

    //TODO Table Colunms names
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_NAME = "todo_name";
    private static final String KEY_TODO_DEADLINE = "todo_deadline";
    private static final String KEY_TODO_DURATION = "todo_estimated_time";
    private static final String KEY_TODO_PLANNAME = "plan_name";
    private static final String KEY_TODO_TIMEUSED = "todo_time_used";
    private static final String KEY_TODO_TYPE = "todo_or_done";
    private static final String KEY_TODO_LAST_CHANGED_TIME = "last_changed_time";
    private static final String[] COLUMNS_TODO = {KEY_TODO_ID,KEY_TODO_NAME,KEY_TODO_DEADLINE,KEY_TODO_DURATION,KEY_TODO_PLANNAME,KEY_TODO_TIMEUSED,KEY_TODO_TYPE,KEY_TODO_LAST_CHANGED_TIME};

    private static final String KEY_TIME_DATA_PLAN_NAME = "time_data_plan_name";
    private static final String KEY_DATE = "date";
    private static final String KEY_SECOND = "second";
    private static final String[] COLUMNS_TIME_DATA = {KEY_TIME_DATA_PLAN_NAME,KEY_DATE, KEY_SECOND};

    public static final String DB_NAME = "Plan_db";

    public static final int VERSION = 2;

    public static MyDatabase mDatabase;

    private SQLiteDatabase db;

    private MyDatabaseHelper dbHelper;

    private MyDatabase(Context context){
        dbHelper = new MyDatabaseHelper(context,DB_NAME,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static MyDatabase getInstance(Context context){
        if (mDatabase == null){
            mDatabase = new MyDatabase(context);
        }
        return mDatabase;
    }

    public void addPlan(Plan plan){
        //for logging
        Log.d("addPlan", plan.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, plan.getName()); // get title
        values.put(KEY_DEADLINE, CalendarUtil.fromCalenderToString(plan.getDeadline()));
        values.put(KEY_DURATION,plan.getDuration());
        values.put(KEY_DESCRIPTION,plan.getDescription());
        values.put(KEY_TIME_USED,0);
        values.put(KEY_START_DATE, CalendarUtil.fromCalenderToString(plan.getStartDate()));

        // 3. insert
        db.insert(TABLE_PLANS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        //db.close();
    }

    public Plan getPlan(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        plan.setTotalSecondSpent(cursor.getInt(5));
        plan.setStartDate(CalendarUtil.fromStringToCalender(cursor.getString(6)));
        //log
        Log.d("getPlan("+id+")", plan.toString());

        // 5. return plan
        return plan;
    }

    public List<Plan> getAllPlans() {
        List<Plan> plans = new LinkedList<Plan>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PLANS;

        // 2. get reference to writable DB
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
                plan.setTotalSecondSpent(cursor.getInt(5));
                plan.setStartDate(CalendarUtil.fromStringToCalender(cursor.getString(6)));

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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, plan.getName());
        values.put(KEY_DEADLINE, CalendarUtil.fromCalenderToString(plan.getDeadline()));
        values.put(KEY_DURATION,plan.getDuration());
        values.put(KEY_DESCRIPTION,plan.getDescription());
        values.put(KEY_TIME_USED,plan.getTotalSecondSpent());
        values.put(KEY_START_DATE, CalendarUtil.fromCalenderToString(plan.getStartDate()));

        // 3. updating row
        int i = db.update(TABLE_PLANS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(plan.getId()) }); //selection args

        // 4. close
        //db.close();

        return i;

    }

    public void deletePlan(Plan plan) {

        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_PLANS, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(plan.getId()) }); //selections args

        // 3. close
        //db.close();

        //log
        Log.d("deletePlan", plan.toString());

    }

    public List<Plan> getAllFinishedPlan() {
        List<Plan> plans = new LinkedList<Plan>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_PLANS_DONE;

        // 2. get reference to writable DB
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
                plan.setTotalSecondSpent(cursor.getInt(5));
                plan.setStartDate(CalendarUtil.fromStringToCalender(cursor.getString(6)));

                // Add plan to plans
                plans.add(plan);
            } while (cursor.moveToNext());
        }

        Log.d("getAllFinishedPlans()", plans.toString());

        // return plans
        return plans;
    }

    public void markPlanAsFinished(Plan plan) {

        deletePlan(plan);

        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, plan.getName()); // get title
        values.put(KEY_DEADLINE, CalendarUtil.fromCalenderToString(plan.getDeadline()));
        values.put(KEY_DURATION,plan.getDuration());
        values.put(KEY_DESCRIPTION,plan.getDescription());
        values.put(KEY_TIME_USED,0);
        values.put(KEY_START_DATE, CalendarUtil.fromCalenderToString(plan.getStartDate()));

        // 3. insert
        db.insert(TABLE_PLANS_DONE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        //db.close();
    }

    public void deleteFinishedPlan(Plan plan) {
        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_PLANS_DONE, //table name
                KEY_ID+" = ?",  // selections
                new String[] { String.valueOf(plan.getId()) }); //selections args

        // 3. close
        //db.close();
    }

    public void restartFinishedPlan(Plan plan) {
        deleteFinishedPlan(plan);
        addPlan(plan);
    }

    public void addTodo(ToDo toDo){
        //for logging
        Log.d("addTodo", toDo.toString());

        // 1. get reference to writable DB


        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_NAME, toDo.getName()); // get title

        // if user specify deadline
        if(toDo.getDeadline() != null){
            values.put(KEY_TODO_DEADLINE, CalendarUtil.fromCalenderToString(toDo.getDeadline()));
        }
        values.put(KEY_TODO_DURATION,toDo.getEstimatedTime());
        values.put(KEY_TODO_PLANNAME,toDo.getPlanName());
        values.put(KEY_TODO_TYPE,1);

        values.put(KEY_TODO_LAST_CHANGED_TIME,System.currentTimeMillis());
        // 3. insert
        db.insert(TABLE_TODO, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        //db.close();

    }

    public int fromTodoToDone(ToDo todo) {
        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(KEY_TODO_TYPE, 0);
        values.put(KEY_TODO_LAST_CHANGED_TIME,System.currentTimeMillis());

        // 3. updating row
        int i = db.update(TABLE_TODO, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(todo.getId()) }); //selection args

        return i;
    }

    // Remember, this method does not take into account deadline which may need revision after.
    public int fromDoneToTodo(Done done){
        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(KEY_TODO_TYPE, 1);
        values.put(KEY_TODO_LAST_CHANGED_TIME,System.currentTimeMillis());

        // 3. updating row
        int i = db.update(TABLE_TODO, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(done.getId()) }); //selection args

        return i;
    }

    public List<ToDo> getAllTodos(){
        List<ToDo> toDos = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TODO;

        // 2. get reference to writable DB
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

                toDo.setLastChangedTime(cursor.getLong(7));
                // Add todo to TODOS
                toDos.add(toDo);

            } while (cursor.moveToNext());
        }
        if (toDo!=null){
            Log.d("getAllTodos()", toDo.toString());
        }

        // return todos
        return toDos;
    }

    public List<ToDo> queryTodoByPlanName(String planName){
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

        if(cursor.moveToFirst()){
            do {
            //1 means type is To-do,so it gets all of the TO-DO that is not done
                if(cursor.getInt(6)==1){
                    ToDo toDo = new ToDo();
                    toDo.setId(cursor.getInt(0));
                    toDo.setName(cursor.getString(1));
                    toDo.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                    toDo.setEstimatedTime(cursor.getInt(3));
                    toDo.setPlanName(cursor.getString(4));
                    toDo.setLastChangedTime(cursor.getLong(7));
                    list.add(toDo);
                }
            } while (cursor.moveToNext());
        }

        Log.d("getAllTodos",list.toString());

        return list;
    }

    public List<Done> queryDoneByPlanName(String planName){
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
        List<Done> list = new LinkedList<>();

        if(cursor.moveToFirst()){
            do{
            //0 means type is done,so it gets all of the TO-DO that is done
                if(cursor.getInt(6)==0){
                    Done done = new Done();
                    done.setId(cursor.getInt(0));
                    done.setName(cursor.getString(1));
                    done.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                    done.setEstimatedTime(cursor.getInt(3));
                    done.setPlanName(cursor.getString(4));
                    done.setLastChangedTime(cursor.getLong(7));
                    list.add(done);
            }
            } while(cursor.moveToNext());
        }
        Log.d("getAllDone",list.toString());
        return list;
    }

    public Plan queryPlanByPlanName(String planName){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLANS,
                COLUMNS_PLAN,
                KEY_NAME +"=?",
                new String[]{planName},
                null,
                null,
                null);
        if(cursor!=null&&cursor.moveToFirst()){
            Plan plan = new Plan();
            plan.setId(cursor.getInt(0));
            plan.setName(cursor.getString(1));
            plan.setDeadline(CalendarUtil.fromStringToCalender(cursor.getString(2)));
            plan.setDuration(cursor.getInt(3));
            plan.setDescription(cursor.getString(4));
            plan.setTotalSecondSpent(cursor.getInt(5));
            plan.setStartDate(CalendarUtil.fromStringToCalender(cursor.getString(6)));
            //log
            Log.d("getPlan("+planName+")", plan.toString());
            return plan;
        }
        return null;
    }

    public void addTimeData(TimeData timeData){
        // 1. get reference to writable DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();

        values.put(KEY_TIME_DATA_PLAN_NAME,timeData.getPlanName());
        values.put(KEY_DATE, CalendarUtil.fromCalenderToString(timeData.getDate()));
        values.put(KEY_SECOND,timeData.getSecond());
        // 3. insert
        db.insert(TABLE_TIME_DATA, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
    }

    public List<TimeData> getAllTimeData(){
        List<TimeData> timeDatas = new LinkedList<>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_TIME_DATA;

        // 2. get reference to writable DB
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build plan and add it to list
        TimeData timeData= null;
        if (cursor.moveToFirst()) {
            do {
                timeData = new TimeData();
                timeData.setPlanName(cursor.getString(1));
                timeData.setDate(CalendarUtil.fromStringToCalender(cursor.getString(2)));
                timeData.setSecond(cursor.getInt(3));

                timeDatas.add(timeData);

            } while (cursor.moveToNext());
        }
        if (timeData!=null){
            Log.d("getAllTimeData()", timeData.toString());
        }
        return timeDatas;
    }


}
