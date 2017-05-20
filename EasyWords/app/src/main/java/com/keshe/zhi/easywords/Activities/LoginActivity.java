package com.keshe.zhi.easywords.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class LoginActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    EditText name = null;
    EditText passwd = null;
    TextView tip = null;
    WilddogAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        name = (EditText) findViewById(R.id.editText9);
        passwd = (EditText) findViewById(R.id.editText11);
        tip = (TextView) findViewById(R.id.textView58);


        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE == tip.getVisibility()) {
                    tip.setVisibility(View.GONE);
                }
                return false;
            }
        });
        passwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE == tip.getVisibility()) {
                    tip.setVisibility(View.GONE);
                }
                return false;
            }
        });

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();
    }

    public void checkInNow(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setCancelable(false).setView(R.layout.doing_some).create();
        String uname = name.getText().toString().trim();
        String pass = passwd.getText().toString().trim();
        if ("".equals(uname) || "".equals(pass)) {
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
            tip.setText("输入不能为空");
            tip.setVisibility(View.VISIBLE);
            return;
        }

        if (uname.matches("^1\\d{10}$")) {//手机号
            System.out.println("手机号登录");
            alertDialog.show();
            mAuth.signInWithPhoneAndPassword(uname, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            Log.d("signInByPhone", "signInWithPhone:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.e("signInByPhone", "signInWithPhone", task.getException());
                                alertDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                alertDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, UserActivity2.class);
                                startActivity(intent);
                            }
                        }
                    });
        } else if (uname.matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")) {//邮箱
            System.out.println("邮箱登录");
            alertDialog.show();
            mAuth.signInWithEmailAndPassword(uname, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            Log.d("signIn", "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w("signIn", "signInWithEmail", task.getException());
                                Toast.makeText(LoginActivity.this, "登陆失败",
                                        Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, UserActivity2.class);
                                startActivity(intent);
                                alertDialog.dismiss();
                            }
                        }
                    });
        }else {
            tip.setText("格式错误");
            tip.setVisibility(View.VISIBLE);
        }
    }

    public void goRegister(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
//        try {
//            Cursor cursor = dbHelper.login(dbHelper.getReadableDatabase(), uname, pass);
//            if (cursor.getCount() == 0) {
//                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
//            } else {
//                Intent intent = new Intent(this, UserActivity2.class);
//                intent.putExtra("username", uname);
//                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("user", uname);
//                editor.apply();
//                startActivity(intent);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
}
