package com.example.jackyjun.focus.ExpandableList;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.jackyjun.focus.R;

/**
 * Created by jackyjun on 16/10/29.
 */
public class DoneListParentViewHolder extends ParentViewHolder {

    public TextView mTextView;
    public ImageButton mParentDropDownArrow;

    public DoneListParentViewHolder(View itemView) {
        super(itemView);

        mTextView = (TextView) itemView.findViewById(R.id.parent_list_item_text_view);
        mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.parent_list_item_expand_arrow);

    }
}
