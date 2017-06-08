package com.keshe.zhi.easywords.Activities;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StudySettings extends AppCompatActivity {
    ListView listView;
    MyDatabaseHelper dbhelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_settings);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        listView = (ListView) findViewById(R.id.listview);

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        final WilddogAuth mAuth = WilddogAuth.getInstance();


        SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
        String today_words = sp.getString("today_words", "20");
        String study_pattern = sp.getString("study_pattern", "yes_no");
        switch (study_pattern) {
            case "yes_no":
                study_pattern = "认知";
                break;
            case "pick_means":
                study_pattern = "选择";
                break;
            case "spell_word":
                study_pattern = "拼写";
                break;
        }


        List<HashMap<String, String>> list = new ArrayList<>();
        String[] left = new String[]{"每日单词量", "学习模式"};
        String[] right = new String[]{today_words, study_pattern};
        for (int i = 0; i < 2; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("left", left[i]);
            map.put("right", right[i]);
            list.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.list_item4, new String[]{"left", "right"}, new int[]{R.id.textView83, R.id.textView84});

        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position) {
                    case 0:
                        final View view1 = LayoutInflater.from(StudySettings.this).inflate(R.layout.alertview, null);
                        final Spinner total = (Spinner) view1.findViewById(R.id.spinner);
                        final TextView ntotal = (TextView) view1.findViewById(R.id.textView31);
                        total.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        ntotal.setText("3");
                                        break;
                                    case 1:
                                        ntotal.setText("8");
                                        break;
                                    case 2:
                                        ntotal.setText("17");
                                        break;
                                    case 3:
                                        ntotal.setText("25");
                                        break;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(StudySettings.this);
                        builder.setTitle("设置学习量");
                        builder.setView(view1);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String today_words = (String) total.getSelectedItem();
                                String today_new_words = ntotal.getText().toString();
                                SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("today_words", today_words);
                                editor.putString("today_new_words", today_new_words);
                                //重新设置学习量后，清除今日单词表
                                editor.putString("today_finished", "0");
                                editor.putString("isReady", "false");
                                String category = sp.getString("category","cet6");
                                dbhelper.cleanWordsToday(dbhelper.getWritableDatabase(),category);
                                editor.apply();

                                TextView textView = (TextView) view.findViewById(R.id.textView84);
                                textView.setText(today_words);
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create();
                        builder.show();
                        break;
                    case 1:
                        //设置学习模式
                        ConstraintLayout view2 = (ConstraintLayout) LayoutInflater.from(StudySettings.this).inflate(R.layout.study_pattern_item, null);
                        final Spinner spinner = (Spinner) view2.findViewById(R.id.spinner2);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(StudySettings.this);
                        builder1.setView(view2);
                        builder1.setTitle("选择学习模式");
                        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String pattern = (String) spinner.getSelectedItem();
                                String pattern_c = pattern;
                                switch (pattern) {
                                    case "认知":
                                        pattern="yes_no";
                                        break;
                                    case "选择":
                                        pattern="pick_means";
                                        break;
                                    case "拼写":
                                        pattern = "spell_word";
                                        break;
                                }
                                SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
                                sp.edit().putString("study_pattern",pattern).apply();

                                TextView textView = (TextView) view.findViewById(R.id.textView84);
                                textView.setText(pattern_c);
                            }
                        });

                        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder1.create().show();
                        break;
                }
            }
        });
    }
}
