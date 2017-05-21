package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordsListActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    ListView wordList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);
    }

    public final class ViewHolder {
        public TextView textView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        LinearLayout ll = (LinearLayout) menuInfo.targetView;
        switch (item.getItemId()) {
            case R.id.look:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("query", ((TextView) ll.findViewById(R.id.textView25)).getText().toString());
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                break;
            case R.id.cancel:
//                dbHelper.reFroNote(dbHelper.getWritableDatabase(), ((TextView) ll.findViewById(R.id.textView25)).getText().toString(), getIntent().getStringExtra("username"));
//                onResume();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHelper = MyDatabaseHelper.getDbHelper(this);
//        final Cursor cursor = dbHelper.getUsersWord(dbHelper.getReadableDatabase(), getIntent().getStringExtra("username"));
        final List<Map<String, String>> list = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            Map<String, String> map = new HashMap<>();
//            map.put("word", cursor.getString(cursor.getColumnIndex("word")));
//            map.put("username", cursor.getString(cursor.getColumnIndex("username")));
//            list.add(map);
//        }
        wordList = (ListView) findViewById(R.id.wordlist);
        registerForContextMenu(wordList);
//        BaseAdapter baseAdapter = new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return cursor.getCount();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return list.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder viewHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(WordsListActivity.this).inflate(R.layout.listitem, null);
//                    viewHolder = new ViewHolder();
//                    viewHolder.textView = (TextView) convertView.findViewById(R.id.textView25);
//                    convertView.setTag(viewHolder);
//                } else {
//                    viewHolder = (ViewHolder) convertView.getTag();
//                }
//                viewHolder.textView.setText((String) ((Map) list.get(position)).get("word"));
//                return convertView;
//            }
//        };
//        wordList.setAdapter(baseAdapter);
//        cursor.close();
    }
}
