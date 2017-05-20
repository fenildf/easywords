package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.keshe.zhi.easywords.fragments.RegByMail;
import com.keshe.zhi.easywords.fragments.RegByPhone;

public class RegisterActivity extends AppCompatActivity implements RegByMail.OnFragmentInteractionListener ,RegByPhone.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setBackgroundDrawable(null);
        getSupportFragmentManager().beginTransaction().add(R.id.reg_frg, new RegByPhone(), "regByPhone").commit();
    }

    public void regByMail(View view) {
        System.out.println("byMail");
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.reg_frg, new RegByMail(), "regByMail").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void goLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
