package com.keshe.zhi.easywords.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.keshe.zhi.easywords.fragments.RegByMail;
import com.keshe.zhi.easywords.fragments.RegByPhone;

public class RegisterActivity extends AppCompatActivity implements RegByMail.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawable(null);
        getSupportFragmentManager().beginTransaction().add(R.id.reg_frg,new RegByPhone(),"regByPhone").commit();
    }

    public void regByMail(View view) {
        System.out.println("byMail");
        getSupportFragmentManager().beginTransaction().replace(R.id.reg_frg,new RegByMail(),"regByPhone").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
