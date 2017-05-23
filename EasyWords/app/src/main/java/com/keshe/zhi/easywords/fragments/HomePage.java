package com.keshe.zhi.easywords.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.Activities.ReciteWordActivity;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment {
    TextView username;
    TextView category;
    ProgressBar progress;
    TextView progress_tv;
    TextView last_days;
    TextView total_learned;
    TextView today_words;
    CardView start_btn;

    public HomePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        username = (TextView) view.findViewById(R.id.textView32);
        category= (TextView) view.findViewById(R.id.textView48);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        progress_tv = (TextView) view.findViewById(R.id.textView28);
        last_days = (TextView) view.findViewById(R.id.textView36);
        total_learned = (TextView) view.findViewById(R.id.textView43);
        today_words= (TextView) view.findViewById(R.id.textView46);
        start_btn = (CardView) view.findViewById(R.id.start);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("开始学习");
                Intent intent = new Intent(getContext(), ReciteWordActivity.class);
                startActivity(intent);
            }
        });

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(getContext(), options);
        WilddogAuth mAuth = WilddogAuth.getInstance();

        username.setText(mAuth.getCurrentUser().getDisplayName());


        SharedPreferences preferences = getActivity().getSharedPreferences(mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        //词库
        category.setText(preferences.getString("category","cet6"));

        String finished = preferences.getString("today_finished", "0");
        String total = preferences.getString("today_words", "20");
        progress.setProgress(Integer.parseInt(finished)*100/Integer.parseInt(total));
        progress_tv.setText("已完成:" + finished + "/" + total);
        //坚持天数
        last_days.setText(preferences.getString("last_days","0"));
        //共学习单词
        total_learned.setText(preferences.getString("total_learned","0"));
        //今日单词数
        today_words.setText(total);

        return view;
    }

}
