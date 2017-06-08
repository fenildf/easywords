package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReciteByChoose.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReciteByChoose#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReciteByChoose extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD = "param1";
    private static final String TABLE = "param2";
    MyDatabaseHelper dbhelper;

    TextView word_tv;
    Button[] btns = new Button[4];

    Button next_btn;
    Button remove_btn;

    int right_pos;

    // TODO: Rename and change types of parameters
    private String word;
    private String table;

    private OnFragmentInteractionListener mListener;

    public ReciteByChoose() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word Parameter 1.
     * @param table Parameter 2.
     * @return A new instance of fragment ReciteByChoose.
     */
    // TODO: Rename and change types and number of parameters
    public static ReciteByChoose newInstance(String word, String table) {
        ReciteByChoose fragment = new ReciteByChoose();
        Bundle args = new Bundle();
        args.putString(WORD, word);
        args.putString(TABLE, table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = getArguments().getString(WORD);
            table = getArguments().getString(TABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reciter_by_choose, container, false);
        word_tv = (TextView) view.findViewById(R.id.textView33);
        btns[0] = (Button) view.findViewById(R.id.button17);
        btns[1] = (Button) view.findViewById(R.id.button15);
        btns[2] = (Button) view.findViewById(R.id.button16);
        btns[3] = (Button) view.findViewById(R.id.button14);
        next_btn = (Button) view.findViewById(R.id.button19);
        remove_btn = (Button) view.findViewById(R.id.button18);
        dbhelper = MyDatabaseHelper.getDbHelper(getContext());
        mListener.isCollected(dbhelper.checkIsCollected(dbhelper.getWritableDatabase(),word,table));
        word_tv.setText(word);
        for (int i = 0; i < 4; i++) {
            btns[i].setOnClickListener(this);
        }
        Cursor cursor = dbhelper.getOtherMeans(dbhelper.getWritableDatabase(), word, table);
        right_pos = new Random().nextInt(4);//产生正确选项的位置
        int temp = 0;
        while (cursor.moveToNext()) {
            if (temp == right_pos) {
                temp++;//跳过正确意思的按钮位置
            }
            btns[temp++].setText(cursor.getString(0));
        }
        cursor.close();

        Cursor cursor1 = dbhelper.getWordMean(dbhelper.getWritableDatabase(), table, word);
        while (cursor1.moveToNext()) {
            btns[right_pos].setText(cursor1.getString(cursor1.getColumnIndex("analyzes")));
        }
        cursor1.close();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String info,String pattern) {
        if (mListener != null) {
            mListener.onFragmentInteraction(info,pattern);
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

    @Override
    public void onClick(View v) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1234) {
                    onButtonPressed(word,"pick_means");
                }
            }
        };
        switch (v.getId()) {
            case R.id.button17:
                if (right_pos == 0) {
                    v.setBackgroundColor(0xff61c35e);
                    dbhelper.setWordFinished(dbhelper.getWritableDatabase(),table,word);
                    new Thread(new Runnable() {//半秒后跳转到解释页面
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    v.setBackgroundColor(0xfff16362);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.button15:
                if (right_pos == 1) {
                    v.setBackgroundColor(0xff61c35e);
                    dbhelper.setWordFinished(dbhelper.getWritableDatabase(),table,word);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    v.setBackgroundColor(0xfff16362);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.button16:
                if (right_pos == 2) {
                    v.setBackgroundColor(0xff61c35e);
                    dbhelper.setWordFinished(dbhelper.getWritableDatabase(),table,word);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    v.setBackgroundColor(0xfff16362);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.button14:
                if (right_pos == 3) {
                    v.setBackgroundColor(0xff61c35e);
                    dbhelper.setWordFinished(dbhelper.getWritableDatabase(),table,word);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else {
                    v.setBackgroundColor(0xfff16362);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(1234);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
        }
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
        void onFragmentInteraction(String info,String pattern);
        void isCollected(boolean isCollected);
    }

}
