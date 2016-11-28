package com.example.jackyjun.focus.ExpandableList;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.Model.Done;
import com.example.jackyjun.focus.Model.ExpandableViewParent;
import com.example.jackyjun.focus.Model.ToDo;
import com.example.jackyjun.focus.Model.ToDoParent;
import com.example.jackyjun.focus.R;
import com.example.jackyjun.focus.UI.PlanInfoActivity;
import com.example.jackyjun.focus.util.CalendarUtil;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by jackyjun on 16/10/29.
 */
public class DoneExpandableAdapter extends ExpandableRecyclerAdapter<DoneListParentViewHolder,DoneListChildViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    public  List<Done> adapterList;

    private TodoExpandableAdapter todoExpandableAdapter;

    public DoneExpandableAdapter(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        adapterList = (List<Done>) parentItemList.get(0).getChildItemList();
    }


    public void setToDoExpandableAdapter(TodoExpandableAdapter adapter){
        todoExpandableAdapter = adapter;
    }

    @Override
    public DoneListParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_card_plan_info_parent_done, viewGroup, false);
        return new DoneListParentViewHolder(view);
    }

    @Override
    public DoneListChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_plan_info_child_backup, viewGroup, false);
        return new DoneListChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(DoneListParentViewHolder doneListParentViewHolder, int i, ParentListItem parentListItem) {
        ExpandableViewParent expandableViewParent = (ExpandableViewParent) parentListItem;
        //todoListParentViewHolder.mTextView.setText(expandableViewParent.getParentTitle());

    }

    @Override
    public void onBindChildViewHolder(DoneListChildViewHolder doneListChildViewHolder, int i, Object o) {
        Done done = (Done) o;
        doneListChildViewHolder.mTextView.setText(done.getName());
        doneListChildViewHolder.mCheckBox.setChecked(true);

        startCheckBoxListener(doneListChildViewHolder, i, done);
    }

    private void startCheckBoxListener(final DoneListChildViewHolder doneListChildViewHolder, final int pos, final Done done) {
        doneListChildViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    MyDatabase db = MyDatabase.getInstance(mContext);
                    db.fromDoneToTodo(done);

                    adapterList.remove(done);
                    //1st param: ParentPos is 0 because there is only 1 parent in the view.
                    //I suppose the pos starts from 1, which cause the potential bug, so I subtract 1.

                    //Log.d("ChildPos",String.valueOf(pos-1));
                    notifyChildItemRemoved(0, pos - 1);
                    //notifyChildItemRangeChanged(0,pos-1,adapterList.size());

                    todoExpandableAdapter.adapterList.add(0,done.toToDo());
                    todoExpandableAdapter.notifyChildItemInserted(0,0);


                    notifyDataSetChanged();


                    // PlanInfoActivity.mExpandableAdapter1.notifyChildItemInserted(0,pos-1);
                }
            }
        });

    }

    private void sortDoneListByDate(List<Done> doneList) {
        Collections.sort(doneList);
        Collections.reverse(doneList);
        Log.d("Sorted AdapterList", doneList.toString());
    }




}

