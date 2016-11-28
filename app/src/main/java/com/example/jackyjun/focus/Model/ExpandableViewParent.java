package com.example.jackyjun.focus.Model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/** ExpandableViewParent is the model for the expandableview in PlanInfoActivity
 *  It stores the parent's title for the expandableview and  a list of ToDos and Dones as the data for its child view
 * Created by jackyjun on 16/10/19.
 */
public class ExpandableViewParent implements ParentListItem {

    private List<Object> mChildrenList;
    private String parentTitle;

    // these strings will be displayed on the parent view of the recyclerview
    public static final String STRING_TODO = "Todo";
    public static final String STRING_DONE = "Done";

    public ExpandableViewParent(){}

    public ExpandableViewParent(String parentTitle){
        this.parentTitle = parentTitle;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    @Override
    public List<Object> getChildItemList() {
        return mChildrenList;
    }

    public void setChildItemList(List<Object> list){
        mChildrenList = list;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return true;
    }
}
