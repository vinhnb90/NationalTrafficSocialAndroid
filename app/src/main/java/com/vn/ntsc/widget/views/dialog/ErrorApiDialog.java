package com.vn.ntsc.widget.views.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;
import com.vn.ntsc.core.model.ServerResponse;
import com.vn.ntsc.utils.LogUtils;

/**
 * Created by nankai on 8/7/2017.
 */

public class ErrorApiDialog {
    private static final String TAG = "AlertDialog";
    private static AlertDialog mDialog;

    public static void showAlert(final Activity activity, int title, int code) {
        showAlert(activity, title, code, null, true);
    }

    public synchronized static void showAlert(final Activity activity,
                                              int title, final int code,
                                              final DialogInterface.OnClickListener onClickListener,
                                              boolean cancleable) {
        if (activity == null) {
            LogUtils.w(TAG, "Activity is null");
            return;
        }
        if (mDialog == null || !mDialog.isShowing()) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            View customTitle = inflater.inflate(R.layout.layout_dialog_customize_no_line, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            int message = ErrorString.getDescriptionOfErrorCode(code);
            if (message == R.string.common_alert)
                return;
            builder.setCancelable(cancleable);
            if (code == ServerResponse.DefinitionCode.SERVER_OUT_OF_DATE_API) {
                builder.setCancelable(false);
            }
            if (title != 0) {
                ((TextView) customTitle.findViewById(R.id.tv_title_dialog_customize_no_Line)).setText(title);
                builder.setCustomTitle(customTitle);
            }
            builder.setMessage(message);
            builder.setPositiveButton(R.string.common_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (onClickListener != null)
                                onClickListener.onClick(dialog, which);

                            if (code == ServerResponse.DefinitionCode.SERVER_OUT_OF_DATE_API) {
                                String packageName = activity.getPackageName();
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("market://details?id="
                                        + packageName));
                                activity.startActivity(intent);
                            }

                        }
                    });

            mDialog = builder.create();
        } else {
            return;
        }
        mDialog.show();
        int dividerId = mDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = mDialog.findViewById(dividerId);
        if (divider != null) {
            divider.setBackgroundColor(mDialog.getContext().getResources().getColor(R.color.transparent));
        }
    }
}