package org.sfsteam.glassclink;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by vkurinov on 09.12.2014.
 */
public class ListViewAdapter<T> extends ArrayAdapter {
    public ListViewAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }
    @Override
    public View getView(int position, android.view.View convertView, android.view.ViewGroup parent){
        TextView tv = (TextView) super.getView(position,convertView,parent);
        tv.setTextColor(Color.WHITE);
        return tv;
    }
}
