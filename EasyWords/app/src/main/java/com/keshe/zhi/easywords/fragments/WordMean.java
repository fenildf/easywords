package com.keshe.zhi.easywords.fragments;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import java.io.IOException;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WordMean.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WordMean#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WordMean extends Fragment {

    MyDatabaseHelper dbhelper;
    TextView word_tv;
    TextView phon_tv;
    TextView mean_tv;
    TextView example_tv;
    Button notShow_btn;
    Button next_btn;
    ImageButton pron_btn;
    String pron_uri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD = "param1";
    private static final String TABLE = "param2";

    // TODO: Rename and change types of parameters
    private String word;
    private String table;

    private OnFragmentInteractionListener mListener;

    public WordMean() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word  Parameter 1.
     * @param table Parameter 2.
     * @return A new instance of fragment WordMean.
     */
    // TODO: Rename and change types and number of parameters
    public static WordMean newInstance(String word, String table) {
        WordMean fragment = new WordMean();
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
        dbhelper = MyDatabaseHelper.getDbHelper(getContext());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_mean, container, false);
        word_tv = (TextView) view.findViewById(R.id.textView37);
        phon_tv = (TextView) view.findViewById(R.id.textView50);
        mean_tv = (TextView) view.findViewById(R.id.textView51);
        example_tv = (TextView) view.findViewById(R.id.textView52);
        notShow_btn = (Button) view.findViewById(R.id.button8);
        next_btn = (Button) view.findViewById(R.id.button);
        pron_btn = (ImageButton) view.findViewById(R.id.imageButton3);


        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed("next");
            }
        });

        notShow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbhelper.setWordFinished(dbhelper.getWritableDatabase(), table, word);
                onButtonPressed("next");
            }
        });

        Cursor cursor = dbhelper.getWordMean(dbhelper.getWritableDatabase(), table, word);
        while (cursor.moveToNext()) {
            word_tv.setText(cursor.getString(cursor.getColumnIndex("content")));
            phon_tv.setText(cursor.getString(cursor.getColumnIndex("phonogram_en")));
            mean_tv.setText(cursor.getString(cursor.getColumnIndex("analyzes")));
            example_tv.setText(cursor.getString(cursor.getColumnIndex("example_en")) + "\n" + cursor.getString(cursor.getColumnIndex("example_zh")));

            pron_uri = cursor.getString(cursor.getColumnIndex("music_path"));
        }
        cursor.close();

        pron_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri uri = Uri.parse(pron_uri);
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
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
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
        void onFragmentInteraction(String info);
    }
}
