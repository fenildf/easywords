package com.keshe.zhi.easywords.Activities;

import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    MyDatabaseHelper dbHelper = null;
    TextView mean = null;
    TextView word = null;
    CheckBox isKeep = null;
    Button ph_en = null;
    Button ph_am = null;
    Handler handler = null;
    TextView example = null;
    String s_ph_en = "";
    String s_ph_am = "";
    String ph_en_mp3 = "";
    String ph_am_mp3 = "";

    Map<String,String> map = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = MyDatabaseHelper.getDbHelper(this);
        mean = (TextView) findViewById(R.id.textView);
        word = (TextView) findViewById(R.id.textView8);
        isKeep = (CheckBox) findViewById(R.id.checkbox);
        checkWordState(getIntent().getStringExtra("query"),getIntent().getStringExtra("username"));
        isKeep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbHelper.addToNote(dbHelper.getWritableDatabase(), getIntent().getStringExtra("query"), getIntent().getStringExtra("username"));
                }else {
                    dbHelper.reFroNote(dbHelper.getWritableDatabase(), getIntent().getStringExtra("query"), getIntent().getStringExtra("username"));
                }
            }
        });
        ph_en = (Button) findViewById(R.id.button6);
        ph_am = (Button) findViewById(R.id.button7);
        Typeface tf=Typeface.createFromAsset(getAssets(), "segoeui.ttf");
        ph_am.setTypeface(tf);
        ph_en.setTypeface(tf);
        example = (TextView) findViewById(R.id.textView15);
        shouResult();
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
                            ph_en.setText(ph_en.getText() + " [" + s_ph_en + "]");
                            ph_am.setText(ph_am.getText() + " [" + s_ph_am + "]");
                            JSONObject audio_addresses = data.getJSONObject("audio_addresses");
                            JSONArray uk = audio_addresses.getJSONArray("uk");
                            JSONArray us = audio_addresses.getJSONArray("us");
                            ph_en_mp3 = uk.getString(0);//英式发音
                            ph_am_mp3 = us.getString(0);//美式发音
                            JSONObject cn_definition = data.getJSONObject("cn_definition");
                            String expl = cn_definition.getString("defn");
                            mean.setText(expl);//中文解释
                            map.put("mean", expl);
                            map.put("ph_en", s_ph_en);
                            map.put("ph_am", s_ph_am);
                            map.put("ph_en_mp3", ph_en_mp3);
                            map.put("ph_am_mp3", ph_am_mp3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x1235:
                        String result2 = msg.getData().getString("result");
                        String sentence_ol = "";
                        try {
                            JSONObject root2 = new JSONObject(result2);
                            JSONArray data = root2.getJSONArray("data");
                            for (int i = 0; i < (data.length() > 2 ? 2 : data.length()); i++) {
                                JSONObject sentence = data.getJSONObject(i);
                                sentence_ol += sentence.getString("annotation").replaceAll("<vocab>", "").replaceAll("</vocab>", "") + "\n";
                                sentence_ol += sentence.getString("translation") + "\n";
                            }
                            example.setText(sentence_ol);
                            map.put("example", sentence_ol);
                            if (dbHelper.insertOrupdate(dbHelper.getWritableDatabase(), map)){
                                System.out.println("insert or update true");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 检查单词是否已经加入单词本，改变checkbox状态
     * @param word 单词
     * @param name 用户名
     */
    private void checkWordState(String word,String name) {
        if (dbHelper.isWordCollec(dbHelper.getReadableDatabase(), word, name)) {
            isKeep.setChecked(true);
        }else{
            isKeep.setChecked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void pron_en(View view) {
        final Uri uri = Uri.parse(ph_en_mp3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(MainActivity.this, uri);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.release();
                    }
                }
            }
        }).start();
    }

    public void pron_am(View view) {
        final Uri uri = Uri.parse(ph_am_mp3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(MainActivity.this, uri);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
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

    private void shouResult() {
        final String tword = getIntent().getStringExtra("query");
        map.put("word", tword);
        try {
            Cursor cursor = dbHelper.getWord(dbHelper.getReadableDatabase(), tword);
            StringBuffer sb = new StringBuffer();
            while (cursor.moveToNext()) {
                if (cursor.getInt(cursor.getColumnIndex("isAll")) == 0) {
                    getFromOnline(tword);
                } else {
                    word.setText(cursor.getString(cursor.getColumnIndex("words")));
                    mean.setText(cursor.getString(cursor.getColumnIndex("meaning")));
                    example.setText(cursor.getString(cursor.getColumnIndex("example")));
                    s_ph_en = cursor.getString(cursor.getColumnIndex("ph_en"));
                    s_ph_am = cursor.getString(cursor.getColumnIndex("ph_am"));
                    ph_en_mp3 = cursor.getString(cursor.getColumnIndex("ph_en_mp3"));
                    ph_am_mp3 = cursor.getString(cursor.getColumnIndex("ph_am_mp3"));
                    ph_en.setText(ph_en.getText() + " [" + s_ph_en + "]");
                    ph_am.setText(ph_am.getText() + " [" + s_ph_am + "]");
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
}
