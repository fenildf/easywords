package com.keshe.zhi.easywords.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keshe.zhi.easywords.Activities.R;
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

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(getContext(), options);
        WilddogAuth mAuth = WilddogAuth.getInstance();

        username.setText(mAuth.getCurrentUser().getDisplayName());


        SharedPreferences preferences = getActivity().getSharedPreferences(mAuth.getCurrentUser().getUid(), Context.MODE_PRIVATE);
        category.setText(preferences.getString("category","cet-6"));
        progress.setProgress(0);
        progress_tv.setText("已完成:" + preferences.getString("today_finished", "0") + "/" + preferences.getString("today_words", "50"));
        last_days.setText(preferences.getString("last_days","0"));
        total_learned.setText(preferences.getString("total_learned","0"));
        today_words.setText(preferences.getString("today_words","50"));
        return view;
    }

}
