package com.example.jackyjun.focus.ExpandableList;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.jackyjun.focus.R;


/**
 * Created by jackyjun on 16/8/12.
 */
public class TodoListChildViewHolder extends ChildViewHolder {

    public CheckBox mCheckBox;
    public TextView mTextView;
    public Button mButton;

    public TodoListChildViewHolder(View itemView) {
        super(itemView);

        mCheckBox = (CheckBox) itemView.findViewById(R.id.plan_info_child_checkbox);
        mTextView = (TextView) itemView.findViewById(R.id.child_list_item_text_view);
        mButton = (Button) itemView.findViewById(R.id.child_List_item_button);
     }

}
