package com.example.jackyjun.focus;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.Model.Plan;

import java.util.List;

/**
 * Created by jackyjun on 16/11/24.
 */

public class FinishedPlanAdapter extends RecyclerView.Adapter<FinishedPlanAdapter.ViewHolder> {

    private Context mContext;
    private List<Plan> plansList;

    public FinishedPlanAdapter(Context mContext, List<Plan> plansList) {
        this.mContext = mContext;
        this.plansList = plansList;
    }

    @Override
    public FinishedPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_finished_project, parent, false);
        //view.setOnClickListener(this);

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Plan mPlan = plansList.get(position);
        holder.planTitle.setText(mPlan.getName());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.history_delete_alertdialog_title);
                builder.setMessage(R.string.history_delete_alertdialog_content);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyDatabase db = MyDatabase.getInstance(mContext);
                        db.deleteFinishedPlan(mPlan);
                        plansList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,plansList.size());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView planTitle;
        public ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            planTitle = (TextView) itemView.findViewById(R.id.plan_title);
            deleteButton = (ImageButton) itemView.findViewById(R.id.history_activity_imagebutton_delete);
        }

        @Override
        public void onClick(View v) {
            if(v.getId()== deleteButton.getId()){
            }
        }
    }
}
