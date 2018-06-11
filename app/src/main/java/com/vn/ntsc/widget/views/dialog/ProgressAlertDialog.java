package com.vn.ntsc.widget.views.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;

/**
 * Created by ThoNh on 11/17/2017.
 */

public class ProgressAlertDialog {

    private TextView mTextView;
    private AlertDialog.Builder mBuilder;
    private AlertDialog alertDialog;

    public ProgressAlertDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_progress_dialog, null);
        mTextView = view.findViewById(R.id.text_progress);

        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(view);
        alertDialog = mBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void showText(int resID) {
        if (mTextView != null) {

            if (mTextView.getVisibility() == View.GONE || mTextView.getVisibility() == View.INVISIBLE) {
                mTextView.setVisibility(View.VISIBLE);
            }

            mTextView.setText(resID);
        }
    }

    public void showText(String text) {
        if (mTextView != null) {

            if (mTextView.getVisibility() == View.GONE || mTextView.getVisibility() == View.INVISIBLE) {
                mTextView.setVisibility(View.VISIBLE);
            }

            mTextView.setText(text);
        }
    }

    public void hideTitle() {
        if (mTextView != null) {
            mTextView.setVisibility(View.GONE);
        }
    }

    public void show() {
        alertDialog.show();
    }

    public void hide() {
        alertDialog.dismiss();
    }
}
