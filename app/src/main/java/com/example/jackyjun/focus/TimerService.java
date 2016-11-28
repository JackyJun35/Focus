package com.example.jackyjun.focus;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.DB.MyDatabaseHelper;
import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.Model.TimeData;
import com.example.jackyjun.focus.UI.MainActivity;
import com.example.jackyjun.focus.UI.PlanInfoActivity;
import com.example.jackyjun.focus.util.TimeAndSecondsConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jackyjun on 16/9/5.
 */
public class TimerService extends Service {

    private static final int NOTIF_ID = 1234;

    private String TAG = "TimerService";
    private Timer timer = null;
    private SimpleDateFormat sdf = null;
    private Intent timeIntent = null;
    private Bundle bundle = null;
    private long starttime;
    private Plan mPlan;
    private int position;
    private IBinder binder = new TimerBinder();

    private NotificationCompat.Builder mBuilder;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private TimerServiceReceiver timerServiceReceiver;

    public static TimerService instance = null;

    public static boolean isServiceRunning(){
        return instance!=null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"TimerService->onBind");
        mPlan = (Plan) intent.getSerializableExtra("Plan");
        position = intent.getIntExtra("Position",0);
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.i(TAG,"TimerService->onCreate");
        this.init();
        //Notification for foreground
        setUpNotification();

        //register BraodcastReceiver
         timerServiceReceiver = new TimerServiceReceiver();
         IntentFilter intentFilter = new IntentFilter("com.example.jackyjun.focus.TIMER_CHANGE");
         registerReceiver(timerServiceReceiver,intentFilter);

        //A timer task run every second to send broadcast to the view to update the UI.
        starttime = System.currentTimeMillis();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mPlan.setTotalSecondSpent(mPlan.getTotalSecondSpent()+1);
                sendTimeChangedBroadcast();
            }
        },1000,1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        //we need to stop the timer so that it won't keep updating time while the service is off
        timer.cancel();
        timer.purge();
        //When the service is off, I need to update the newest time to the database so that next time I open it
        //the time will be the new one.
        long elapsedSecond = (System.currentTimeMillis() - starttime)/1000;

        TimeData timeData = new TimeData(mPlan.getName(),elapsedSecond, Calendar.getInstance());
        MyDatabase database = MyDatabase.getInstance(getApplicationContext());
        database.updatePlan(mPlan);
        database.addTimeData(timeData);
        Log.d(TAG,"TimerService->onDestroyed");

        unregisterReceiver(timerServiceReceiver);
    }

    public class TimerBinder extends Binder{
        public TimerService getService(){
            return TimerService.this;
        }
    }

    private void init(){
        timer = new Timer();
        timeIntent = new Intent();
        bundle = new Bundle();
    }

    private void sendTimeChangedBroadcast(){
        timeIntent.putExtra("RealTime",getElapsedTime());
        timeIntent.putExtra("Plan",mPlan);
        timeIntent.putExtra("Position",position);
        timeIntent.setAction("com.example.jackyjun.focus.TIMER_CHANGE");
        sendBroadcast(timeIntent);
    }

    //this method is used to convert time into the format hh:mm:ss
    private String getElapsedTime(){
        long endtime = System.currentTimeMillis();
        long elapseTime = endtime - starttime;
        String ans = TimeAndSecondsConverter.fromMilisecondToString(elapseTime);
        Log.d("Time used",ans);
        return ans;
    }

    private String getDisplayedTimeForMainActivity(int id){
        MyDatabase myDatabase = MyDatabase.getInstance(this);
        Plan plan = myDatabase.getPlan(id);
        long second = plan.getTotalSecondSpent();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - starttime;
        long updatedTimeInMillisecond = second*1000 + elapsedTime;
        String ans = TimeAndSecondsConverter.fromMilisecondToString(updatedTimeInMillisecond);
        return ans;
    }

    private void setUpNotification(){
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intentNotif = new Intent(this, MainActivity.class);
        intentNotif.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intentNotif,PendingIntent.FLAG_UPDATE_CURRENT);

        // notification's layout
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_timer_service);
        // notification's icon
        mRemoteViews.setImageViewResource(R.id.notif_icon, R.mipmap.ic_launcher);
        // notification's title
        mRemoteViews.setTextViewText(R.id.notif_title, getResources().getString(R.string.app_name));
        // notification's content
        mRemoteViews.setTextViewText(R.id.notif_content,"0:00:00");

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setContent(mRemoteViews)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setTicker(getElapsedTime());

        startForeground(NOTIF_ID,mBuilder.build());
    }

    class TimerServiceReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mRemoteViews.setTextViewText(R.id.notif_content,getElapsedTime());
            mBuilder.setContent(mRemoteViews);
            mNotificationManager.notify(NOTIF_ID,mBuilder.build());
        }
    }
}
