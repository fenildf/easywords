package com.keshe.zhi.easywords.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.keshe.zhi.easywords.Activities.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskComplete extends Fragment {


    public TaskComplete() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_complete, container, false);
    }

}
