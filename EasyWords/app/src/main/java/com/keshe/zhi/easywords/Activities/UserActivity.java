package com.keshe.zhi.easywords.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Handler handler = null;
    String pron_mp3 = "";
    TextView sent_today = null;
    MyDatabaseHelper dbHelper = null;
    MediaPlayer mPlayer = new MediaPlayer();
    TextView tv_days = null;
    TextView tv_total_words = null;
    TextView tv_master_words = null;
    TextView tv_today_words = null;
    TextView tv_today_nword = null;
    TextView tv_today_cmp = null;
    Button begin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tv_days = (TextView) findViewById(R.id.textView10);
        tv_total_words = (TextView) findViewById(R.id.textView11);
        tv_master_words = (TextView) findViewById(R.id.textView13);
        tv_today_words = (TextView) findViewById(R.id.textView16);
        tv_today_nword = (TextView) findViewById(R.id.textView17);
        tv_today_cmp = (TextView) findViewById(R.id.textView18);
        sent_today = (TextView) findViewById(R.id.textView24);
        begin = (Button) findViewById(R.id.button5);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        initUserRecord(getIntent().getStringExtra("username"));
        sentenceToday();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("易背单词");
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x1234:
                        String result = msg.getData().getString("result");
                        try {
                            JSONObject root = new JSONObject(result);
                            pron_mp3 = root.getString("tts");
                            String content = root.getString("content");
                            String pic_s = root.getString("picture");
                            String pic_l = root.getString("picture2");
                            String date = root.getString("dateline");
                            String note = root.getString("note");
                            ContentValues cv = new ContentValues();
                            cv.put("sentence", content + "\n" + note);
                            cv.put("pron_mp3", pron_mp3);
                            cv.put("pic_s", pic_s);
                            cv.put("pic_l", pic_l);
                            cv.put("date", date);
                            dbHelper.addSentence(dbHelper.getWritableDatabase(), cv);
                            sent_today.setText(content + "\n" + note);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this).setMessage("是否退出应用").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        }
        return true;
    }

    private void initUserRecord(String username) {
        dbHelper.initUserRecord(dbHelper.getWritableDatabase(), username);
    }

    public void beginRecite(View view) {
        Intent intent = new Intent(this, ReciteWordActivity.class);
        intent.putExtra("username", getIntent().getStringExtra("username"));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showRecord();
        if (tv_today_words.getText().toString().equals(String.valueOf(dbHelper.last_pos(dbHelper.getReadableDatabase(), getIntent().getStringExtra("username"))))) {
            begin.setText("再来一组");
            dbHelper.deleteRecord(dbHelper.getWritableDatabase(), getIntent().getStringExtra("username"));
        } else {
            begin.setText("开始");
        }
    }

    public void showRecord() {
        Cursor cursor = dbHelper.getRecord(dbHelper.getReadableDatabase(), getIntent().getStringExtra("username"));
        try {
            while (cursor.moveToNext()) {
                int days = cursor.getInt(cursor.getColumnIndex("days"));
                int total_words = cursor.getInt(cursor.getColumnIndex("total_words"));
                int master_words = cursor.getInt(cursor.getColumnIndex("master_words"));
                int today_words = cursor.getInt(cursor.getColumnIndex("today_words"));
                int today_nword = cursor.getInt(cursor.getColumnIndex("today_nword"));
                int today_cmp = cursor.getInt(cursor.getColumnIndex("today_cmp"));
                tv_days.setText(String.valueOf(days));
                tv_total_words.setText(String.valueOf(total_words));
                tv_master_words.setText(String.valueOf(master_words));
                tv_today_words.setText(String.valueOf(today_words));
                tv_today_nword.setText(String.valueOf(today_nword));
                tv_today_cmp.setText(String.valueOf(today_cmp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    public void pronu(View view) {
        final Uri uri = Uri.parse(pron_mp3);
        if (!mPlayer.isPlaying()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPlayer.setDataSource(UserActivity.this, uri);
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (!mPlayer.isPlaying()) {
                            mPlayer.release();
                        }
                    }
                }
            }).start();
            mPlayer.reset();
        }
    }

    private void sentenceToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        Cursor cursor = dbHelper.getSenToday(dbHelper.getReadableDatabase(), date);
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                pron_mp3 = cursor.getString(cursor.getColumnIndex("pron_mp3"));
                sent_today.setText(cursor.getString(cursor.getColumnIndex("sentence")));
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String baseurl = "http://open.iciba.com/dsapi/";
                        URL url = new URL(baseurl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setDoInput(true);
                        InputStream is = conn.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        String result = "";
                        char[] buffer = new char[100];
                        int len = 0;
                        while ((len = isr.read(buffer)) != -1) {
                            result += String.valueOf(buffer, 0, len);
                        }
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
                        msg.setData(bundle);
                        msg.what = 0x1234;
                        handler.sendMessage(msg);
                        isr.close();
                        is.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint("输入要查找的单词");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                intent.putExtra("query", query);
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (true) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, WordsListActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, UserSetActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.exit_login) {
            SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.remove("user");
            edit.apply();
            Intent intent = new Intent(this, EntryActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
