package com.keshe.zhi.easywords.Activities;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

//每日单词统计显示最近7天每天单词学习书，查询day_record表；进步曲线查询user_record表总记录数
public class ChartActivity extends AppCompatActivity {
    MyDatabaseHelper dbhelper;
    ColumnChartView ccv;
    LineChartView lcv;
    float[] daySum;
    float[] user_record;
    String[] xLabel;
    String[] daysBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        ccv = (ColumnChartView) findViewById(R.id.columnChartView);
        lcv = (LineChartView) findViewById(R.id.lineChartView);
        xLabel = new String[7];
        daySum = new float[7];
        user_record = new float[7];
        daysBefore = new String[7];
        getColumnData();
        showColumnChart();
        getLineData();
        showLineChart();
    }

    private void getColumnData() {
        for (int k = 0; k < 7; k++) {//生成当天往前6天共7天的日期
            Date date = new Date();
            long time = date.getTime() - k * 24 * 60 * 60 * 1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String time_str = format.format(new Date(time));//前k天的日期
            daysBefore[k] = time_str.trim();
            xLabel[k] = time_str.substring(time_str.indexOf("-") + 1);
        }

        for (int i = 0; i < 7; i++) {
            Cursor cursor = dbhelper.getDayRecord(dbhelper.getWritableDatabase(), daysBefore[i]);//获取daysBefore日期的词数
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    daySum[i] = Float.parseFloat(cursor.getString(cursor.getColumnIndex("word_count")));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void showColumnChart() {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < 7; i++) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(daySum[i], 0xffffbb33));
            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(true);
            columns.add(column);
        }
        ColumnChartData data = new ColumnChartData(columns);
        List<AxisValue> axisXvalues = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            axisXvalues.add(new AxisValue(i).setLabel(xLabel[i]));
        }
        Axis axisX = new Axis(axisXvalues).setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("日期");
        axisY.setName("数量");
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        ccv.setColumnChartData(data);
        ccv.setValueSelectionEnabled(true);
    }


    public void getLineData() {
        for (int i = 0; i < 7; i++) {
            Cursor cursor = dbhelper.getUserRecord(dbhelper.getWritableDatabase(), daysBefore[i]);//获取daysBefore日期的词数
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    user_record[i] = Float.parseFloat(cursor.getString(0));
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void showLineChart() {
        List<Line> lines = new ArrayList<Line>();
        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < 7; i++) {
            values.add(new PointValue(i, user_record[user_record.length-1-i]));
        }
        final Line line = new Line(values);
        line.setColor(0xff33b5e5);
        line.setHasLines(true);
        line.setHasPoints(true);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(true);
        lines.add(line);
        LineChartData data = new LineChartData(lines);
        List<AxisValue> axisXvalues = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            axisXvalues.add(new AxisValue(i).setLabel(xLabel[xLabel.length-1-i]));
        }
        Axis axisX = new Axis(axisXvalues).setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("日期");
        axisY.setName("数量");
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        lcv.setLineChartData(data);
        lcv.setValueSelectionEnabled(true);
    }

}
