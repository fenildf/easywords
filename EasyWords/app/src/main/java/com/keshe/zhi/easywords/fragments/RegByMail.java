package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegByMail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegByMail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegByMail extends Fragment {
    ImageView image_code;
    EditText mail;
    EditText pass;
    EditText checkCode;
    TextView checkMsg;
    WilddogAuth mAuth;
    Button regis_btn;
    AlertDialog alertDialog;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegByMail() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegByMail.
     */
    // TODO: Rename and change types and number of parameters
    public static RegByMail newInstance(String param1, String param2) {
        RegByMail fragment = new RegByMail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reg_by_mail, container, false);
        alertDialog = new AlertDialog.Builder(getContext()).setCancelable(false).setView(R.layout.doing_some).create();
        mail = (EditText) view.findViewById(R.id.editText);
        pass = (EditText) view.findViewById(R.id.editText7);
        checkCode = (EditText) view.findViewById(R.id.editText8);
        checkMsg = (TextView) view.findViewById(R.id.textView62);
        image_code=(ImageView)view.findViewById(R.id.imageView16);
        regis_btn = (Button) view.findViewById(R.id.button9);
        mail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE==checkMsg.getVisibility()){
                    checkMsg.setVisibility(View.GONE);
                }
                return false;
            }
        });

        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (View.VISIBLE==checkMsg.getVisibility()){
                    checkMsg.setVisibility(View.GONE);
                }
                return false;
            }
        });

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(getActivity(), options);
        mAuth = WilddogAuth.getInstance();

        regis_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(mail.getText().toString()) || "".equals(pass.getText().toString())) {
                    checkMsg.setText("输入不能为空");
                    checkMsg.setVisibility(View.VISIBLE);
                } else if (mail.getText().toString().matches("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$")) {
                    if (checkCode.getText().toString().equals(GenCode.getInstance().getCode().toLowerCase())){
                        alertDialog.show();
                        mAuth.createUserWithEmailAndPassword(mail.getText().toString(), pass.getText().toString())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // 获取用户
                                            WilddogUser user = task.getResult().getWilddogUser();
                                            Log.d("result", user.toString());
                                            alertDialog.dismiss();
                                            String uid = user.getUid();
                                            //在users下新建一个节点
                                            SyncReference ref = WilddogSync.getInstance().getReference();
                                            HashMap<String,String> userinfo = new HashMap<String, String>();
                                            userinfo.put("uid",uid);
                                            userinfo.put("today_words","50");
                                            userinfo.put("today_new_words","8");
                                            userinfo.put("today_progress", "0");
                                            userinfo.put("study_pattern","yes_no");
                                            userinfo.put("last_days","10");
                                            userinfo.put("category","cet6");
                                            SyncReference chiref=ref.child("users").push();
                                            chiref.setValue(userinfo);
                                            //将用户节点-uid对应关系保存到user_key节点下
                                            HashMap<String,String> user_key = new HashMap<String, String>();
                                            user_key.put(uid,chiref.getKey());
                                            ref.child("user_key").setValue(user_key);

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
                    checkMsg.setText("邮箱格式错误");
                    checkMsg.setVisibility(View.VISIBLE);
                }
            }
        });
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
