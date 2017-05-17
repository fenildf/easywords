package com.keshe.zhi.easywords.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    EditText name = null;
    EditText passwd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        name = (EditText) findViewById(R.id.editText2);
        passwd = (EditText) findViewById(R.id.editText3);
    }

    public void checkInNow(View view) {
        String uname = name.getText().toString().trim();
        String pass = passwd.getText().toString().trim();
        if (uname.equals("")||passwd.equals("")) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Cursor cursor = dbHelper.login(dbHelper.getReadableDatabase(), uname, pass);
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, UserActivity2.class);
                intent.putExtra("username", uname);
                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", uname);
                editor.apply();
                startActivity(intent);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
