package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReciteBySpelling.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReciteBySpelling#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReciteBySpelling extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WORD = "param1";
    private static final String TABLE = "param2";
    MyDatabaseHelper dbhelper;
    TextView mean_tv;
    EditText write_tv;
    Button showMean_btn;
    Button submit_btn;
    TextView hidden_tv;
    ImageButton pronounce;
    ImageView mark;
    // TODO: Rename and change types of parameters
    private String word;
    private String table;

    private OnFragmentInteractionListener mListener;

    public ReciteBySpelling() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param word Parameter 1.
     * @param table Parameter 2.
     * @return A new instance of fragment ReciteBySpelling.
     */
    // TODO: Rename and change types and number of parameters
    public static ReciteBySpelling newInstance(String word, String table) {
        ReciteBySpelling fragment = new ReciteBySpelling();
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
        View view = inflater.inflate(R.layout.fragment_recite_by_spelling, container, false);
        dbhelper = MyDatabaseHelper.getDbHelper(getContext());
        mListener.isCollected(dbhelper.checkIsCollected(dbhelper.getWritableDatabase(),word,table));
        mean_tv = (TextView) view.findViewById(R.id.textView37);
        write_tv = (EditText) view.findViewById(R.id.editText15);
        showMean_btn = (Button) view.findViewById(R.id.button20);
        submit_btn = (Button) view.findViewById(R.id.button21);
        hidden_tv = (TextView) view.findViewById(R.id.textView33);
        pronounce = (ImageButton) view.findViewById(R.id.imageButton4);
        mark = (ImageView) view.findViewById(R.id.imageView26);
        hidden_tv.setText(word);

        Cursor cursor = dbhelper.getWordMean(dbhelper.getWritableDatabase(), table, word);
        String wordMean = "";
        String pron = "";
        while (cursor.moveToNext()) {
            wordMean = cursor.getString(cursor.getColumnIndex("analyzes"));
            pron = cursor.getString(cursor.getColumnIndex("music_path"));
        }
        cursor.close();

        mean_tv.setText(wordMean);

        final String finalPron = pron;
        pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Uri uri = Uri.parse(finalPron);
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

        showMean_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(word,"spell_word");
            }
        });

        final String finalWordMean = word.trim();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1234:
                        onButtonPressed("next","spell_word");
                        break;
                    case 2345:
                        onButtonPressed(word,"spell_word");
                        break;
                }
            }
        };
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_answer=write_tv.getText().toString();
                System.out.println(user_answer);
                System.out.println(finalWordMean);
                if (finalWordMean.equals(user_answer)) {
                    mark.setVisibility(View.VISIBLE);
                    mark.setImageDrawable(getResources().getDrawable(R.drawable.ic_right));
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
                    mark.setVisibility(View.VISIBLE);
                    mark.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                handler.sendEmptyMessage(2345);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri,String pattern) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri,pattern);
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
        void onFragmentInteraction(String uri,String pattern);
        void isCollected(boolean isCollected);
    }
}
