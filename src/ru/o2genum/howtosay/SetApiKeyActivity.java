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
 * Provides possibility to set custom API key
 *
 * @author Andrey Moiseev
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetApiKeyActivity extends BaseActivity {
    EditText editKey;
    TextView keyStatus;
    Button setButton;

    KeyState keyState = KeyState.KEY_IS_NOT_SET;
    String key = "";

    enum KeyState { KEY_IS_SET, KEY_IS_NOT_SET, KEY_SEEMS_WRONG };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.set_api_key, null);
        editKey = (EditText) view.findViewById(R.id.key_edit);
        keyStatus = (TextView) view.findViewById(R.id.key_status);
        setButton = (Button) view.findViewById(R.id.set_button);
        loadKeyFromPrefs();
        updateViews();
        setButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                storeKey(editKey.getText().toString().trim());
                loadKeyFromPrefs();
                updateViews();
            }
        });
        setView(view);
    }

    private void loadKeyFromPrefs() {
        key = prefs.getString(PREFS_API_KEY, "");
        key = key.trim();
        keyState = KeyState.KEY_IS_SET;
        if(key.length() == 0) {
            keyState  = KeyState.KEY_IS_NOT_SET;
            return;
        }
        if(key.length() != 32) {
            keyState = KeyState.KEY_SEEMS_WRONG;
            return;
        }
        for(int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if(!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z'))) {
                keyState = KeyState.KEY_SEEMS_WRONG;
            }
        }
        return;
    }

    private void storeKey(String key) {
        prefs.edit().putString(PREFS_API_KEY, key).commit();
    }

    private void updateViews() {
        editKey.setText(key, TextView.BufferType.EDITABLE);
        switch(keyState) {
            case KEY_IS_SET:
                keyStatus.setText(getString(R.string.key_is_set),
                        TextView.BufferType.SPANNABLE);
                break;
            case KEY_IS_NOT_SET:
                keyStatus.setText(getString(R.string.key_is_not_set),
                        TextView.BufferType.SPANNABLE);
                break;
            case KEY_SEEMS_WRONG:
                keyStatus.setText(getString(R.string.key_seems_wrong),
                        TextView.BufferType.SPANNABLE);
                break;
        }
    }
}
