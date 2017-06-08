package com.keshe.zhi.easywords.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.keshe.zhi.easywords.Activities.ChangeCategory;
import com.keshe.zhi.easywords.Activities.ChartActivity;
import com.keshe.zhi.easywords.Activities.CheckActivity;
import com.keshe.zhi.easywords.Activities.GeneralActivity;
import com.keshe.zhi.easywords.Activities.ReciteWordActivity;
import com.keshe.zhi.easywords.Activities.SearchActivity;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import com.keshe.zhi.easywords.Activities.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {
    private static final int REQUEST_CODE_GENERAL_BASIC = 106;
    TextView username;
    TextView category;
    ProgressBar progress;
    TextView progress_tv;
    TextView last_days;
    TextView total_learned;
    TextView today_words;
    CardView start_btn;
    ImageView changeCategory;
    ImageView doSearch;
    ImageView cam;
    ImageView chart;
    FloatingActionButton floatingActionButton;
    private AlertDialog.Builder alertDialog;
    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1234==requestCode) {
            startActivity(new Intent(getContext(),ReciteWordActivity.class).putExtra("query",data.getStringExtra("query")));
        }
        if (resultCode == 2345&&requestCode==23456) {
            startActivity(new Intent(getContext(),ReciteWordActivity.class).putExtra("query",data.getStringExtra("result")));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        username = (TextView) view.findViewById(R.id.textView32);
        category = (TextView) view.findViewById(R.id.textView48);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        progress_tv = (TextView) view.findViewById(R.id.textView28);
        last_days = (TextView) view.findViewById(R.id.textView36);
        total_learned = (TextView) view.findViewById(R.id.textView43);
        today_words = (TextView) view.findViewById(R.id.textView46);
        start_btn = (CardView) view.findViewById(R.id.start);
        changeCategory = (ImageView) view.findViewById(R.id.imageView11);
        doSearch = (ImageView) view.findViewById(R.id.imageView12);
        cam = (ImageView) view.findViewById(R.id.imageView27);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CheckActivity.class));
            }
        });
        chart = (ImageView) view.findViewById(R.id.imageView6);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChartActivity.class));
            }
        });
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), GeneralActivity.class),23456);
            }
        });
        doSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent,1234);
            }
        });
        changeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangeCategory.class));
            }
        });
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("开始学习");
                Intent intent = new Intent(getContext(), ReciteWordActivity.class);
                intent.putExtra("query", "");
                startActivity(intent);
            }
        });

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(getContext(), options);
        WilddogAuth mAuth = WilddogAuth.getInstance();

        username.setText(mAuth.getCurrentUser().getDisplayName());


        SharedPreferences preferences = getActivity().getSharedPreferences(mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        //词库
        String ciku = preferences.getString("category", "cet6");
        switch (ciku) {
            case "cet4":
                category.setText("四级");
                break;
            case "cet6":
                category.setText("六级");
                break;
            case "cet4_core":
                category.setText("四级核心");
                break;
            case "kaoyan":
                category.setText("考研");
                break;
        }

        String finished = preferences.getString("today_finished", "0");
        String total = preferences.getString("today_words", "20");
        progress.setProgress(Integer.parseInt(finished) * 100 / Integer.parseInt(total));
        progress_tv.setText("已完成:" + finished + "/" + total);
        //坚持天数
        last_days.setText(preferences.getString("last_days", "0"));
        //共学习单词
        total_learned.setText(preferences.getString("total_learned", "0"));
        //今日单词数
        today_words.setText(total);

        return view;
    }

}
