package com.keshe.zhi.easywords.fragments;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.utils.GenCode;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegByPhone extends Fragment {
    ImageView image_code;

    public RegByPhone() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reg_by_phone, container, false);
        image_code=(ImageView)view.findViewById(R.id.imageView16);
        image_code.setImageBitmap(GenCode.getInstance().createBitmap());
        image_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(GenCode.getInstance().getCode());
                image_code.setImageBitmap(GenCode.getInstance().createBitmap());
            }
        });
        return view;
    }


}
