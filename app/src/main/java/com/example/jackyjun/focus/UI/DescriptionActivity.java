package com.example.jackyjun.focus.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import com.example.jackyjun.focus.R;

/**
 * Created by jackyjun on 16/8/5.
 */
public class DescriptionActivity extends AppCompatActivity {

    private EditText editText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_description);

        editText = (EditText) findViewById(R.id.description_edit_text);
        toolbar = (Toolbar) findViewById(R.id.toolbar_in_description);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_finish:
                        actionStart();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }

    private void actionStart(){
        Intent intent = new Intent(DescriptionActivity.this,AddPlanActivity.class);
        if(editText!=null){
            intent.putExtra("description",editText.getText().toString());
            Log.d("Description",editText.getText().toString());
        }
        setResult(RESULT_OK,intent);
        finish();
    }


}
