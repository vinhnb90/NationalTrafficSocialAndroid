package com.nankai.designlayout.dialog.numberpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.nankai.designlayout.R;

public class AgeBetweenPickerDialog extends AlertDialog {
	private NumberPicker mNumberPickerOne, mNumberPickerTwo;
	private int mInitialValueOne, mInitialValueTwo;

	public AgeBetweenPickerDialog(Context context, int tileId,
			OnClickListener listener) {
		super(context);
		setTitle(context.getText(tileId));
		setButton(BUTTON_POSITIVE, context.getText(R.string.dialog_set_number), listener);
		setButton(BUTTON_NEGATIVE, context.getText(R.string.dialog_cancel), (OnClickListener) null);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_number_picker, null);
		view.findViewById(R.id.tv_unit_one).setVisibility(View.GONE);
		view.findViewById(R.id.tv_unit_two).setVisibility(View.GONE);
		setView(view);
		mNumberPickerOne = (NumberPicker) view
				.findViewById(R.id.num_picker_one);
		mNumberPickerTwo = (NumberPicker) view
				.findViewById(R.id.num_picker_two);
		mNumberPickerOne.setVisibility(View.VISIBLE);
		mNumberPickerTwo.setVisibility(View.VISIBLE);
	}

	public NumberPicker getNumberPicker() {
		return mNumberPickerOne;
	}

	public void setRangeOne(int start, int end) {
		mNumberPickerOne.setRange(start, end);
	}

	public void setRangeTwo(int start, int end) {
		mNumberPickerTwo.setRange(start, end);
	}

	public void setWrap(boolean wrap) {
		mNumberPickerOne.setWrap(wrap);
	}

	public void setAgeWrap(boolean wrapable) {
		mNumberPickerOne.setWrap(wrapable);
		mNumberPickerTwo.setWrap(wrapable);
	}

	public void setRange(int start, int end, String[] displayedValues) {
		mNumberPickerOne.setRange(start, end, displayedValues);
	}

	public void setInitialValue(int valueOne, int valueTow) {
		this.mInitialValueOne = valueOne;
		this.mInitialValueTwo = valueTow;
	}

	public int getMinAge() {
		return mNumberPickerOne.getCurrent();
	}

	public int getMaxAge() {
		return mNumberPickerTwo.getCurrent();
	}

	@Override
	public void show() {
		mNumberPickerTwo.setCurrent(mInitialValueTwo);
		mNumberPickerOne.setCurrent(mInitialValueOne);
		super.show();
	}
}