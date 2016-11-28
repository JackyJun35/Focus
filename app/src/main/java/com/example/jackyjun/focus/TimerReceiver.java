package com.example.jackyjun.focus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.UI.MainActivity;
import com.example.jackyjun.focus.UI.PlanInfoActivity;

/**
 * Created by jackyjun on 16/9/5.
 */
public class TimerReceiver extends BroadcastReceiver {

    private Plan mPlan;
    private int position;
    @Override
    public void onReceive(Context context, Intent intent) {
        mPlan = (Plan) intent.getSerializableExtra("Plan");
        position = intent.getIntExtra("Position",0);
        MainActivity.planList.get(position).setTotalSecondSpent(mPlan.getTotalSecondSpent());
        MainActivity.mAdapter.notifyDataSetChanged();
    }
}
