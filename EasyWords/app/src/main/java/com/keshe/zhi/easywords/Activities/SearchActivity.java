package com.keshe.zhi.easywords.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.keshe.zhi.easywords.db.MyDatabaseHelper;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    MyDatabaseHelper dbhelper;
    String table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        dbhelper = MyDatabaseHelper.getDbHelper(this);
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://bishe.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        WilddogAuth mAuth = WilddogAuth.getInstance();
        table = getSharedPreferences(mAuth.getCurrentUser().getUid(), MODE_PRIVATE).getString("category", "cet6");
        final FloatingSearchView searchView = (FloatingSearchView) findViewById(R.id.searchview);
        searchView.setSearchFocused(true);

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                Cursor cursor = dbhelper.getSuggestion(dbhelper.getWritableDatabase(),newQuery,table);
                ArrayList<SearchSuggestion> list = new ArrayList<SearchSuggestion>();
                while (cursor.moveToNext()) {
                    String content = cursor.getString(0);
                    SearchSuggestion suggestion = new MySearchSuggestion(content);
                    list.add(suggestion);
                }
                cursor.close();
                searchView.swapSuggestions(list);
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Intent intent = new Intent();
                intent.putExtra("query", searchSuggestion.getBody());
                setResult(1234,intent);
                SearchActivity.this.finish();

            }

            @Override
            public void onSearchAction(String currentQuery) {
                Intent intent = new Intent();
                intent.putExtra("query", currentQuery);
                setResult(1234,intent);
                SearchActivity.this.finish();
            }
        });

        searchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                    leftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_history_black_24dp));
            }
        });
    }

    private static class MySearchSuggestion implements SearchSuggestion{
        String body;
        MySearchSuggestion(String body) {
            this.body=body;
        }

        @Override
        public String getBody() {
            return body;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }
    }

}
