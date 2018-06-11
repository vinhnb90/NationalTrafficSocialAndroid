package com.nankai.designlayout.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nankai.designlayout.R;

import java.util.List;

/**
 * Created by nankai on 3/30/2017.
 */

public abstract class MaterialSpinnerBaseAdapter<T> extends BaseAdapter {

    private final Context context;
    private int selectedIndex;

    public MaterialSpinnerBaseAdapter(Context context) {
        this.context = context;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_spinner_item, parent, false);
            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);
            convertView.setTag(new ViewHolder(textView));
        } else {
            textView = ((ViewHolder) convertView.getTag()).textView;
        }

        textView.setText(getItem(position).toString());
        return convertView;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void notifyItemSelected(int index) {
        selectedIndex = index;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public abstract T getItem(int position);

    @Override public abstract int getCount();

    public abstract T get(int position);

    public abstract List<T> getItems();

    private static class ViewHolder {

        private TextView textView;

        private ViewHolder(TextView textView) {
            this.textView = textView;
        }

    }

}
