package com.keshe.zhi.easywords.Activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckActivity extends AppCompatActivity {
    private Button check_btn;
    private MyDatabaseHelper dbhelper;
    private MaterialCalendarView calendarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        check_btn = (Button) findViewById(R.id.button22);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarview);
    }

    public void checkIn(View view) {
        Date date = new Date();
        dbhelper.checkIn(dbhelper.getWritableDatabase(), date);
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = dbhelper.getCheckedDay(dbhelper.getWritableDatabase());
        while (cursor.moveToNext()) {
            String date=cursor.getString(0);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            try {
                Date date1=format.parse(date);
                calendarView.setDateSelected(date1,true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
