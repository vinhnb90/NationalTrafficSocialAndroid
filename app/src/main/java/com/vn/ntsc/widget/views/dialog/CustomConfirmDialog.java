package com.vn.ntsc.widget.views.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vn.ntsc.R;

public class CustomConfirmDialog extends AlertDialog implements OnClickListener {
	private OnButtonClickListener buttonClickListener;
	private OnCancelClickListener cancelClickListener;

	public interface OnButtonClickListener {
		public void onYesClick();
	}

	public interface OnCancelClickListener {
		public void OnCancelClick();
	}

	public CustomConfirmDialog(Context context, String title, String msg,
                               boolean isYesNo) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View customTitle = inflater.inflate(R.layout.layout_dialog_custom, null);
		if (!TextUtils.isEmpty(title)) {
			((TextView) customTitle.findViewById(R.id.dialog_custom_tv_title)).setText(title);
			setCustomTitle(customTitle);
		}
		if (!TextUtils.isEmpty(msg)) {
			setMessage(msg);
		}
		if (isYesNo) {
			setButton(BUTTON_POSITIVE, context.getText(R.string.common_yes),
					this);
			setButton(BUTTON_NEGATIVE, context.getText(R.string.common_cancel),
					(OnClickListener) null);
		} else {
			setButton(BUTTON_POSITIVE, context.getText(R.string.common_ok),
					this);
		}
		int dividerId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = customTitle.findViewById(dividerId);
		if (divider != null) {
			divider.setBackgroundColor(context.getResources().getColor(R.color.transparent));
		}
	}

	public void setOnButtonClick(OnButtonClickListener listen) {
		buttonClickListener = listen;
	}

	public void setCancelClickListener(OnCancelClickListener cancelClickListener) {
		this.cancelClickListener = cancelClickListener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_NEGATIVE) {
			if (cancelClickListener != null) {
				cancelClickListener.OnCancelClick();
			}
		} else if (which == BUTTON_POSITIVE) {
			if (buttonClickListener != null) {
				buttonClickListener.onYesClick();
			}
		}
	}

}
