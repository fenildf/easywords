package com.keshe.zhi.easywords.fragments;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keshe.zhi.easywords.Activities.LoginActivity;
import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.utils.GenCode;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegByPhone extends Fragment {
    ImageView image_code;
    Button btn_regis;
    EditText phoneNum = null;
    EditText passwd = null;
    EditText checkCode;
    WilddogAuth mAuth;
    TextView checkMsg;
    private OnFragmentInteractionListener mListener;
    AlertDialog alertDialog;

    public RegByPhone() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reg_by_phone, container, false);
        alertDialog = new AlertDialog.Builder(getContext()).setCancelable(false).setView(R.layout.doing_some).create();
        checkMsg = (TextView) view.findViewById(R.id.textView61);
        phoneNum = (EditText) view.findViewById(R.id.editText);
        passwd = (EditText) view.findViewById(R.id.editText7);
        phoneNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE==checkMsg.getVisibility()){
                    checkMsg.setVisibility(View.GONE);
                }
                return false;
            }
        });
        passwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE == checkMsg.getVisibility()) {
                    checkMsg.setVisibility(View.GONE);
                }
                return false;
            }
        });
        checkCode = (EditText) view.findViewById(R.id.editText8);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(getActivity(), options);
        mAuth = WilddogAuth.getInstance();
        btn_regis = (Button) view.findViewById(R.id.button9);
        btn_regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(phoneNum.getText().toString()) || "".equals(passwd.getText().toString())) {
                    checkMsg.setText("输入不能为空");
                    checkMsg.setVisibility(View.VISIBLE);
                } else if (phoneNum.getText().toString().matches("^1\\d{10}$")) {
                    if (checkCode.getText().toString().equals(GenCode.getInstance().getCode().toLowerCase())){
                        alertDialog.show();
                        mAuth.createUserWithPhoneAndPassword(phoneNum.getText().toString(), passwd.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // 获取用户
                                            WilddogUser user = task.getResult().getWilddogUser();
                                            Log.d("result", user.toString());
                                            alertDialog.dismiss();
                                            Toast.makeText(getContext(), "创建成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getContext(), LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            // 错误处理
                                            Log.d("result", task.getException().toString());
                                            alertDialog.dismiss();
                                            Toast.makeText(getActivity(), "创建失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        checkMsg.setText("验证码错误");
                        checkMsg.setVisibility(View.VISIBLE);
                    }
                }else {
                    checkMsg.setText("手机号码格式错误");
                    checkMsg.setVisibility(View.VISIBLE);
                }

            }
        });
        image_code = (ImageView) view.findViewById(R.id.imageView16);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegByMail.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
