package com.example.jackyjun.focus.Model;

import android.content.Context;
import android.util.Log;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.DB.MyDatabaseHelper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class is responsible for the management of TODOs
 * Since it is a component for plans,it will be stored as a hashmap
 * Created by jackyjun on 16/8/12.
 */
public class ToDoManager {
    private static ToDoManager sTodoManager;
    private HashMap<String,List<ToDo>> todoListMap;
    private HashMap<String,List<Done>> doneListMap;
    private List<ToDo> toDoList;
    private List<Done> doneList;
    private MyDatabase db;

    public synchronized static ToDoManager get(Context context) {
        if (sTodoManager == null) {
            sTodoManager = new ToDoManager(context);
        }
        return sTodoManager;
    }

    // need to connect to the database
    private ToDoManager(Context context){
        db = MyDatabase.getInstance(context); //// TODO: 16/8/20 potential bug:database version
        todoListMap = new HashMap<>();
        doneListMap = new HashMap<>();
    }

    public ToDoManager(Context context,String planName){
        db = MyDatabase.getInstance(context); //// TODO: 16/8/20 potential bug:database version
        todoListMap = new HashMap<>();
        doneListMap = new HashMap<>();
        toDoList = db.queryTodoByPlanName(planName);
        doneList = db.queryDoneByPlanName(planName);
        todoListMap.put(planName,toDoList);
        doneListMap.put(planName,doneList);
    }

    public List<ToDo> getToDoList(String planName){
        return todoListMap.get(planName);
    }

    public ToDo getToDo(String planName,int id){
        List<ToDo> toDos = todoListMap.get(planName);
        for(ToDo toDo:toDos){
            if(toDo.getId() == id){
                return toDo;
            }
        }
        return null;
    }

    public List<Done> getDoneList(String planName){
        return doneListMap.get(planName);
    }

    public Done getDone(String planName,int id){
        List<Done> doneList = doneListMap.get(planName);
        for(Done done : doneList){
            if(done.getId() == id){
                return done;
            }
        }
        return null;
    }

    public void addTodo(ToDo toDo){
        todoListMap.get(toDo.getPlanName()).add(toDo);
        db.addTodo(toDo);
    }
}
