package com.example.jackyjun.focus.UI;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jackyjun on 16/7/20.
 */
public class AddPlanActivity extends AppCompatActivity {

    private EditText editText;
    private EditText descriptionEditText;
    private ListView listView;
    private SimpleAdapter adapter;
    private Calendar mCalendar;
    private String returnedData;
    private List<Map<String,Object>> data;
    private static final String[] display = {"text","image","displayText"};
    private static final int[] listViewLayoutId = {R.id.textview_name, R.id.image_photo,R.id.display_text_in_add_plan_item};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);

        editText = (EditText) findViewById(R.id.plan_name_in_add_plan_activity);
        descriptionEditText = (EditText) findViewById(R.id.description_edit_text_add_plan);

        listView = (ListView) findViewById(R.id.plan_settings_in_add_plan_activity);
        data = getData();
        adapter = new SimpleAdapter(this, data, R.layout.add_plan_list_item, display, listViewLayoutId);
        listView.setAdapter(adapter);
        mCalendar = Calendar.getInstance();



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        DatePickerDialog dpd =  new DatePickerDialog(AddPlanActivity.this, date, mCalendar
                                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                                mCalendar.get(Calendar.DAY_OF_MONTH));
                        Date date= Calendar.getInstance().getTime();
                        dpd.getDatePicker().setMinDate(date.getTime());
                        dpd.show();
                        break;
                    case 1:
                        createNumberPickerDialog();
                        break;
                }
            }
        });


        com.rey.material.widget.Button button = (com.rey.material.widget.Button) findViewById(R.id.finish_button_in_add_plan_activity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionStart();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    returnedData = data.getStringExtra("description");
                    Log.d("DescriptionActivity",returnedData);
                }break;
            default:
        }

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("text","截止日期");
        map.put("image",R.drawable.ic_today_black_24dp);
        list.add(map);

        map = new HashMap<>();
        map.put("text","持续时间");
        map.put("image",R.drawable.ic_access_time_black_24dp);
        list.add(map);

        return list;
    }

    //Helper Method
    private void updateLabel() {
        String myFormat = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //Map<String,Object> map = new HashMap<>();
        //map.put("displayText",sdf.format(mCalendar.getTime()));
        //data.get(0).remove("displayText");
        data.get(0).put("displayText",sdf.format(mCalendar.getTime()));
        adapter.notifyDataSetChanged();
    }

    private void createNumberPickerDialog(){
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(AddPlanActivity.this)
                .setTitle("预计的时间（小时）")
                .setView(R.layout.number_picker_add_plan)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = dialogbuilder.create();
        alertDialog.show();
        EditText editText = (EditText) alertDialog.findViewById(R.id.numberpicker_for_add_plan);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        data.get(1).put("displayText",editText.getText());
        adapter.notifyDataSetChanged();

    }

    private void createSelectorDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddPlanActivity.this)
                .setTitle("选择活动的重要程度")
                .setView(R.layout.item_importance_add_plan)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void createDescriptionClass(){
        Intent intent = new Intent(AddPlanActivity.this, DescriptionActivity.class);
        startActivityForResult(intent,1);
    }

    private void actionStart(){
        if(!editText.getText().toString().trim().equals("")){
            MyDatabase db = MyDatabase.getInstance(this);
                if(db.queryPlanByPlanName(editText.getText().toString().trim())!=null){
                    Toast.makeText(this, "任务已经存在，请更改任务名称", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(AddPlanActivity.this,MainActivity.class);
                    intent.putExtra("planName",editText.getText().toString());
                    intent.putExtra("deadline", mCalendar);
                    if(data.get(1).get("displayText") !=null){
                        intent.putExtra("duration", data.get(1).get("displayText").toString());
                    }
                    Log.d("FuckDescription",descriptionEditText.getText().toString());
                    intent.putExtra("description",descriptionEditText.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                    }
                }
        else{
            Toast.makeText(this,"请输入任务名称",Toast.LENGTH_SHORT).show();
        }
    }
}

