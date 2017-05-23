package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    EditText name = null;
    EditText passwd = null;
    TextView tip = null;
    WilddogAuth mAuth;
    MyDatabaseHelper databaseHelper;

    /**
     * 将/res/raw中的数据库复制到默认数据库创建的位置
     * @param file 数据库文件
     */
    public void saveToSDcard(File file) {
        System.out.println("正在复制数据库");
        InputStream is = this.getResources().openRawResource(R.raw.easy_word);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1000];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bis.close();
            bos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("数据库复制成功");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        name = (EditText) findViewById(R.id.editText9);
        passwd = (EditText) findViewById(R.id.editText11);
        tip = (TextView) findViewById(R.id.textView58);
        databaseHelper=MyDatabaseHelper.getDbHelper(this);

        File file = this.getDatabasePath(dbHelper.getDatabaseName());
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {//数据库文件不存在则创建
            System.out.println("数据库不存在");
            File pfile = file.getParentFile();
            if (!pfile.exists()) {//父级目录不存在则创建
                if (pfile.mkdir()){
                    try {
                        if (!file.createNewFile()) {
                            Toast.makeText(this,"数据库未复制成功",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveToSDcard(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                try {
                    if (!file.createNewFile()) {
                        Toast.makeText(this,"数据库未复制成功",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveToSDcard(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/cet-6.txt");
//        char[] chars = new char[200];
//        int len=-1;
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            Reader reader = new FileReader(file);
//            while ((len=reader.read(chars))!=-1) {
//                stringBuilder.append(new String(chars, 0, len));
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONArray array = new JSONArray(stringBuilder.toString());
//            System.out.println(array.length());
//            for (int i = 0; i < array.length(); i++) {
//                JSONObject object = array.getJSONObject(i);
////                new MyThread(dbhelper,object,"cet6").start();
//                new AsyncTask<Object, Integer, Integer>() {
//                    @Override
//                    protected Integer doInBackground(Object... params) {
//                        int result=0;
//                        if (((MyDatabaseHelper)params[0]).saveToDb(((MyDatabaseHelper)params[0]).getWritableDatabase(),(JSONObject)params[1],(String)params[2])) {
//                            System.out.println("已保存到数据库");
//                        }
//                        return result;
//                    }
//
//                    @Override
//                    protected void onProgressUpdate(Integer... values) {
//                        super.onProgressUpdate(values);
//                    }
//
//                    @Override
//                    protected void onPostExecute(Integer integer) {
//                        super.onPostExecute(integer);
////                        if (integer == 100) {
////                            Toast.makeText(getContext(),"完成",Toast.LENGTH_SHORT).show();
////                        }
//                    }
//                }.execute(databaseHelper,object,"cet6");
//            }
//            Toast.makeText(this,"完成",Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


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
//        String uname = name.getText().toString().trim();
//        String pass = passwd.getText().toString().trim();
        String uname = "18624323501";
        String pass = "771929558";

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
