package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.keshe.zhi.easywords.fragments.ReciteByChoose;
import com.keshe.zhi.easywords.fragments.ReciteBySpelling;
import com.keshe.zhi.easywords.fragments.ReciteWord_show;
import com.keshe.zhi.easywords.fragments.TaskComplete;
import com.keshe.zhi.easywords.fragments.WordMean;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.util.Random;

public class ReciteWordActivity extends AppCompatActivity implements ReciteWord_show.OnFragmentInteractionListener, WordMean.OnFragmentInteractionListener, ReciteByChoose.OnFragmentInteractionListener,ReciteBySpelling.OnFragmentInteractionListener {
    MyDatabaseHelper dbHelper = null;
    String table = "";
    WilddogAuth mAuth;
    TextView progress;
    CheckBox collect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recite_word);
        dbHelper = MyDatabaseHelper.getDbHelper(this);
        progress = (TextView) findViewById(R.id.textView47);
        collect = (CheckBox) findViewById(R.id.checkbox);

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        mAuth = WilddogAuth.getInstance();
        SharedPreferences preferences = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
        //词库
        table = preferences.getString("category", "cet6");
        //学习模式
        String study_pattern = preferences.getString("study_pattern", "yes_no");
        //今日单词总数
        String today_words = preferences.getString("today_words", "20");
        //今日完成总数
        String today_finished = preferences.getString("today_finished", "0");
        //今日新词总数
        String today_new_words = preferences.getString("today_new_words", "3");

        progress.setText(today_finished + "/" + today_words);


        //今日单词是否已经准备了
        String isReady = preferences.getString("isReady", "false");

        if ("false".equals(isReady)) {
            System.out.println("ReciteWordActivity-onCreate:not ready");
            dbHelper.prepareTodayWords(dbHelper.getWritableDatabase(), today_words, table, today_new_words);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("isReady", "true");
            editor.apply();
        }


        //判断学习模式
        switch (study_pattern) {
            case "yes_no"://认识或不认识
                Cursor cursor = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);
                String word = "";
                String pron = "";
                if (!cursor.isLast() && !(cursor.getCount() == 0)) {
                    cursor.moveToNext();
                    word = cursor.getString(cursor.getColumnIndex("content"));
                    pron = cursor.getString(cursor.getColumnIndex("music_path"));
                    getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteWord_show.newInstance(word, table, pron)).commit();
                    cursor.close();
                }
                cursor.close();
                break;
            case "pick_means"://选择意思
                Cursor cursor2 = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);
                String word2 = "";
                String pron2 = "";
                if (!cursor2.isLast() && !(cursor2.getCount() == 0)) {
                    cursor2.moveToNext();
                    word2 = cursor2.getString(cursor2.getColumnIndex("content"));
                    getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteByChoose.newInstance(word2, table)).commit();
                    cursor2.close();
                }
                cursor2.close();
                break;
            case "spell_word"://根据意思拼写单词
                Cursor cursor3 = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);
                String word3 = "";
                String pron3 = "";
                if (!cursor3.isLast() && !(cursor3.getCount() == 0)) {
                    cursor3.moveToNext();
                    word3 = cursor3.getString(cursor3.getColumnIndex("content"));
                    getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteBySpelling.newInstance(word3,table)).commit();
                    cursor3.close();
                }
                cursor3.close();
                break;
        }

        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                Boolean isChecked = ((CheckBox) v).isChecked();
                System.out.println(isChecked);
                Fragment fragment = null;
                for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
                    fragment = getSupportFragmentManager().getFragments().get(i);
                    System.out.println(fragment);
                    if (fragment != null) {
                        break;
                    }
                }
                if (fragment == null) {
                    return;
                }
                TextView textView = (TextView) fragment.getView().findViewById(R.id.textView33);
                if (isChecked) {
                    dbHelper.addToCollection(dbHelper.getWritableDatabase(), textView.getText(), getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE).getString("category", "cet6"));
                } else {
                    dbHelper.deleteFromCollection(dbHelper.getWritableDatabase(), textView.getText(), getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE).getString("category", "cet6"));
                }
            }
        });

        String query = getIntent().getStringExtra("query");//要查的单词
        if (!"".equals(query)) {
            onFragmentInteraction(query,"query");
            progress.setText("查询结果");
        }
    }

    @Override
    public void onFragmentInteraction(String info, String pattern) {
        if ("next".equals(info)) {
            switch (pattern) {
                case "yes_no":
                    Cursor cursor = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);

                    //更新进度提示
                    SharedPreferences sp = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
                    String today_words = sp.getString("today_words", "20");
                    String today_finished = String.valueOf(Integer.parseInt(today_words) - cursor.getCount());
                    sp.edit().putString("today_finished", today_finished).apply();
                    progress.setText(today_finished + "/" + today_words);

                    String word = "";
                    String pron = "";
                    if (!cursor.isLast() && !(cursor.getCount() == 0)) {
                        cursor.move(new Random().nextInt(cursor.getCount()) + 1);
                        word = cursor.getString(cursor.getColumnIndex("content"));
                        pron = cursor.getString(cursor.getColumnIndex("music_path"));
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteWord_show.newInstance(word, table, pron)).commit();
                        cursor.close();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, new TaskComplete()).commit();
                        cursor.close();
                    }
                    cursor.close();
                    break;
                case "pick_means":
                    Cursor cursor2 = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);

                    //更新进度提示
                    SharedPreferences sp2 = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
                    String today_words2 = sp2.getString("today_words", "20");
                    String today_finished2 = String.valueOf(Integer.parseInt(today_words2) - cursor2.getCount());
                    sp2.edit().putString("today_finished", today_finished2).apply();
                    progress.setText(today_finished2 + "/" + today_words2);

                    String word2 = "";
                    if (!cursor2.isLast() && !(cursor2.getCount() == 0)) {
                        cursor2.move(new Random().nextInt(cursor2.getCount()) + 1);
                        word2 = cursor2.getString(cursor2.getColumnIndex("content"));
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteByChoose.newInstance(word2, table)).commit();
                        cursor2.close();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, new TaskComplete()).commit();
                        cursor2.close();
                    }
                    cursor2.close();
                    break;
                case "spell_word":
                    Cursor cursor3 = dbHelper.todayWordsInfo(dbHelper.getWritableDatabase(), table);

                    //更新进度提示
                    SharedPreferences sp3 = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE);
                    String today_words3 = sp3.getString("today_words", "20");
                    String today_finished3 = String.valueOf(Integer.parseInt(today_words3) - cursor3.getCount());
                    sp3.edit().putString("today_finished", today_finished3).apply();
                    progress.setText(today_finished3 + "/" + today_words3);

                    String word3 = "";
                    if (!cursor3.isLast() && !(cursor3.getCount() == 0)) {
                        cursor3.move(new Random().nextInt(cursor3.getCount()) + 1);
                        word3 = cursor3.getString(cursor3.getColumnIndex("content"));
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, ReciteBySpelling.newInstance(word3, table)).commit();
                        cursor3.close();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.word_content, new TaskComplete()).commit();
                        cursor3.close();
                    }
                    cursor3.close();
                    break;
            }
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.word_content, WordMean.newInstance(info, table, pattern)).commit();
        }

    }


    @Override
    public void isCollected(boolean isCollected) {
        collect.setChecked(isCollected);
    }

//    public void pron_en(View view) {
//        final Uri uri = Uri.parse(uri_ph_en_mp3);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                MediaPlayer mPlayer = new MediaPlayer();
//                try {
//                    mPlayer.setDataSource(ReciteWordActivity.this, uri);
//                    mPlayer.prepare();
//                    mPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (!mPlayer.isPlaying()) {
//                        mPlayer.release();
//                    }
//                }
//            }
//        }).start();
//    }
//
//    public void pron_am(View view) {
//        final Uri uri = Uri.parse(uri_ph_am_mp3);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                MediaPlayer mPlayer = new MediaPlayer();
//                try {
//                    mPlayer.setDataSource(ReciteWordActivity.this, uri);
//                    mPlayer.prepare();
//                    mPlayer.start();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (!mPlayer.isPlaying()) {
//                        mPlayer.release();
//                    }
//                }
//            }
//        }).start();
//    }

//    public void getExFromOL(final String id) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String baseurl = "https://api.shanbay.com/bdc/example/?type=sys&vocabulary_id=";
//                    baseurl += id;
//                    URL url = new URL(baseurl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setDoInput(true);
//                    InputStream is = conn.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    String result = "";
//                    char[] buffer = new char[100];
//                    int len = 0;
//                    while ((len = isr.read(buffer)) != -1) {
//                        result += String.valueOf(buffer, 0, len);
//                    }
//                    Message msg = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("result", result);
//                    msg.setData(bundle);
//                    msg.what = 0x1235;
//                    handler.sendMessage(msg);
//                    isr.close();
//                    is.close();
//                    conn.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    public void getFromOnline(final String tword) {
//        word.setText(tword);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String baseurl = "https://api.shanbay.com/bdc/search/?word=";
//                    baseurl += tword;
//                    URL url = new URL(baseurl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setDoInput(true);
//                    InputStream is = conn.getInputStream();
//                    InputStreamReader isr = new InputStreamReader(is);
//                    String result = "";
//                    char[] buffer = new char[100];
//                    int len = 0;
//                    while ((len = isr.read(buffer)) != -1) {
//                        result += String.valueOf(buffer, 0, len);
//                    }
//                    Message msg = new Message();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("result", result);
//                    msg.setData(bundle);
//                    msg.what = 0x1234;
//                    handler.sendMessage(msg);
//                    isr.close();
//                    is.close();
//                    conn.disconnect();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

//    public void showWord(String tword) {
//        try {
//            Cursor cursor = dbHelper.getWord(dbHelper.getReadableDatabase(), tword);
//            StringBuffer sb = new StringBuffer();
//            while (cursor.moveToNext()) {
//                if (cursor.getInt(cursor.getColumnIndex("isAll")) == 0) {
//                    getFromOnline(tword);
//                } else {
//                    word.setText(cursor.getString(cursor.getColumnIndex("words")));
//                    mean.setText(cursor.getString(cursor.getColumnIndex("meaning")));
//                    String s_ph_en = cursor.getString(cursor.getColumnIndex("ph_en"));
//                    String s_ph_am = cursor.getString(cursor.getColumnIndex("ph_am"));
//                    uri_ph_en_mp3 = cursor.getString(cursor.getColumnIndex("ph_en_mp3"));
//                    uri_ph_am_mp3 = cursor.getString(cursor.getColumnIndex("ph_am_mp3"));
//                    ph_en_mp3.setText("英" + " [" + s_ph_en + "]");
//                    ph_am_mp3.setText("美" + " [" + s_ph_am + "]");
//                }
//            }
//            if (cursor.getCount() == 0) {//本地数据库没有，联网查询
//                getFromOnline(tword);
//            }
//            cursor.close();
//        } catch (Exception e) {
//            Toast.makeText(this, "未查到该单词", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }

//    public void yes(View view) {
//        switch (((Button) view).getText().toString()) {
//            case "认识":
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
//                lp.addRule(RelativeLayout.CENTER_VERTICAL);
//                yes.setLayoutParams(lp);
//                rl.setVisibility(View.VISIBLE);
//                no.setVisibility(View.INVISIBLE);
//                setRecord(word.getText().toString());
//                yes.setText("下一个");
//                break;
//            case "下一个":
//                if (dbHelper.getSkipTotal(dbHelper.getReadableDatabase()) == today_words.getCount()) {
//                    System.out.println("完成");
//                    System.out.println(dbHelper.getSkipTotal(dbHelper.getReadableDatabase()));
//                    rl1.setVisibility(View.INVISIBLE);
//                    mean.setText("今日任务已完成");
//                    yes.setText("确定");
//                } else if (today_words.moveToNext()) {//移动到下一条skip！=1记录
//                    while (dbHelper.getSkip(dbHelper.getReadableDatabase(), today_words.getString(today_words.getColumnIndex("word"))) == 1) {
//                        if (today_words.moveToNext()) {
//
//                        } else {
//                            today_words.moveToFirst();
//                        }
//                    }
//                    showWord(today_words.getString(today_words.getColumnIndex("word")));
//                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
//                    lp2.removeRule(RelativeLayout.CENTER_VERTICAL);
//                    yes.setLayoutParams(lp2);
//                    rl.setVisibility(View.INVISIBLE);
//                    no.setVisibility(View.VISIBLE);
//                    yes.setText("认识");
//                }else {
//                    today_words.moveToFirst();
//                    while (dbHelper.getSkip(dbHelper.getReadableDatabase(), today_words.getString(today_words.getColumnIndex("word"))) == 1) {
//                        if (today_words.moveToNext()) {
//
//                        } else {
//                            today_words.moveToFirst();
//                        }
//                    }
//                    showWord(today_words.getString(today_words.getColumnIndex("word")));
//                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
//                    lp2.removeRule(RelativeLayout.CENTER_VERTICAL);
//                    yes.setLayoutParams(lp2);
//                    rl.setVisibility(View.INVISIBLE);
//                    no.setVisibility(View.VISIBLE);
//                    yes.setText("认识");
//                }
//                break;
//            case "确定":
//                this.finish();
//                break;
//        }
//    }

    /**
     * 对认识的单词，将record表中level设置+2，today_cmp +1,total_words +1
     */
//    private void setRecord(String word) {
//        dbHelper.setRecord(dbHelper.getWritableDatabase(), getIntent().getStringExtra("username"), word);
//    }
//
//    public void no(View view) {
//        switch (((Button) view).getText().toString()) {
//            case "不认识":
//                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(yes.getWidth(), yes.getHeight());
//                lp.addRule(RelativeLayout.CENTER_VERTICAL);
//                yes.setLayoutParams(lp);
//                rl.setVisibility(View.VISIBLE);
//                no.setVisibility(View.INVISIBLE);
//                yes.setText("下一个");
//                break;
//        }
//    }


}
