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

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SidebarItemView extends LinearLayout {
    Context context;
    View v;
    ImageView iv;
    TextView tv;
    AttributeSet attrs;
    boolean selected = false;
    final String NAMESPACE =
        "http://schemas.android.com/apk/res/ru.o2genum.howtosay";

    public SidebarItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        loadView();
    }

    public SidebarItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        loadView();
    }

    private void loadView() {
        LayoutInflater inflater = (LayoutInflater)
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = (View) inflater.inflate(R.layout.sidebar_item, this, true);
        iv = (ImageView) v.findViewById(R.id.imageview);
        iv.setImageResource(attrs.getAttributeResourceValue(NAMESPACE,
                    "icon", 0));
        tv = (TextView) v.findViewById(R.id.textview);
        tv.setText(attrs.getAttributeResourceValue(NAMESPACE, "text", 0));
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected) {
            v.setBackgroundResource(
                    R.drawable.sidebar_item_selected_background);
        } else {
            v.setBackgroundResource(0);
        }
    }

    public boolean isSelected() {
        return selected;
    }
}
