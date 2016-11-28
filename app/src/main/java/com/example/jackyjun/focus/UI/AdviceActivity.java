package com.example.jackyjun.focus.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jackyjun.focus.Feedback.GMailSender;
import com.example.jackyjun.focus.R;

import java.util.Date;

/**
 * Created by jackyjun on 16/10/9.
 */
public class AdviceActivity extends Activity {
    private EditText titleEditText;
    private EditText contentEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        titleEditText = (EditText) findViewById(R.id.advice_name_edit_text);
        contentEditText = (EditText) findViewById(R.id.advice_content_edit_text);
        submitButton = (Button) findViewById(R.id.advice_submit_button);

        checkTimeLimit();

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String subject = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                if (!subject.trim().equals("") && !content.trim().equals("")){
                    new SendEmailTask().execute(subject,content);

                    //return back to the main activity
                    Intent intent = new Intent(AdviceActivity.this,MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(AdviceActivity.this,"请输入建议的名称或内容",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class SendEmailTask extends AsyncTask<String,Integer,Long>{
        @Override
        protected Long doInBackground(String... params) {
            try {
                GMailSender sender = new GMailSender("jackyjun935@gmail.com", "tansijun20000117");
                sender.sendMail(params[0],
                        params[1],
                        "jackyjun935@gmail.com",
                        "jackyjun935@gmail.com");
                Log.d("SendMail","email sent");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            //disable the submit button and save the time in the SharedPreference
            SharedPreferences prefs = AdviceActivity.this.getSharedPreferences("time", Context.MODE_PRIVATE);
            long currentTime = new Date().getTime();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("time", currentTime);
            editor.apply();
            submitButton.setEnabled(false);

            Toast.makeText(AdviceActivity.this, "感谢你提出的宝贵的建议", Toast.LENGTH_SHORT).show();
        }
    }

    //check the if the time has already past one day
    //if it is, enable the button again
    //if it is not, send a toast to remind the user
    private void checkTimeLimit() {
        SharedPreferences prefs = this.getSharedPreferences("time", Context.MODE_PRIVATE);
        long previousTime = prefs.getLong("time", 0);
        long currentTime = new Date().getTime();

        if (currentTime - previousTime > 24 * 3600 * 1000) {
            //enable the button
            submitButton.setEnabled(true);
        } else{
            submitButton.setEnabled(false);
            Toast.makeText(this,"你今天已经提过建议了，请24小时后再来",Toast.LENGTH_SHORT).show();
        }
    }


}
