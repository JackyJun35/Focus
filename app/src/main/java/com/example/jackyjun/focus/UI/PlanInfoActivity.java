package com.example.jackyjun.focus.UI;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.ExpandableList.DoneExpandableAdapter;
import com.example.jackyjun.focus.Model.Done;
import com.example.jackyjun.focus.Model.ExpandableViewParent;
import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.Model.ToDo;
import com.example.jackyjun.focus.Model.ToDoManager;
import com.example.jackyjun.focus.PlanAdapter;
import com.example.jackyjun.focus.R;
import com.example.jackyjun.focus.ExpandableList.TodoExpandableAdapter;
import com.example.jackyjun.focus.TimerService;
import com.example.jackyjun.focus.util.CalendarUtil;
import com.example.jackyjun.focus.util.DisplayMetricsConverter;
import com.example.jackyjun.focus.util.PlanDaysCalculator;
import com.example.jackyjun.focus.util.TimeAndSecondsConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jackyjun on 16/8/12.
 */
public class PlanInfoActivity extends AppCompatActivity {

    public static String planName;

    private Plan mPlan;
    private int position; //position pass by main activity
    public static RecyclerView recyclerView1; //new TO-DO
    public static RecyclerView recyclerView2;//recycled DONE
    public static TodoExpandableAdapter mExpandableAdapter1; //adapter for TODO
    public static DoneExpandableAdapter mExpandableAdapter2; //adapter for DONE
    private TextView chronometer;
    private FloatingActionButton fab;
    private ImageButton addButton;
    private MyDatabase db;
    private PlanInfoTimerReceiver timerReceiver;
    private ToDoManager toDoManager;

    private int numOfDones;
    private static final int HEIGHT_OF_DONE = 60;

    private boolean isCounting = false;
    public boolean isActive = false;
    private TimerService mBoundService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((TimerService.TimerBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_info);

        loadPlanData();
        //first set up the RecyclerView
        setUpRecyclerView();

        //set up the chronometer for count up
        chronometer = (TextView) findViewById(R.id.chronometer_in_plan_info);

        //set up the mPlan card info
        setUpPlanCardView();
        setUpDescriptionCard();

        //Register broadcast receiver
        registerBroadcastReceiver();

        //set up the fab
        setUpFab();
        Log.d("PlanInfoActivity","onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
        Log.d("PlanInfoActivity","onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("PlanInfoActivity","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (TimerService.isServiceRunning()){
            unbindService(connection);
        }
        unregisterReceiver(timerReceiver);
        isActive = false;
        Log.d("PlanInfoActivity","onDestroy");
    }

    /**
     *
     * @return list of parent objects: todo and Done
     */
    private ArrayList<ParentListItem> generateTodos() {
        toDoManager = new ToDoManager(this,mPlan.getName());

        List<ToDo> toDos = toDoManager.getToDoList(mPlan.getName());
        ExpandableViewParent parent1 = new ExpandableViewParent(ExpandableViewParent.STRING_TODO);

        LinkedList<Object> childList = new LinkedList<>();
        parent1.setChildItemList(childList);

        if(toDos!=null){
            for (ToDo todo:toDos){
                parent1.getChildItemList().add(todo);

            }
        }

        ArrayList<ParentListItem> parentItemList = new ArrayList<>();
        parentItemList.add(parent1);
        return parentItemList;
    }

    private ArrayList<ParentListItem> generateDones(){
        toDoManager = new ToDoManager(this,mPlan.getName());
        List<Done> dones = toDoManager.getDoneList(mPlan.getName());

        //get the number of dones to set layout dynamically in the method setUpRecyclerView();
        numOfDones = dones.size();

        Log.d("DoneList",dones.toString());

        ExpandableViewParent parent2 = new ExpandableViewParent(ExpandableViewParent.STRING_DONE);

        LinkedList<Object> childList = new LinkedList<>();
        parent2.setChildItemList(childList);

        if(dones!=null){
            for (Done done : dones){
                parent2.getChildItemList().add(done);
            }
        }
        ArrayList<ParentListItem> parentObjects = new ArrayList<>();
        parentObjects.add(parent2);
        return parentObjects;
    }

    private void setUpFab(){
        fab = (FloatingActionButton) findViewById(R.id.fab_in_plan_info);
        if (PlanAdapter.mBoundService!=null&&TimerService.isServiceRunning()){
            Intent intent = new Intent(PlanInfoActivity.this,TimerService.class);
            intent.putExtra("Plan", mPlan);
            intent.putExtra("Position",position);
            bindService(intent,connection,Service.BIND_AUTO_CREATE);
            fab.setImageResource(R.drawable.ic_pause_white_48dp);
            isCounting = true;
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCounting) {
                    Intent intent = new Intent(PlanInfoActivity.this,TimerService.class);
                    unbindService(connection);
                    sendServiceStopBroadCast();
                    stopService(intent);
                    fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                    isCounting = false;
                }else if (!isCounting){
                    Intent intent = new Intent(PlanInfoActivity.this,TimerService.class);
                    intent.putExtra("Plan", mPlan);
                    intent.putExtra("Position",position);
                    bindService(intent,connection,BIND_AUTO_CREATE);
                    sendServiceStartBroadcast();
                    fab.setImageResource(R.drawable.ic_pause_white_48dp);
                    isCounting = true;
                }
            }
        });
    }

    //this method get the name from MainActivity and display the data for a specific Plan.
    private void loadPlanData(){
        Intent intent = getIntent();
        planName = intent.getStringExtra("PlanName");
        position = intent.getIntExtra("Position",0);
        db = MyDatabase.getInstance(this);
        mPlan = db.queryPlanByPlanName(planName);
    }

    //this method set up the recyclerview in the planinfo class UI
    private void setUpRecyclerView(){
        recyclerView1 = (RecyclerView) findViewById(R.id.recycler_view_1_in_plan_info);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_2_in_plan_info);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView1.setLayoutManager(linearLayoutManager1);
        recyclerView2.setLayoutManager(linearLayoutManager2);

        toDoManager = new ToDoManager(this,mPlan.getName());
        List<ToDo> toDoList = toDoManager.getToDoList(mPlan.getName());
        List<Done> doneList = toDoManager.getDoneList(mPlan.getName());

        mExpandableAdapter1 = new TodoExpandableAdapter(this,generateTodos());
        //mExpandableAdapter1.setCustomParentAnimationViewId(R.id.parent_list_item_expand_arrow);
        //mExpandableAdapter1.setParentClickableViewAnimationDefaultDuration();
        //mExpandableAdapter1.setParentAndIconExpandOnClick(true);
        recyclerView1.setAdapter(mExpandableAdapter1);

        mExpandableAdapter2 = new DoneExpandableAdapter(this,generateDones());
        recyclerView2.setAdapter(mExpandableAdapter2);

        recyclerView2.getLayoutParams().height = DisplayMetricsConverter.dpToPx(this, numOfDones * HEIGHT_OF_DONE + 150);

        //set the list for manipulating the data of the other adapter.

        mExpandableAdapter1.setDoneExpandableAdapter(mExpandableAdapter2);
        mExpandableAdapter2.setToDoExpandableAdapter(mExpandableAdapter1);
    }

    private void setUpPlanCardView(){
        final TextView deadlineText = (TextView) findViewById(R.id.deadline_text_view);
        final TextView daySpentText = (TextView) findViewById(R.id.day_spent_text_view);
        final TextView progressText = (TextView) findViewById(R.id.progress_text_view);
        final TextView planNameText = (TextView) findViewById(R.id.plan_title_in_plan_info_card);
        ImageButton editButton = (ImageButton) findViewById(R.id.edit_plan_info_card);
        planNameText.setText(mPlan.getName());
        if(mPlan.getDeadline()!=null){
            deadlineText.setText(CalendarUtil.fromCalenderToString(mPlan.getDeadline()));
        }else{
            deadlineText.setText("无");
        }
        daySpentText.setText(Long.toString(PlanDaysCalculator.countupDays(mPlan.getStartDate())));
        if(mPlan.getDuration()!=0){
            progressText.setText(mPlan.getProgressInString());
        }else{
            progressText.setText("0%");
        }
        chronometer.setText(TimeAndSecondsConverter.fromMilisecondToString(mPlan.getTotalSecondSpent()*1000));
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(PlanInfoActivity.this);
                final View dialogView = layoutInflater.inflate(R.layout.item_alert_dialog_edit_plan_info,null);
                final EditText planTitle = (EditText) dialogView.findViewById(R.id.alert_dialog_edit_text_plan_name);
                final CalendarView calendarView = (CalendarView) dialogView.findViewById(R.id.alert_dialog_calendar_plan_deadline);
                final EditText estimatedTime = (EditText) dialogView.findViewById(R.id.alert_dialog_plan_estimated_time);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlanInfoActivity.this);
                mBuilder.setTitle("修改你的计划");
                mBuilder.setView(dialogView);

                planTitle.setText(mPlan.getName());
                planTitle.setSelection(planTitle.getText().length());
                calendarView.setMinDate(Calendar.getInstance().getTime().getTime());
                if (PlanDaysCalculator.countdownDays(mPlan.getDeadline())>0){
                    calendarView.setDate(mPlan.getDeadline().getTime().getTime());
                }
                estimatedTime.setText(String.valueOf(mPlan.getDuration()));


                mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(calendarView.getDate());
                        mPlan.setName(planTitle.getText().toString().trim());
                        mPlan.setDuration(Integer.parseInt(estimatedTime.getText().toString().trim()));
                        mPlan.setDeadline(calendar);
                        db.updatePlan(mPlan);

                        Log.d("PlanTitle",planTitle.getText().toString().trim());
                        Log.d("mPlan",mPlan.toString());

                        planNameText.setText(planTitle.getText().toString().trim());
                        if(mPlan.getDeadline()!=null){
                            deadlineText.setText(CalendarUtil.fromCalenderToString(mPlan.getDeadline()));
                        }else{
                            deadlineText.setText("无");
                        }
                        daySpentText.setText(Long.toString(PlanDaysCalculator.countupDays(mPlan.getStartDate())));
                        if(mPlan.getDuration()!=0){
                            progressText.setText(mPlan.getProgressInString());
                        }else{
                            progressText.setText("0%");
                        }
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.create().show();


            }
        });
    }

    private void setUpDescriptionCard(){
        final TextView descriptionText = (TextView) findViewById(R.id.text_description);
        descriptionText.setText(mPlan.getDescription());
        ImageButton descriptionEditButton = (ImageButton) findViewById(R.id.description_edit_button_plan_info);
        descriptionEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(PlanInfoActivity.this);
                final View dialogView = inflater.inflate(R.layout.item_alert_dialog_edit_description,null);
                final EditText descriptionEditText = (EditText) dialogView.findViewById(R.id.edit_description_text_alert_dialog);
                AlertDialog.Builder builder = new AlertDialog.Builder(PlanInfoActivity.this);
                builder.setView(dialogView);
                builder.setTitle("项目描述编辑");

                descriptionEditText.setText(mPlan.getDescription());
                descriptionEditText.setSelection(mPlan.getDescription().length());

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPlan.setDescription(descriptionEditText.getText().toString().trim());
                        db.updatePlan(mPlan);
                        descriptionText.setText(mPlan.getDescription());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void registerBroadcastReceiver(){
        timerReceiver = new PlanInfoTimerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.jackyjun.focus.TIMER_CHANGE");
        registerReceiver(timerReceiver,intentFilter);
    }

    private void sendServiceStartBroadcast(){
        Intent intent = new Intent("com.example.jackyjun.focus.SERVICE_START");
        sendBroadcast(intent);
    }

    private void sendServiceStopBroadCast(){
        Intent intent = new Intent("com.example.jackyjun.focus.SERVICE_STOP");
        sendBroadcast(intent);
    }

    //Not a very elegant solution but it works
    public void changeChronometerText(String currentTime){
        chronometer.setText(currentTime);
    }

    class PlanInfoTimerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //This string has the format of HH:MM:SS
            Plan plan = (Plan) intent.getSerializableExtra("Plan");
            String elapsedTime = intent.getStringExtra("RealTime");
            //I use planName but not id to identify if two plan are the same,so I have to prevent
            //two plan with same names
            if (mPlan.getName().equals(plan.getName())){
                changeChronometerText(elapsedTime);
                fab.setImageResource(R.drawable.ic_pause_white_48dp);
                isCounting = true;
            }
            Log.d("PlanInfoReceiver","onReceive");
        }
    }
}

