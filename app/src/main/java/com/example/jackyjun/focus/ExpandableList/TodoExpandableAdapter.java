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
import java.util.List;

/**
 * Created by jackyjun on 16/8/12.
 */
public class TodoExpandableAdapter extends ExpandableRecyclerAdapter<TodoListParentViewHolder,TodoListChildViewHolder>{

    private LayoutInflater mInflater;
    //this list represent the data in the ExpandableView
    public List<ToDo> adapterList;
    private Context mContext;
    private MyDatabase db;

    //set up adapterList of the Done list so that it can be manipulated here.
    private DoneExpandableAdapter doneExpandableAdapter;

    public TodoExpandableAdapter(Context context, List<ParentListItem> parentItemList) {
        super(parentItemList);

        db = MyDatabase.getInstance(mContext);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        //warning: potential bug
        adapterList =  (List<ToDo>) (List<?>) parentItemList.get(0).getChildItemList();
        Log.d("AdapterList",adapterList.toString());
    }

    public void setDoneExpandableAdapter(DoneExpandableAdapter adapter){
        this.doneExpandableAdapter = adapter;
    }


    @Override
    public TodoListParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_card_plan_info_parent, viewGroup, false);
        return new TodoListParentViewHolder(view);
    }

    @Override
    public TodoListChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_plan_info_child_backup, viewGroup, false);
        return new TodoListChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(TodoListParentViewHolder todoListParentViewHolder,int parentPos, ParentListItem parentListItem) {
        ExpandableViewParent expandableViewParent = (ExpandableViewParent) parentListItem;
        todoListParentViewHolder.mTextView.setText(expandableViewParent.getParentTitle());

        loadAddTodoDialogBackUp(todoListParentViewHolder);
        //loadAddTodoDialog(todoListParentViewHolder);
    }

    @Override
    public void onBindChildViewHolder(TodoListChildViewHolder todoListChildViewHolder, int childPos, Object o) {
        ToDo toDo = (ToDo) o;
        todoListChildViewHolder.mTextView.setText(toDo.getName());
        todoListChildViewHolder.mCheckBox.setChecked(false);

        startCheckBoxListener(todoListChildViewHolder,childPos,toDo);
    }

    private void loadAddTodoDialog(TodoListParentViewHolder todoListParentViewHolder){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final View dialogView = layoutInflater.inflate(R.layout.item_alert_dialog_to_do,null);
        final EditText toDoTitle = (EditText) dialogView.findViewById(R.id.alert_dialog_edit_text_todo_name);
        final CalendarView calendarView = (CalendarView) dialogView.findViewById(R.id.alert_dialog_calendar_todo_deadline);
        final EditText estimatedTime = (EditText) dialogView.findViewById(R.id.alert_dialog_estimated_time);


        todoListParentViewHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("添加ToDo");
                dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Calendar calendar = Calendar.getInstance();
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                calendar.set(year,month,dayOfMonth);
                                Log.d("Date", CalendarUtil.fromCalenderToString(calendar));
                            }
                        });


                        ToDo toDo = new ToDo();
                        toDo.setName(toDoTitle.getText().toString().trim());
                        toDo.setDeadline(calendar);
                        toDo.setEstimatedTime(Integer.parseInt(estimatedTime.getText().toString().trim()));
                        toDo.setPlanName(((PlanInfoActivity) mContext).planName);
                        Log.d("Todo Name",toDo.getName());
                        //call database to store the data
                        MyDatabase db = MyDatabase.getInstance(mContext);
                        db.addTodo(toDo);

                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    private void loadAddTodoDialogBackUp(TodoListParentViewHolder todoListParentViewHolder){
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);


        todoListParentViewHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dialogView = layoutInflater.inflate(R.layout.item_alert_dialog_todo_backup,null);
                final EditText toDoTitle = (EditText) dialogView.findViewById(R.id.alert_dialog_edit_text_todo_name);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("添加Todo");
                dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // "to-Do" pass in two values, first it's name and then planName, so I get the planName from the context(very elegant solution)
                        ToDo toDo = new ToDo(toDoTitle.getText().toString().trim(),((PlanInfoActivity) mContext).planName);
                        db.addTodo(toDo);
                        adapterList.add(toDo);
                        //notifyDataSetChanged();
                        notifyChildItemInserted(0,adapterList.size()-1);

                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });




    }

    private void startCheckBoxListener(final TodoListChildViewHolder todoListChildViewHolder,final int pos ,final ToDo todo){
        todoListChildViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    MyDatabase db = MyDatabase.getInstance(mContext);
                    db.fromTodoToDone(todo);

                    adapterList.remove(todo);
                    //1st param: ParentPos is 0 because there is only 1 parent in the view.
                    //I suppose the pos starts from 1, which cause the potential bug, so I subtract 1.

                    //Log.d("ChildPos",String.valueOf(pos-1));
                    notifyChildItemRemoved(0,pos-1);
                   // notifyChildItemRangeChanged(0,pos-1,adapterList.size());
                    doneExpandableAdapter.adapterList.add(0,todo.toDone());
                    doneExpandableAdapter.notifyChildItemInserted(0,0);

                    notifyDataSetChanged();

                }
            }
        });

    }



}
