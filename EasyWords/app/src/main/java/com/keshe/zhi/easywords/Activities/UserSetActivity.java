package com.keshe.zhi.easywords.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

public class UserSetActivity extends AppCompatActivity {
    TextView username = null;
    ListView listView = null;
    MyDatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_set);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        username = (TextView) findViewById(R.id.textView26);
        username.setText(getIntent().getStringExtra("username"));
        listView = (ListView) findViewById(R.id.set_list);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.set_listitem, R.id.textView27, new String[]{"个人资料", "修改密码", "扩充词库", "更改学习量"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position) {
                    case 3:
                        final View view1 = LayoutInflater.from(UserSetActivity.this).inflate(R.layout.alertview, null);
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
                        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(UserSetActivity.this);
                        builder.setTitle("设置学习量");
                        builder.setView(view1);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int today_words = Integer.valueOf((String) total.getSelectedItem());
                                dbHelper.setToday(dbHelper.getWritableDatabase(), today_words, Integer.valueOf(ntotal.getText().toString()), getIntent().getStringExtra("username"));
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
                        Intent intent = new Intent(UserSetActivity.this, ChangPass.class);
                        intent.putExtra("username", getIntent().getStringExtra("username"));
                        startActivity(intent);
                        break;
                }
            }
        });
    }
}
