package com.keshe.zhi.easywords.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.keshe.zhi.easywords.Activities.R;
import com.keshe.zhi.easywords.words.WordInfo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReciteWord_show.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReciteWord_show#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReciteWord_show extends Fragment {
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
        // Inflate the layout for this fragment
        Gson gson = new Gson();
        gson.fromJson("", WordInfo.class);
        return inflater.inflate(R.layout.fragment_recite_word_show, container, false);
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
