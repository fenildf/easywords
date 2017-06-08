package com.keshe.zhi.easywords.Activities;

import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

public class DanCiBenActivity extends AppCompatActivity {
    MyDatabaseHelper dbhelper;
    ListView wordlist;
    String table_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dan_ci_ben);
        wordlist = (ListView) findViewById(R.id.listview);
        dbhelper=MyDatabaseHelper.getDbHelper(this);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        WilddogAuth mAuth = WilddogAuth.getInstance();
        table_name = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE).getString("category","cet6");

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        Cursor cursor = dbhelper.getWordCollection(dbhelper.getWritableDatabase(),table_name);
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,R.layout.list_item5,cursor,new String[]{"content","analyzes"},new int[]{R.id.textView90,R.id.textView91});
        wordlist.setAdapter(simpleCursorAdapter);
        registerForContextMenu(wordlist);
        wordlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.item_context,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ConstraintLayout constraintLayout= (ConstraintLayout) menuInfo.targetView;
        TextView word = (TextView) constraintLayout.findViewById(R.id.textView90);
        System.out.println(word.getText().toString());
        dbhelper.deleteFromCollection(dbhelper.getWritableDatabase(),word.getText(),table_name);
        onResume();
        return super.onContextItemSelected(item);
    }
}
