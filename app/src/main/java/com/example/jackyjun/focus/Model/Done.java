package com.example.jackyjun.focus.Model;

import java.util.Calendar;

/**
 * Created by jackyjun on 16/10/19.
 */
public class Done extends ToDoParent {

    private int type = TYPE_DONE;

    private int estimatedTime;
    private Calendar deadline;
    private boolean isDone = true;

    public Done(){}

    public Done(String name){
        super(name);
    }

    public Done(String name, String planName){
        super(name,planName);
    }

    public Done(String name, int estimatedTime, Calendar deadline) {
        super(name);
        this.estimatedTime = estimatedTime;
        this.deadline = deadline;
    }


    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public ToDo toToDo(){
        return new ToDo(this.name,this.estimatedTime,this.deadline);
    }


}
