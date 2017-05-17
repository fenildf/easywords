package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

public class ChangPass extends AppCompatActivity {
    EditText oldpass = null;
    EditText newpass = null;
    EditText newpass2 = null;
    Button change = null;
    MyDatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_pass);
        oldpass = (EditText) findViewById(R.id.editText4);
        newpass = (EditText) findViewById(R.id.editText5);
        newpass2 = (EditText) findViewById(R.id.editText6);
        change = (Button) findViewById(R.id.button12);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
    }


    public void changePass(View view) {
        String tv_op = oldpass.getText().toString();
        String tv_np = newpass.getText().toString();
        String tv_np2 = newpass2.getText().toString();
        if (!tv_np.equals(tv_np2)) {
            Toast.makeText(this, "密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.changePass(dbHelper.getWritableDatabase(), getIntent().getStringExtra("username"), tv_op,tv_np)) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        }
    }
}
