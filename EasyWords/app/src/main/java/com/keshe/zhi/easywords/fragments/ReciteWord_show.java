package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.Activities.ReciteWordActivity;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReciteWord_show.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReciteWord_show#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReciteWord_show extends Fragment {
    MyDatabaseHelper dbhelper;
    TextView word_tv;
    Button yes_btn;
    Button no_btn;
    ImageButton pron;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD_MEAN = "today_words";
    private static final String TABLE_NAME = "today_finished";
    private static final String WORD_PRON = "word_pron";

    // TODO: Rename and change types of parameters
    private String word_mean;
    private String table_name;
    private String word_pron;

    private OnFragmentInteractionListener mListener;

    public ReciteWord_show() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word_mean  Parameter 1.
     * @param table_name Parameter 2.
     * @return A new instance of fragment ReciteWord_show.
     */
    // TODO: Rename and change types and number of parameters
    public static ReciteWord_show newInstance(String word_mean, String table_name,String word_pron) {
        ReciteWord_show fragment = new ReciteWord_show();
        Bundle args = new Bundle();
        args.putString(WORD_MEAN, word_mean);
        args.putString(TABLE_NAME, table_name);
        args.putString(WORD_PRON, word_pron);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word_mean = getArguments().getString(WORD_MEAN);
            table_name = getArguments().getString(TABLE_NAME);
            word_pron = getArguments().getString(WORD_PRON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbhelper = MyDatabaseHelper.getDbHelper(getContext());

        View view = inflater.inflate(R.layout.fragment_recite_word_show, container, false);
        word_tv = (TextView) view.findViewById(R.id.textView33);
        word_tv.setText(word_mean);
        yes_btn = (Button) view.findViewById(R.id.button10);
        no_btn = (Button) view.findViewById(R.id.button11);
        pron = (ImageButton) view.findViewById(R.id.imageButton2);

        pron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri uri = Uri.parse(word_pron);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MediaPlayer mPlayer = new MediaPlayer();
                        try {
                            mPlayer.setDataSource(getContext(), uri);
                            mPlayer.prepare();
                            mPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (!mPlayer.isPlaying()) {
                                mPlayer.release();
                            }
                        }
                    }
                }).start();
            }
        });

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbhelper.setWordFinished(dbhelper.getWritableDatabase(), table_name, word_mean);
                onButtonPressed("next");
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(word_mean);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String info) {
        if (mListener != null) {
            mListener.onFragmentInteraction(info);
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
        void onFragmentInteraction(String info);
    }

    private static class MyThread extends Thread {
        MyDatabaseHelper databaseHelper;
        JSONObject object;
        String table;

        MyThread(MyDatabaseHelper dbhelper, JSONObject object, String table) {
            this.databaseHelper = dbhelper;
            this.object = object;
            this.table = table;
        }

        @Override
        public void run() {
            if (databaseHelper.saveToDb(databaseHelper.getWritableDatabase(), object, "cet6")) {
                System.out.println("已保存到数据库");
            }
        }

    }
}
