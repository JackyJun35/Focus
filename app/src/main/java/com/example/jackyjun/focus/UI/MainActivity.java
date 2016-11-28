package com.example.jackyjun.focus.UI;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jackyjun.focus.DB.MyDatabaseHelper;
import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.PlanAdapter;
import com.example.jackyjun.focus.R;
import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.TimerReceiver;
import com.example.jackyjun.focus.TimerService;
import com.example.jackyjun.focus.util.PlanDaysCalculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyDatabase myDatabase;
    private MyDatabaseHelper dbHelper;
    private TextView motiWordText;
    private ImageButton setTextButton;
    private TimerReceiver timerReceiver;
    private SharedPreferences pref;
    public static List<Plan> planList = new ArrayList<Plan>();
    public static PlanAdapter mAdapter;
    private int posistion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializing database  Todo!!!!There is no need for a database?!
        myDatabase = MyDatabase.getInstance(this);
        dbHelper = new MyDatabaseHelper(this,"PLAN_db",1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPlanActivity.class);
                startActivityForResult(intent,2);
            }
        });
        //setting up recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.main_activity_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //load from database the plan information
        loadPlanData();

        //broadcast receiver for timer service
        registerBroadcastReceiver();

        //set up edit text for the motivational words
        setUpMotivationalCard();

        //send out notification about the deadline
        sendDeadlineNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_stats) {
            Intent intent = new Intent(this,StatsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_advice) {
            Intent intent = new Intent(this,AdviceActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_history) {
            Intent intent = new Intent(this,HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about_app) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlanData();
        Log.d("Fuck MainActivity","MainActivity->onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    Plan plan = new Plan();
                    //pass in the plan data
                    String planName = data.getStringExtra("planName");
                    Calendar deadline = (Calendar) data.getSerializableExtra("deadline");
                    if(data.getStringExtra("duration")!=null){
                        int duration = Integer.parseInt(data.getStringExtra("duration"));
                        plan.setDuration(duration);
                    }
                    String description = data.getStringExtra("description");

                    Log.d("FirstActivity", planName);
                    if(planName!=null){plan.setName(planName);}
                    if(deadline!=null){plan.setDeadline(deadline);}
                    if(description!=null){plan.setDescription(description);}
                    plan.setTotalSecondSpent(0);
                    //Calendar.getInstance will return a calendar with today's date
                    plan.setStartDate(Calendar.getInstance());
                    //Log
                    Log.d("Start Date",plan.getStartDate().toString());

                    planList.add(plan);
                    mAdapter.notifyDataSetChanged(); //present the new plan data on the screen

                    myDatabase.addPlan(plan); //Add plan to the database
                }
                break;
            default:
        } }

    private void registerBroadcastReceiver(){
        timerReceiver = new TimerReceiver();
        IntentFilter intentFilter = new IntentFilter("com.example.jackyjun.focus.TIMER_CHANGED");
        registerReceiver(timerReceiver,intentFilter);

        ServiceStopReceiver serviceStopReceiver = new ServiceStopReceiver();
        IntentFilter intentFilter2 = new IntentFilter("com.example.jackyjun.focus.SERVICE_STOP");
        registerReceiver(serviceStopReceiver,intentFilter2);

        ServiceStartReceiver serviceStartReceiver = new ServiceStartReceiver();
        IntentFilter intentFilter3 = new IntentFilter("com.example.jackyjun.focus.SERVICE_START");
        registerReceiver(serviceStartReceiver,intentFilter3);
    }

    private void setUpMotivationalCard(){
        //get the data
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        motiWordText = (TextView) findViewById(R.id.moti_text_main_acitivty);
        String text = pref.getString("MotiWord","自律使我自由");
        motiWordText.setText(text);

        //set the data
        setTextButton = (ImageButton) findViewById(R.id.edit_moti_button);
        setTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.item_alert_dialog_edit_text, null);
                android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("激励自己的话");
                dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText motiEditText = (EditText) dialogView.findViewById(R.id.edit_moti_text);
                        String text = motiEditText.getText().toString().trim();
                        SharedPreferences.Editor editor = pref.edit();
                        if(!text.equals("")){
                            motiWordText.setText(text);
                            editor.putString("MotiWord",text);
                            editor.apply();
                        }
                        dialog.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void sendDeadlineNotification(){
        for (final Plan plan : planList){
            long countDownDays = PlanDaysCalculator.countdownDays(plan.getDeadline());
            long originalDuration = PlanDaysCalculator.countDays(plan.getStartDate(),plan.getDeadline());
            if (countDownDays <= 0.25*originalDuration && countDownDays>0){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("项目到期提醒")
                                .setContentText("你的"+plan.getName()+"计划只剩下"+countDownDays+"天啦，加油！");
                setUpNotification(mBuilder);
            }
            if(countDownDays == 0){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("项目到期提醒")
                                .setContentText("你的"+plan.getName()+"计划今天就要到期啦，加油！");
                setUpNotification(mBuilder);
            }
            if (countDownDays<0){
                //set up notification
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("项目到期提醒")
                                .setContentText("你的"+plan.getName()+"计划今天已经逾期啦，是否要延期？");
                setUpNotification(mBuilder);

                //set up dialog
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
                dialog.setTitle("项目到期提醒");
                dialog.setMessage("你的"+plan.getName()+"计划已经到时间啦，你是否已经完成了呢");
                dialog.setCancelable(true);
                dialog.setPositiveButton("标记为已完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyDatabase db = MyDatabase.getInstance(MainActivity.this);
                        db.deletePlan(plan);
                        planList.remove(plan);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("我需要延期", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final Calendar mCalendar = Calendar.getInstance();
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            }
                        };
                        DatePickerDialog dpd =  new DatePickerDialog(MainActivity.this, date, mCalendar
                                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                                mCalendar.get(Calendar.DAY_OF_MONTH));
                        Date date2= Calendar.getInstance().getTime();
                        dpd.getDatePicker().setMinDate(date2.getTime());
                        dpd.show();
                        plan.setDeadline(mCalendar);
                        mAdapter.notifyDataSetChanged();
                        myDatabase = MyDatabase.getInstance(MainActivity.this);
                        myDatabase.updatePlan(plan);
                    }
                });
                dialog.create().show();
            }
        }
    }

    //helper method
    private void setUpNotification(NotificationCompat.Builder mBuilder){
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());
    }

    private void loadPlanData(){
        planList = myDatabase.getAllPlans();
        mAdapter = new PlanAdapter(this,planList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new PlanAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Intent intent = new Intent(MainActivity.this,PlanInfoActivity.class);
                intent.putExtra("PlanName",planList.get(mRecyclerView.getChildLayoutPosition(view)).getName());
                posistion = mRecyclerView.getChildAdapterPosition(view);
                intent.putExtra("Position",mRecyclerView.getChildLayoutPosition(view));
                startActivity(intent);
            }
        });
    }

    class ServiceStopReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(TimerService.isServiceRunning()){
            unbindService(PlanAdapter.connection);
            Intent stopIntent = new Intent(MainActivity.this,TimerService.class);
            stopService(stopIntent);
            }
        }
    }

    class ServiceStartReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent startIntent = new Intent(MainActivity.this, TimerService.class);
            bindService(startIntent,PlanAdapter.connection,BIND_AUTO_CREATE);
        }
    }
}

