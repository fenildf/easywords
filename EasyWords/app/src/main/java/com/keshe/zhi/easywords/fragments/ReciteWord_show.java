package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;


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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TODAY_WORDS = "today_words";
    private static final String TODAY_FINISHED = "today_finished";

    // TODO: Rename and change types of parameters
    private String today_words;
    private String today_finished;

    private OnFragmentInteractionListener mListener;

    public ReciteWord_show() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param today_words Parameter 1.
     * @param today_finished Parameter 2.
     * @return A new instance of fragment ReciteWord_show.
     */
    // TODO: Rename and change types and number of parameters
    public static ReciteWord_show newInstance(String today_words, String today_finished) {
        ReciteWord_show fragment = new ReciteWord_show();
        Bundle args = new Bundle();
        args.putString(TODAY_WORDS, today_words);
        args.putString(TODAY_FINISHED, today_finished);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            today_words = getArguments().getString(TODAY_WORDS);
            today_finished = getArguments().getString(TODAY_FINISHED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dbhelper=MyDatabaseHelper.getDbHelper(getContext());
        // Inflate the layout for this fragment
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/postgraduate.txt");
        char[] chars = new char[200];
        int len=-1;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Reader reader = new FileReader(file);
            while ((len=reader.read(chars))!=-1) {
                stringBuilder.append(new String(chars, 0, len));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray array = new JSONArray(stringBuilder.toString());
            System.out.println(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
//                new MyThread(dbhelper,object,"cet6").start();
                new AsyncTask<Object, Integer, Integer>() {
                    @Override
                    protected Integer doInBackground(Object... params) {
                        int result=0;
                        if (((MyDatabaseHelper)params[0]).saveToDb(((MyDatabaseHelper)params[0]).getWritableDatabase(),(JSONObject)params[1],(String)params[2])) {
                            System.out.println("已保存到数据库");
                        }
                        return result;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
//                        if (integer == 100) {
//                            Toast.makeText(getContext(),"完成",Toast.LENGTH_SHORT).show();
//                        }
                    }
                }.execute(dbhelper,object,"kaoyan");
            }
            Toast.makeText(getContext(),"完成",Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        View view =inflater.inflate(R.layout.fragment_recite_word_show, container, false);
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

    private static class MyThread extends Thread{
        MyDatabaseHelper databaseHelper;
        JSONObject object;
        String table;

        MyThread(MyDatabaseHelper dbhelper, JSONObject object, String table) {
            this.databaseHelper=dbhelper;
            this.object=object;
            this.table=table;
        }

        @Override
        public void run() {
            if (databaseHelper.saveToDb(databaseHelper.getWritableDatabase(),object,"cet6")) {
                System.out.println("已保存到数据库");
            }
        }

    }
}
