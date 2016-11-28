package com.example.jackyjun.focus.Model;

import java.util.ArrayList;

/**
 * Created by jackyjun on 16/8/1.
 */
public class PlanManager {
    private ArrayList<Plan> planArrayList;

    public void addPlan(Plan plan){planArrayList.add(plan);}

    public void removePlan(Plan plan){planArrayList.remove(plan);}


}
