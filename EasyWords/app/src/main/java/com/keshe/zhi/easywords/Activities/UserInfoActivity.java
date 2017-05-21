package com.keshe.zhi.easywords.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.request.UserProfileChangeRequest;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class UserInfoActivity extends AppCompatActivity {
    EditText name;
    EditText phone;
    EditText mail;
    EditText age;
    EditText hobby;
    WilddogAuth wilddogAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        name = (EditText) findViewById(R.id.editText3);
        phone = (EditText) findViewById(R.id.editText12);
        mail = (EditText) findViewById(R.id.editText10);
        age = (EditText) findViewById(R.id.editText13);
        hobby = (EditText) findViewById(R.id.editText14);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        wilddogAuth = WilddogAuth.getInstance();
        //获取当前属性
        name.setText(wilddogAuth.getCurrentUser().getDisplayName());
        phone.setText(wilddogAuth.getCurrentUser().getPhone());
        mail.setText(wilddogAuth.getCurrentUser().getEmail());
        SharedPreferences preferences = getSharedPreferences(wilddogAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        age.setText(preferences.getString("age", ""));
        hobby.setText(preferences.getString("hobby", ""));
    }

    public void submitChange(View view) {
        String usrname = name.getText().toString();
        String phonenum = phone.getText().toString();
        String mailbox = mail.getText().toString();
        String age_txt = age.getText().toString();
        String hobby_txt = hobby.getText().toString();
        WilddogUser user = wilddogAuth.getCurrentUser();
        SharedPreferences preferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        if (preferences.getString("age", "").equals(age_txt) && preferences.getString("hobby", "").equals(hobby_txt) && user.getDisplayName().equals(usrname) && user.getPhone().equals(phonenum) && user.getEmail().equals(mailbox)) {
            Toast.makeText(this, "未做任何修改", Toast.LENGTH_SHORT).show();
            return;
        }

        //保存年龄和兴趣

        if (!preferences.getString("age","").equals(age_txt)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("age", age_txt);
            editor.apply();
        }
        if (!preferences.getString("hobby", "").equals(hobby_txt)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("age", hobby_txt);
            editor.apply();
        }

        if (!user.getDisplayName().equals(usrname)) {
            //更新用户名
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(usrname)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                // 发生错误
                            }
                        }
                    });
        }

        if (!user.getPhone().equals(phonenum)) {
            //更新手机号
            user.updatePhone(phonenum).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        // 更新成功
                    } else {
                        // 发生错误
                        Log.d("result", task.getException().toString());
                        if ("The phone is already in use by another account.".equals(task.getException().getMessage())) {
                            Toast.makeText(UserInfoActivity.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            });
        }

        if (!user.getEmail().equals(mailbox)) {
            //更新邮箱
            user.updateEmail(mailbox)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                // 更新成功
                            } else {
                                // 发生错误
                                Log.d("result", task.getException().toString());
                                if ("The email address is already in use by another account.".equals(task.getException().getMessage())) {
                                    Toast.makeText(UserInfoActivity.this, "该邮箱已被注册", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                    });
        }
        // 更新成功
        Toast.makeText(UserInfoActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }
}
