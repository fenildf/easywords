package com.keshe.zhi.easywords.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        listView= (ListView) findViewById(R.id.listview);
        List<HashMap<String,String>> list = new ArrayList<>();
        String[] props = new String[]{};
        for (int i = 0; i < 4; i++) {
            HashMap<String, String> map = new HashMap<>();
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,list);
    }
}
