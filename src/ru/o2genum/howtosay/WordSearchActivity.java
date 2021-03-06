/*
    Copyright (c) 2011, Andrey Moiseev

    Licensed under the Apache License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may obtain
    a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package ru.o2genum.howtosay;

/**
 * Word search activity
 *
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import ru.o2genum.forvo.Pronunciation;
import ru.o2genum.forvo.Word;
import ru.o2genum.forvo.WordAndPronunciation;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.List;

public class WordSearchActivity extends BaseActivity {

    private String query;
    private Word word;
    private String title;
    private WordAdapter wordAdapter;
    private EndlessWordAdapter endlessWordAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(is11Plus() && hasLargeScreen()) {
            intent.setClass(this, DashboardActivity.class);
            startActivity(intent); 
            finish();
        }
        query = intent.getStringExtra(SearchManager.QUERY);
        word = new Word(query);
        title = getString(R.string.searching_for, query);
        wordAdapter = new WordAdapter(this, 0);
        // List view - our key UI element
        endlessWordAdapter =
            new EndlessWordAdapter(this, wordAdapter, R.layout.pendingview);
        initializeUI();
    }

    protected void initializeUI() {
        ListView lv = new ListView(this);
        lv.setAdapter(endlessWordAdapter);
        setTitle(title);
        setView(lv);
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                int position, long id) {
                EndlessWordAdapter a = (EndlessWordAdapter)
                    parent.getAdapter();
                if(!(a.getItemViewType(position) == 
                    Adapter.IGNORE_ITEM_VIEW_TYPE)) {
                        playSound(
                            ((WordAndPronunciation) a.getItem(position))
                            .getPronunciation()
                            .getAudioURL(Pronunciation.AudioFormat.MP3)
                            .toString(), view);
                } else {
                    // Pending view was clicked
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initializeBasicUI();
        initializeUI();
    }

    class WordAdapter extends ArrayAdapter<WordAndPronunciation> {

        public WordAdapter(Context context, int resId) {
            super(context, resId);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
            convertView = (View) inflater.inflate(
                    R.layout.list_item, null);
            }
            TextView textView = (TextView) convertView.findViewById(
                    R.id.text1);
            TextView textView2 = (TextView) convertView.findViewById(
                    R.id.text2);
            textView.setText((CharSequence) getItem(position).getWord().
                    getOriginal());
            int numPronunciations = getItem(position).getWord()
                .getPronunciationsNumber();
            textView2.setText((CharSequence) getLocalizedLanguageName(
                    getItem(position).getPronunciation().getLanguage()
                    .getCode(),
                    getItem(position).getPronunciation().getLanguage()
                    .getLanguageName()) + ", " +
                    String.format(getResources()
                        .getQuantityString(R.plurals.pronunciation_s,
                        numPronunciations), numPronunciations));
            return convertView;
        }

    }

    class EndlessWordAdapter extends EndlessAdapter {

        List<WordAndPronunciation> cachedResults;

        public EndlessWordAdapter(Context context, ListAdapter adapter,
                int id) {
            super(context, adapter, id);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            final int PAGE = 25;
            cachedResults = word.searchPronouncedWords(null, PAGE,
                    getWrappedAdapter().getCount() / PAGE + 1);
            return !(cachedResults.size() < PAGE);
        }

        @Override
        protected void appendCachedData() {
            WordAdapter a = (WordAdapter) getWrappedAdapter();
            for(WordAndPronunciation wap : cachedResults) {
                a.add(wap);
            }
        }

        @Override
        protected boolean onException(View pendingView, Exception ex) {
            toastException(ex);
            return false;
        }
    }

    @Override
    public void doSearch(String query) {
        doWordSearch(query);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        if(endlessWordAdapter.getItemViewType(info.position) == 
            Adapter.IGNORE_ITEM_VIEW_TYPE) {
            return;
            }
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)
            item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.search_pronunciations:
                doPronunciationSearch(((WordAndPronunciation)
                    endlessWordAdapter.getItem(info.position)).getWord()
                    .getOriginal());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
