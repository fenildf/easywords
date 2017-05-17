package com.keshe.zhi.easywords.Activities;

import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReciteWordActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    TextView word = null;
    Button ph_en_mp3 = null;
    Button ph_am_mp3 = null;
    RelativeLayout rl = null;
    RelativeLayout rl1 = null;
    TextView mean = null;
    Handler handler = null;
    Button yes = null;
    Button no = null;
    String uri_ph_en_mp3 = "";
    String uri_ph_am_mp3 = "";
    Cursor today_words = null;
    String s_ph_en = "";
    String s_ph_am = "";
    int last_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        word = (TextView) findViewById(R.id.textView33);
        ph_en_mp3 = (Button) findViewById(R.id.button8);
//        ph_am_mp3 = (Button) findViewById(R.id.button9);
        Typeface tf = Typeface.createFromAsset(getAssets(), "segoeui.ttf");
        ph_am_mp3.setTypeface(tf);
        ph_en_mp3.setTypeface(tf);
//        mean = (TextView) findViewById(R.id.textView37);
//        rl = (RelativeLayout) findViewById(R.id.rl);
        rl.setVisibility(View.INVISIBLE);
//        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        yes = (Button) findViewById(R.id.button10);
        no = (Button) findViewById(R.id.button11);
        today_words = dbHelper.getTodayWords(dbHelper.getReadableDatabase(), getIntent().getStringExtra("username"));
        last_pos = dbHelper.last_pos(dbHelper.getReadableDatabase(), getIntent().getStringExtra("username"));
        today_words.move(last_pos + 1);
        while (dbHelper.getSkip(dbHelper.getReadableDatabase(), today_words.getString(today_words.getColumnIndex("word"))) == 1) {
            if (today_words.moveToNext()) {
            } else {
                today_words.moveToFirst();
            }
        }

        showWord(today_words.getString(today_words.getColumnIndex("word")));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0x1234:
                        String result = msg.getData().getString("result");
                        try {
                            JSONObject root = new JSONObject(result);
                            JSONObject data = root.getJSONObject("data");
                            String id = data.getString("id");//单词id
                            getExFromOL(id);
                            JSONObject pronunciations = data.getJSONObject("pronunciations");
                            s_ph_en = pronunciations.getString("uk");//英式音标
                            s_ph_am = pronunciations.getString("us");//美式音标
                            if (!s_ph_en.equals("")) {
                                ph_en_mp3.setText("英" + " [" + s_ph_en + "]");
                            } else ph_en_mp3.setText("英");
                            if (!s_ph_am.equals("")) {
                                ph_am_mp3.setText("美" + " [" + s_ph_am + "]");
                            } else ph_am_mp3.setText("美");
                            JSONObject audio_addresses = data.getJSONObject("audio_addresses");
                            JSONArray uk = audio_addresses.getJSONArray("uk");
                            JSONArray us = audio_addresses.getJSONArray("us");
                            uri_ph_en_mp3 = uk.getString(0);//英式发音
                            uri_ph_am_mp3 = us.getString(0);//美式发音
                            JSONObject cn_definition = data.getJSONObject("cn_definition");
                            String expl = cn_definition.getString("defn");
                            mean.setText(expl);//中文解释
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    public void pron_en(View view) {
        final Uri uri = Uri.parse(uri_ph_en_mp3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(ReciteWordActivity.this, uri);
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
    }

    public void pron_am(View view) {
        final Uri uri = Uri.parse(uri_ph_am_mp3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(ReciteWordActivity.this, uri);
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
    }

    public void getExFromOL(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseurl = "https://api.shanbay.com/bdc/example/?type=sys&vocabulary_id=";
                    baseurl += id;
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
                    msg.what = 0x1235;
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

    public void getFromOnline(final String tword) {
        word.setText(tword);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String baseurl = "https://api.shanbay.com/bdc/search/?word=";
                    baseurl += tword;
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

    public void showWord(String tword) {
        try {
            Cursor cursor = dbHelper.getWord(dbHelper.getReadableDatabase(), tword);
            StringBuffer sb = new StringBuffer();
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex("isAll")) == 0) {
                    getFromOnline(tword);
                } else {
                    word.setText(cursor.getString(cursor.getColumnIndex("words")));
                    mean.setText(cursor.getString(cursor.getColumnIndex("meaning")));
                    String s_ph_en = cursor.getString(cursor.getColumnIndex("ph_en"));
                    String s_ph_am = cursor.getString(cursor.getColumnIndex("ph_am"));
                    uri_ph_en_mp3 = cursor.getString(cursor.getColumnIndex("ph_en_mp3"));
                    uri_ph_am_mp3 = cursor.getString(cursor.getColumnIndex("ph_am_mp3"));
                    ph_en_mp3.setText("英" + " [" + s_ph_en + "]");
                    ph_am_mp3.setText("美" + " [" + s_ph_am + "]");
                }
            }
            if (cursor.getCount() == 0) {//本地数据库没有，联网查询
                getFromOnline(tword);
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "未查到该单词", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void yes(View view) {
        switch (((Button) view).getText().toString()) {
            case "认识":
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                yes.setLayoutParams(lp);
                rl.setVisibility(View.VISIBLE);
                no.setVisibility(View.INVISIBLE);
                setRecord(word.getText().toString());
                yes.setText("下一个");
                break;
            case "下一个":
                if (dbHelper.getSkipTotal(dbHelper.getReadableDatabase()) == today_words.getCount()) {
                    System.out.println("完成");
                    System.out.println(dbHelper.getSkipTotal(dbHelper.getReadableDatabase()));
                    rl1.setVisibility(View.INVISIBLE);
                    mean.setText("今日任务已完成");
                    yes.setText("确定");
                } else if (today_words.moveToNext()) {//移动到下一条skip！=1记录
                    while (dbHelper.getSkip(dbHelper.getReadableDatabase(), today_words.getString(today_words.getColumnIndex("word"))) == 1) {
                        if (today_words.moveToNext()) {

                        } else {
                            today_words.moveToFirst();
                        }
                    }
                    showWord(today_words.getString(today_words.getColumnIndex("word")));
                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
                    lp2.removeRule(RelativeLayout.CENTER_VERTICAL);
                    yes.setLayoutParams(lp2);
                    rl.setVisibility(View.INVISIBLE);
                    no.setVisibility(View.VISIBLE);
                    yes.setText("认识");
                }else {
                    today_words.moveToFirst();
                    while (dbHelper.getSkip(dbHelper.getReadableDatabase(), today_words.getString(today_words.getColumnIndex("word"))) == 1) {
                        if (today_words.moveToNext()) {

                        } else {
                            today_words.moveToFirst();
                        }
                    }
                    showWord(today_words.getString(today_words.getColumnIndex("word")));
                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
                    lp2.removeRule(RelativeLayout.CENTER_VERTICAL);
                    yes.setLayoutParams(lp2);
                    rl.setVisibility(View.INVISIBLE);
                    no.setVisibility(View.VISIBLE);
                    yes.setText("认识");
                }
                break;
            case "确定":
                this.finish();
                break;
        }
    }

    /**
     * 对认识的单词，将record表中level设置+2，today_cmp +1,total_words +1
     */
    private void setRecord(String word) {
        dbHelper.setRecord(dbHelper.getWritableDatabase(), getIntent().getStringExtra("username"), word);
    }

    public void no(View view) {
        switch (((Button) view).getText().toString()) {
            case "不认识":
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                yes.setLayoutParams(lp);
                rl.setVisibility(View.VISIBLE);
                no.setVisibility(View.INVISIBLE);
                yes.setText("下一个");
                break;
        }
    }
}
