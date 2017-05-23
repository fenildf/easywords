package com.keshe.zhi.easywords.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by zhi on 2016/12/31 0031.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    Context context;

    public MyDatabaseHelper(Context context) {
        super(context, "easy_word.db", null, 1);
        this.context=context;
    }

    public static MyDatabaseHelper getDbHelper(Context context) {
        return new MyDatabaseHelper(context);
    }

    /**
     * 将json对象保存到数据库中
     *
     * @param database
     * @param object
     * @param table
     * @return
     */
    public boolean saveToDb(SQLiteDatabase database, JSONObject object, String table) {
        long result = -1;
        try {
            String analyzes = object.getString("analyzes");//单词意思
            String content = object.getString("content");//单词
            String audio_path_an = object.getString("audio_path_an");//
            String audio_path_en = object.getString("audio_path_en");
            String example_en = object.getString("example_en");
            String example_zh = object.getString("example_zh");
            String favorite = object.getString("favorite");
            String id = object.getString("id");
            String music_path = object.getString("music_path");
            String phonogram_an = object.getString("phonogram_an");
            String phonogram_en = object.getString("phonogram_en");
            String sortNum = object.getString("sortNum");
            String word_id = object.getString("word_id");

            ContentValues contentValues = new ContentValues();
            contentValues.put("analyzes", analyzes);
            contentValues.put("content", content);
            contentValues.put("audio_path_an", audio_path_an);
            contentValues.put("audio_path_en", audio_path_en);
            contentValues.put("example_en", example_en);
            contentValues.put("example_zh", example_zh);
            contentValues.put("favorite", favorite);
            contentValues.put("id", id);
            contentValues.put("music_path", music_path);
            contentValues.put("phonogram_an", phonogram_an);
            contentValues.put("phonogram_en", phonogram_en);
            contentValues.put("sortNum", sortNum);
            contentValues.put("word_id", word_id);

            result = database.insert(table, null, contentValues);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            database.close();
        }

        return result != -1;
    }


    /**
     * 准备今日要复习的单词
     *
     * @param database
     * @param count           今日复习总数
     * @param table_name      用户当前词库
     * @param today_new_words 今日新词数量
     */
    public void prepareTodayWords(SQLiteDatabase database, String count, String table_name, String today_new_words) {

        //查询用户已学习单词总数
        Cursor cursor1 = database.rawQuery("select count(*) from user_record where table_name='" + table_name + "'", null);
        int learned_count = 0;
        while (cursor1.moveToNext()) {
            learned_count = Integer.parseInt(cursor1.getString(0));
        }

        cursor1.close();

        int from_learned = 0;
        int from_new = 0;
        //如果用户学习过的次数大于今日总词数-新词数
        if (learned_count >= (Integer.parseInt(count) - Integer.parseInt(today_new_words))) {
            from_new = Integer.parseInt(today_new_words);
            from_learned = Integer.parseInt(count) - Integer.parseInt(today_new_words);
        } else {
            from_learned = learned_count;
            from_new = Integer.parseInt(count) - learned_count;
        }


        //select sortNum from table_name where no exists
        // (select*from user_record where user_record.word_num=table_name.sortNum) limit count
        //从已学单词表和词库中选出指定数量的单词
        Cursor cursor2 = database.rawQuery("select*from(select sortNum from " + table_name + " where not exists " +
                "(select*from user_record where user_record.word_num=" + table_name + ".sortNum) limit " + from_new + ") union select*from(select word_num" +
                " from user_record where table_name='" + table_name + "' limit " + from_learned + ")", null);


        //将今日要学习的单词的id存到今日要学习的单词表中
        while (cursor2.moveToNext()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("table_name", table_name);
            contentValues.put("word_num", cursor2.getString(0));
            database.insert("words_today", null, contentValues);
        }

        cursor2.close();

    }


    public Cursor todayWordsInfo(SQLiteDatabase database, String tablename) {

        return database.rawQuery("select*from " + tablename + " where exists (select*from words_today where " +
                "table_name='" + tablename + "' and words_today.word_num=" + tablename + ".sortNum and words_today.is_finished=0)", null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL(CREATE_USER_TABLE_SQL);
//        sqLiteDatabase.execSQL(create_cet6_table_sql);
    }

//    /**
//     * 根据单词从数据库中查询该单词所有信息
//     *
//     * @return 返回数据库游标
//     */
//    public Cursor getWord(SQLiteDatabase db, String word) {
//        try {
//            Cursor cursor = null;
//            cursor = db.rawQuery("select * from cet6 where words='" + word + "'", null);
//            return cursor;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 设置该单词is_finished标记
     *
     * @param writableDatabase
     * @param table_name
     * @param word_mean
     */
    public void setWordFinished(SQLiteDatabase writableDatabase, String table_name, String word_mean) {
        Cursor cursor = writableDatabase.rawQuery("select sortNum from " + table_name + " where content='" + word_mean + "'", null);
        String word_num = "";
        while (cursor.moveToNext()) {
            word_num = cursor.getString(0);
        }
        cursor.close();

        //加入已学习表
        Cursor cursor2 = writableDatabase.rawQuery("select*from user_record where table_name=? and word_num=?", new String[]{table_name, word_num});
        if (cursor2.getCount() == 0) {//如果表中已有，则不插入
            ContentValues contentValues1 = new ContentValues();
            contentValues1.put("table_name", table_name);
            contentValues1.put("word_num", word_num);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String date = simpleDateFormat.format(System.currentTimeMillis());
            contentValues1.put("finishtime", date);
            writableDatabase.insert("user_record", null, contentValues1);
        }

        cursor2.close();

        Cursor cursor3 =writableDatabase.rawQuery("select*from user_record where table_name=?", new String[]{table_name});
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(context, options);
        final WilddogAuth mAuth = WilddogAuth.getInstance();
        SharedPreferences sp = context.getSharedPreferences(mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        sp.edit().putString("total_learned", String.valueOf(cursor3.getCount())).apply();
        cursor3.close();

        //在今日单词表中将该单词标记为完成
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_finished", 1);
        writableDatabase.update("words_today", contentValues, "table_name=? and word_num=?", new String[]{table_name, word_num});
    }

    public Cursor getWordMean(SQLiteDatabase writableDatabase, String table, String word) {
        return writableDatabase.rawQuery("select*from " + table + " where content='" + word + "'", null);
    }

    public void cleanWordsToday(SQLiteDatabase writableDatabase, String category) {
        writableDatabase.delete("words_today", "table_name=?", new String[]{category});
    }

    /**
     * 登录
     *
     * @param db
     * @param username
     * @param passwd
     * @return
     */
//    public Cursor login(SQLiteDatabase db, String username, String passwd) {
//        try {
//            Cursor cursor = db.rawQuery("select _id, name from user where name='" + username + "' and passwd='" + passwd + "'", null);
//            return cursor;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 用户注册
     *
     * @param db
     * @param username
     * @param passwd
     * @return
     */
//    public boolean register(SQLiteDatabase db, String username, String passwd) {
//        ContentValues cv = new ContentValues();
//        cv.put("name", username);
//        cv.put("passwd", passwd);
//        try {
//            if (db.insert("user", null, cv) == -1) {
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public boolean isUserExist(SQLiteDatabase db, String name) {
//        try {
//            Cursor cursor = db.rawQuery("select * from user where name=?", new String[]{name});
//            if (cursor.getCount() > 0) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

//    public Cursor getSenToday(SQLiteDatabase db, String date) {
//        try {
//            Cursor cursor = db.rawQuery("select*from sentences where date=?", new String[]{date});
//            return cursor;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public void addToNote(SQLiteDatabase db, String word, String name) {
//        ContentValues cv = new ContentValues();
//        cv.put("word", word);
//        cv.put("username", name);
//        try {
//            db.insert("words_note", null, cv);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void reFroNote(SQLiteDatabase db, String word, String name) {
//        try {
//            db.delete("words_note", "username=? and word=?", new String[]{name, word});
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 单词是否已经加入单词本
     *
     * @param db
     * @param word
     * @param name
     * @return
     */
//    public boolean isWordCollec(SQLiteDatabase db, String word, String name) {
//        try {
//            Cursor cursor = db.rawQuery("select*from words_note where username=? and word=?", new String[]{name, word});
//            if (cursor.getCount() == 0) {
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//
//        }
//        return false;
//    }

    /**
     * 保存每日一句
     *
     * @param db
     * @param contentValues
     * @return
     */
//    public boolean addSentence(SQLiteDatabase db, ContentValues contentValues) {
//        try {
//            if (db.rawQuery("select*from sentences where date=?", new String[]{(String) contentValues.get("date")}).getCount() == 0) {
//                System.out.println("notaddtodaysentence");
//                if (db.insert("sentences", null, contentValues) == -1) {
//                    return false;
//                } else {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 没有则插入，有则更新
     *
     * @param db
     * @param map
     * @return
     */
//    public boolean insertOrupdate(SQLiteDatabase db, Map map) {
//        try {
//            String word = (String) map.get("word");
//            Cursor cursor = db.rawQuery("select * from cet6 where words=?", new String[]{word});
//            ContentValues cv = new ContentValues();
//            cv.put("words", word);
//            cv.put("meaning", (String) map.get("mean"));
//            cv.put("example", (String) map.get("example"));
//            cv.put("ph_en", (String) map.get("ph_en"));
//            cv.put("ph_am", (String) map.get("ph_am"));
//            cv.put("ph_en_mp3", (String) map.get("ph_en_mp3"));
//            cv.put("ph_am_mp3", (String) map.get("ph_am_mp3"));
//            cv.put("isAll", 1);
//            if (cursor.getCount() == 0) {//数据库中没有该单词则插入，有则修改
//                if (db.insert("cet6", null, cv) != -1) {
//                    return true;
//                } else return false;
//            } else {
//                if (db.update("cet6", cv, "words=?", new String[]{word}) != 1) {
//                    return false;
//                } else return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 获取用户的生词
     *
     * @param db
     * @param name
     * @return
     */
//    public Cursor getUsersWord(SQLiteDatabase db, String name) {
//        try {
//            Cursor cursor = db.rawQuery("select*from words_note where username=?", new String[]{name});
//            return cursor;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 获取用户学习计划
     *
     * @param db
     * @param username
     * @return
     */
//    public Cursor getRecord(SQLiteDatabase db, String username) {
//        try {
//            Cursor cursor = db.rawQuery("select*from record where username=?", new String[]{username});
//            return cursor;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public void setToday(SQLiteDatabase db, int today, int today_n, String username) {
//        try {
//            if (db.rawQuery("select*from record where username=?", new String[]{username}).getCount() != 0) {
//                ContentValues cv = new ContentValues();
//                cv.put("today_words", today);
//                cv.put("today_nword", today_n);
//                db.update("record", cv, "username=?", new String[]{username});
//            } else {
//                ContentValues cv = new ContentValues();
//                cv.put("today_words", today);
//                cv.put("today_nword", today_n);
//                cv.put("username", username);
//                db.insert("record", null, cv);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 先判断已背过的单词数是否大于今日任务量，若是，按照熟练度选取任务量-新词量 个已背过单词，再选取新词量个新词
     * 若否，选取所有背过的单词，再选取剩下的新词，总数为今日任务量
     *
     * @param db
     * @param username
     * @return
     */
//    public Cursor getTodayWords(SQLiteDatabase db, String username) {
//        try {
//            Cursor t = db.rawQuery("select today_words from record where username=?", new String[]{username});
//            t.moveToFirst();
//            if (t.getInt(t.getColumnIndex("today_words")) == last_pos(db, username) || last_pos(db, username) == 0) {//如果今日任务数等于已完成数，则更新今日单词表,今日已完成数为0时，也更新今日单词表，用于初次加载
//                System.out.println("getTodayWords---");
//                Cursor record = db.rawQuery("select*from record where username=?", new String[]{username});
//                int today_total = 0;//今日总任务量
//                int today_n = 0;//今日新词数
//                int offset = db.rawQuery("select*from cet6 where level>0", null).getCount();
//                while (record.moveToNext()) {
//                    today_total = record.getInt(record.getColumnIndex("today_words"));
//                    today_n = record.getInt(record.getColumnIndex("today_nword"));
//                }
//                Cursor result = null;
//                if (offset >= today_total) {//已经背过的单词总数大于等于今日任务量
//                    System.out.println("已经背过的单词大于等于今日任务量");
//                    result = db.rawQuery("select*from(select*from cet6 where level>0 order by level asc limit " + (today_total - today_n) + ") union select*from(select*from cet6 limit " + today_n + " offset " + offset + ")", null);
//                } else {
//                    System.out.println("已经背过的单词不大于今日任务量");
//                    result = db.rawQuery("select*from(select*from cet6 limit " + offset + ") union select*from(select*from cet6 limit " + (today_total - offset) + " offset " + offset + ")", null);
//                }
//                db.delete("today_word", null, null);
//                while (result.moveToNext()) {
//                    ContentValues cv = new ContentValues();
//                    cv.put("word", result.getString(result.getColumnIndex("words")));
//                    db.insert("today_word", null, cv);
//                }
//                result.close();
//                Cursor today_words = db.rawQuery("select*from today_word", null);
//                record.close();
//                t.close();
//                return today_words;
//            } else {
//                Cursor today_words = db.rawQuery("select*from today_word", null);
//                t.close();
//                return today_words;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public int last_pos(SQLiteDatabase database, String username) {
//        int pos = 0;
//        try {
//            Cursor cursor = database.rawQuery("select today_cmp from record where username=?", new String[]{username});
//            while (cursor.moveToNext()) {
//                pos = cursor.getInt(cursor.getColumnIndex("today_cmp"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return pos;
//    }

//    public void setRecord(SQLiteDatabase db, String username, String word) {
//        try {
//            System.out.println("setrecord-----");
//            //更新today_cmp和total_words
//            Cursor record = db.rawQuery("select*from record where username=?", new String[]{username});
//            int today_cmp = 0;
//            int total_words = 0;
//            while (record.moveToNext()) {
//                today_cmp = record.getInt(record.getColumnIndex("today_cmp"));
//                total_words = record.getInt(record.getColumnIndex("total_words"));
//            }
//            ContentValues cv = new ContentValues();
//            cv.put("today_cmp", today_cmp + 1);
//            cv.put("total_words", total_words + 1);
//            db.update("record", cv, "username=?", new String[]{username});
//
//            ContentValues cv3 = new ContentValues();
//            cv3.put("skip", "1");
//            db.update("today_word", cv3, "word=?", new String[]{word});
//
//            //更新level
//            Cursor cet6 = db.rawQuery("select level from cet6 where words=?", new String[]{word});
//            int level = 0;
//            while (cet6.moveToNext()) {
//                level = cet6.getInt(cet6.getColumnIndex("level"));
//            }
//            ContentValues cv2 = new ContentValues();
//            cv2.put("level", level + 2);
//            db.update("cet6", cv2, "words=?", new String[]{word});
//            cet6.close();
//            record.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public int getSkipTotal(SQLiteDatabase db) {
//        int total = 0;
//        try {
//            Cursor cursor = db.rawQuery("select*from today_word where skip=?", new String[]{"1"});
//            total = cursor.getCount();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return total;
//    }

//    public void deleteRecord(SQLiteDatabase db, String username) {
//        try {
//            ContentValues cv = new ContentValues();
//            cv.put("today_cmp", 0);
//            cv.put("offset", 0);
//            db.update("record", cv, "username=?", new String[]{username});
//            System.out.println("deleteRecord---");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void initUserRecord(SQLiteDatabase db, String username) {
//        try {
//            Cursor cursor = db.rawQuery("select*from record where username=?", new String[]{username});
//            if (cursor.getCount() == 0) {
//                ContentValues cv = new ContentValues();
//                cv.put("username", username);
//                cv.put("today_words", 20);
//                cv.put("today_nword", 3);
//                db.insert("record", null, cv);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public int getSkip(SQLiteDatabase db, String word) {
//        int skip=0;
//        try {
//            Cursor cursor = db.rawQuery("select skip from today_word where word=?", new String[]{word});
//            cursor.moveToFirst();
//            skip = cursor.getInt(cursor.getColumnIndex("skip"));
//            cursor.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return skip;
//    }

//    public boolean changePass(SQLiteDatabase db,String username,String oldpass,String pass) {
//        try {
//            Cursor cursor=db.rawQuery("select*from user where name=? and passwd=?", new String[]{username, oldpass});
//            if (cursor.getCount() != 0) {
//                ContentValues cv = new ContentValues();
//                cv.put("name",username);
//                cv.put("passwd",pass);
//                db.update("user", cv, "name=?", new String[]{username});
//                cursor.close();
//                return true;
//            }else {
//                cursor.close();
//                return false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
