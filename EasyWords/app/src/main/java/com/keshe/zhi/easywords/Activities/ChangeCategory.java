package com.keshe.zhi.easywords.Activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;


public class ChangeCategory extends AppCompatActivity implements View.OnClickListener {
    Button cet4;
    Button cet6;
    Button cet4_core;
    Button kaoyan;
    WilddogAuth mAuth;
    MyDatabaseHelper dbhelper;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_category);
        cet4 = (Button) findViewById(R.id.button5);
        cet6 = (Button) findViewById(R.id.button7);
        cet4_core = (Button) findViewById(R.id.button4);
        kaoyan = (Button) findViewById(R.id.button6);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        cet4_core.setOnClickListener(this);
        cet4.setOnClickListener(this);
        cet6.setOnClickListener(this);
        kaoyan.setOnClickListener(this);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();



    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
        category = sp.getString("category", "cet6");
        switch (category) {
            case "cet4":
                cet4.setText("已选择");
                cet6.setText("选择");
                cet4_core.setText("选择");
                kaoyan.setText("选择");
                break;
            case "cet6":
                cet4.setText("选择");
                cet6.setText("已选择");
                cet4_core.setText("选择");
                kaoyan.setText("选择");
                break;
            case "cet4_core":
                cet4.setText("选择");
                cet6.setText("选择");
                cet4_core.setText("已选择");
                kaoyan.setText("选择");
                break;
            case "kaoyan":
                cet4.setText("选择");
                cet6.setText("选择");
                cet4_core.setText("选择");
                kaoyan.setText("已选择");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        switch (v.getId()) {
            case R.id.button5:
                System.out.println("choose cet4");
                dbhelper.cleanWordsToday(dbhelper.getWritableDatabase(),category);
                editor.putString("category", "cet4").putString("today_finished","0").putString("isReady","false").apply();
                onResume();
                break;
            case R.id.button6:
                dbhelper.cleanWordsToday(dbhelper.getWritableDatabase(),category);
                editor.putString("category", "kaoyan").putString("today_finished","0").putString("isReady","false").apply();
                onResume();
                break;
            case R.id.button7:
                dbhelper.cleanWordsToday(dbhelper.getWritableDatabase(),category);
                editor.putString("category", "cet6").putString("today_finished","0").putString("isReady","false").apply();
                onResume();
                break;
            case R.id.button4:
                dbhelper.cleanWordsToday(dbhelper.getWritableDatabase(),category);
                editor.putString("category", "cet4_core").putString("today_finished","0").putString("isReady","false").apply();
                onResume();
                break;
        }
    }
}
