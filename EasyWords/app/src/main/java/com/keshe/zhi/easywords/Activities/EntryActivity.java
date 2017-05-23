package com.keshe.zhi.easywords.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EntryActivity extends AppCompatActivity {
    MyDatabaseHelper dbhelper=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        File file = this.getDatabasePath(dbhelper.getDatabaseName());
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
    }

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

    public void goLogin(View view) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    public void goRegister(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String uname = sharedPreferences.getString("user", "");
        if (!uname.equals("")) {
            System.out.println("用户存在");
            Intent intent = new Intent(this, UserActivity2.class);
            intent.putExtra("username", uname);
            startActivity(intent);
        }

    }
}
