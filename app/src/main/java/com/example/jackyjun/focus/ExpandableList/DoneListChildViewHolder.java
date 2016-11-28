package com.example.jackyjun.focus.ExpandableList;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.example.jackyjun.focus.R;

/**
 * Created by jackyjun on 16/10/29.
 */
public class DoneListChildViewHolder extends ChildViewHolder {

    public CheckBox mCheckBox;
    public TextView mTextView;
    public ImageButton mImageButton;

    public DoneListChildViewHolder(View itemView) {
        super(itemView);

        mCheckBox = (CheckBox) itemView.findViewById(R.id.plan_info_child_checkbox);
        mTextView = (TextView) itemView.findViewById(R.id.child_list_item_text_view);
        mImageButton = (ImageButton) itemView.findViewById(R.id.plan_info_child_done_delete);
    }
}
