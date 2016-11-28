package com.example.jackyjun.focus;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jackyjun on 16/8/1.
 */
public class AddPlanAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private String[] mData;
    private Drawable[] icons;

    public AddPlanAdapter(LayoutInflater inflater,String[] data) {
        mInflater = inflater;
        mData = data;
    }


    @Override
    public int getCount() {
        return mData.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.add_plan_list_item,null);

        ImageView icon = (ImageView) view.findViewById(R.id.image_photo);
        TextView text = (TextView) view.findViewById(R.id.textview_name);
        icon.setImageDrawable(icons[position]);
        text.setText(mData[position]);

        return view;
    }
}
