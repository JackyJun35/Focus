package com.example.jackyjun.focus;

import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.Model.Plan;
import com.akexorcist.roundcornerprogressbar.*;
import com.example.jackyjun.focus.UI.MainActivity;
import com.example.jackyjun.focus.util.PlanDaysCalculator;
import com.example.jackyjun.focus.util.TimeAndSecondsConverter;

import java.util.List;

/**
 * Created by jackyjun on 16/7/17.
 */
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> implements View.OnClickListener{

    private List<Plan> plansList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    public static TimerService mBoundService;
    public static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((TimerService.TimerBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView planTitle;
        public TextView planTotalTime;
        // how many days have been spent on this plan
        public TextView planDays;
        public TextView planCurrentTime;
        public Button startButton;
        public RoundCornerProgressBar progressBar;
        public ImageButton menuButton;

        public ViewHolder(View v) {
            super(v);
            planTitle = (TextView) itemView.findViewById(R.id.plan_title);
            planTotalTime = (TextView) itemView.findViewById(R.id.plan_total_time_text_view);
            planCurrentTime = (TextView) itemView.findViewById(R.id.plan_current_hours_text_view);
            planDays = (TextView) itemView.findViewById(R.id.days_on_plan_text_view);
            startButton = (Button) itemView.findViewById(R.id.plan_start);
            progressBar = (RoundCornerProgressBar) itemView.findViewById(R.id.plan_progress_bar);
            menuButton = (ImageButton) itemView.findViewById(R.id.menu_button_in_main_activity_card);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()==startButton.getId()){
            }
        }
    }


    public PlanAdapter(Context mContext, List<Plan> plansList) {
        this.mContext = mContext;
        this.plansList = plansList;
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        view.setOnClickListener(this);
        //set the view's size

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Plan mPlan = plansList.get(position);
        holder.planTitle.setText(mPlan.getName());
        holder.planDays.setText("还有"+ PlanDaysCalculator.countdownDays(mPlan.getDeadline())+"天");
        holder.planTotalTime.setText("/"+mPlan.getDuration()+"h");
        holder.planCurrentTime.setText(mPlan.getCurrentTimeInString());
        //Set up progress bar
        setUpProgressBar(holder,mPlan);
        initStartButton(holder,position,mPlan);
        initPopupMenu(holder,position);
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void initPopupMenu(ViewHolder holder, final int position){
        final PopupMenu popupMenu= new PopupMenu(mContext,holder.menuButton);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                        dialog.setTitle("删除你的计划");
                        dialog.setMessage("你确定要删除你的计划吗？(删除之后就不能还原了)");
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDatabase db = MyDatabase.getInstance(mContext);
                                db.deletePlan(plansList.get(position));
                                plansList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,plansList.size());
                                dialog.dismiss();
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case R.id.action_finish_in_plan_card:
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(mContext);
                        dialog2.setTitle("标记为已完成");
                        dialog2.setMessage("预计时间还没到，您确定已经完成了您的计划了吗");
                        dialog2.setCancelable(true);
                        dialog2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MyDatabase db = MyDatabase.getInstance(mContext);
                                db.markPlanAsFinished(plansList.get(position));
                                plansList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,plansList.size());
                                dialog.dismiss();
                            }
                        });
                        dialog2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog2.show();
                }

                return true;
            }
        });
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_card_in_main_activity,popupMenu.getMenu());
        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
    }

    private void initStartButton(final ViewHolder holder,final int position,final Plan mPlan){
        holder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBoundService==null||!mBoundService.isServiceRunning()){
                    //here the Intent open the service, and the service need to know the plan and it's location
                    //in the view so that it can upadate the UI by broadcaster;
                    Intent intent = new Intent(mContext, TimerService.class);
                    intent.putExtra("Plan",mPlan);
                    intent.putExtra("Position",position);
                    mContext.bindService(intent,connection,Context.BIND_AUTO_CREATE);
                    holder.startButton.setText("停止");
                    Log.d("Service","Timerservice started");
                }else{
                    Intent intent = new Intent(mContext,TimerService.class);
                    mContext.unbindService(connection);
                    mContext.stopService(intent);
                    holder.startButton.setText("开始");
                }
            }
        });
    }

    private void setUpProgressBar(ViewHolder holder,Plan mPlan){
        holder.progressBar.setMax(100);
        //Since the progress is changed every second,I have to get the original progress from database
        MyDatabase db = MyDatabase.getInstance(holder.progressBar.getContext());
        holder.progressBar.setProgress(db.queryPlanByPlanName(mPlan.getName()).getProgress());
        //The second progress is dynamic so it get progress from mPlan which is updated every second
        holder.progressBar.setSecondaryProgress(mPlan.getProgress());
        Log.d("Progress",Float.toString(mPlan.getProgress()));
        Log.d("Totalsecond",Long.toString(mPlan.getTotalSecondSpent()));
    }


}


