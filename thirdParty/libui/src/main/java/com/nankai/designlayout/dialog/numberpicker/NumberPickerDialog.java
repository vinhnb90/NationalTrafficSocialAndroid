/*
 * Copyright (C) 2010-2012 Mike Novak <michael.novakjr@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nankai.designlayout.dialog.numberpicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nankai.designlayout.R;

public class NumberPickerDialog extends AlertDialog implements OnClickListener {

	private OnNumberSetListener mListener;
	private NumberPicker mNumberPickerOne, mNumberPickerTwo, mNumberPickerThree;
	private int mInitialValueOne, mInitialValueTwo, mInitialValueThree;
	private SHOWMODE showMode = SHOWMODE.NORMAL;
	private TextView tvUnitOne;
	private TextView tvUnitTwo;
	private TextView tvUnitThree;

	/**
	 * @param context
	 *            is android's Context
	 * @param tileId
	 *            is id them
	 * @param showMode
	 *            value display
	 * @param tileId
	 *            is id string
	 */
	public NumberPickerDialog(Context context, int tileId, SHOWMODE showMode,
			String unitOne, String unitTwo) {
		super(context);
		this.showMode = showMode;
		setButton(BUTTON_POSITIVE, context.getText(R.string.dialog_set_number),
				this);
		setButton(BUTTON_NEGATIVE, context.getText(R.string.dialog_cancel),
				(OnClickListener) null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(context.getText(tileId));

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_number_picker, null);
		setView(view);
		mNumberPickerOne = view
				.findViewById(R.id.num_picker_one);
		mNumberPickerTwo = view
				.findViewById(R.id.num_picker_two);
		mNumberPickerThree = view
				.findViewById(R.id.num_picker_three);
		tvUnitOne = view.findViewById(R.id.tv_unit_one);
		tvUnitTwo = view.findViewById(R.id.tv_unit_two);
		tvUnitOne.setText(unitOne.length() <= 0 ? "'" : unitOne);// default =
																	// foot
		tvUnitTwo.setText(unitTwo.length() <= 0 ? "''" : unitTwo);// default =
																	// inch
		switch (showMode) {
		case NORMAL:
		case TWO:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.VISIBLE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.VISIBLE);
			break;
		case ONE:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.GONE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	/**
	 * @param context
	 *            is android's Context
	 * @param tileId
	 *            is id them
	 * @param showMode
	 *            value display
	 * @param tileId
	 *            is id string
	 */
	public NumberPickerDialog(Context context, int tileId, SHOWMODE showMode,
			String unitOne, String unitTwo, String unitThree) {
		super(context);
		this.showMode = showMode;
		setButton(BUTTON_POSITIVE, context.getText(R.string.dialog_set_number),
				this);
		setButton(BUTTON_NEGATIVE, context.getText(R.string.dialog_cancel),
				(OnClickListener) null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setTitle(context.getText(tileId));

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_number_picker, null);
		setView(view);
		mNumberPickerOne =  view
				.findViewById(R.id.num_picker_one);
		mNumberPickerTwo = view
				.findViewById(R.id.num_picker_two);
		mNumberPickerThree = view
				.findViewById(R.id.num_picker_three);
		tvUnitOne = view.findViewById(R.id.tv_unit_one);
		tvUnitTwo = view.findViewById(R.id.tv_unit_two);
		tvUnitThree = view.findViewById(R.id.tv_unit_three);
		tvUnitOne.setText(unitOne.length() <= 0 ? "'" : unitOne);// default =
																	// foot
		tvUnitTwo.setText(unitTwo.length() <= 0 ? "''" : unitTwo);// default =
																	// inch
		tvUnitThree.setText(unitThree.length() <= 0 ? "'''" : unitThree);
		switch (showMode) {
		case NORMAL:
		case TWO:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.VISIBLE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.VISIBLE);
			break;
		case ONE:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.GONE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.GONE);
			break;

		case THREE:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.VISIBLE);
			mNumberPickerThree.setVisibility(View.VISIBLE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.VISIBLE);
			tvUnitThree.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	public void showMode(SHOWMODE showmode) {
		this.showMode = showmode;
	}

	/**
	 * Retrieve the number picker used in the dialog
	 */
	public NumberPicker getNumberPicker() {
		return mNumberPickerOne;
	}

	/**
	 * Set the range allowed for the number picker with edittext one
	 * 
	 * @param start
	 *            the minimum allowed value
	 * @param end
	 *            the maximum allowed value
	 * @deprecated Instead this can be set by retrieving the numberpicker and
	 *             setting the value directly.
	 */
	@Deprecated
	public void setRangeOne(int start, int end) {
		mNumberPickerOne.setRange(start, end);
	}

	/**
	 * Set the range allowed for the number picker with edittext two
	 * 
	 * @param start
	 *            the minimum allowed value
	 * @param end
	 *            the maximum allowed value
	 * @deprecated Instead this can be set by retrieving the numberpicker and
	 *             setting the value directly.
	 */
	@Deprecated
	public void setRangeTwo(int start, int end) {
		mNumberPickerTwo.setRange(start, end);
	}

	public void setRangeThree(int start, int end) {
		mNumberPickerThree.setRange(start, end);
	}

	/**
	 * Set the wrap option for the number picker
	 * 
	 * @param wrap
	 *            true if values need to wrap
	 * @deprecated Instead this can be set by retrieving the numberpicker and
	 *             setting the value directly.
	 */
	@Deprecated
	public void setWrap(boolean wrap) {
		mNumberPickerOne.setWrap(wrap);
	}

	/**
	 * Set the range for the number picker and the values to display
	 * 
	 * @param start
	 *            the minimum allowed value
	 * @param end
	 *            the maximum allowed value
	 * @param displayedValues
	 *            values to display in the numberpicker instead of the integer
	 *            values of the range
	 * @deprecated Instead this can be set by retrieving the numberpicker and
	 *             setting the value directly.
	 */
	@Deprecated
	public void setRange(int start, int end, String[] displayedValues) {
		mNumberPickerOne.setRange(start, end, displayedValues);
	}

	public void setOnNumberSetListener(OnNumberSetListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mListener != null) {
			mListener.onNumberOneSet(mNumberPickerOne.getCurrent());
			mListener.onNumberTwoSet(mNumberPickerTwo.getCurrent());
			mListener.onNumberOneAndTwoSet(mNumberPickerOne.getCurrent(),
					mNumberPickerTwo.getCurrent());
		}
	}

	public interface OnNumberSetListener {
		public void onNumberOneSet(int selectedNumber);

		public void onNumberTwoSet(int selectedNumber);

		public void onNumberOneAndTwoSet(int numberOne, int numberTwo);
	}

	public void setInitialBoldValue(int valueOne, int valueTow, int valueThree) {
		this.mInitialValueOne = valueOne;
		this.mInitialValueTwo = valueTow;
		this.mInitialValueThree = valueThree;
	}

	public void setInitialBoldValue(int valueOne, int valueTow) {
		this.mInitialValueOne = valueOne;
		this.mInitialValueTwo = valueTow;
	}

	public void setInitialOneValue(int valueOne) {
		this.mInitialValueOne = valueOne;
	}

	@Override
	public void show() {
		super.show();
		mNumberPickerTwo.setCurrent(mInitialValueTwo);
		mNumberPickerOne.setCurrent(mInitialValueOne);
		mNumberPickerThree.setCurrent(mInitialValueThree);
		switch (showMode) {
		case NORMAL:
		case TWO:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.VISIBLE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.VISIBLE);
			break;
		case ONE:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.GONE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.GONE);
			break;

		case THREE:
			mNumberPickerOne.setVisibility(View.VISIBLE);
			mNumberPickerTwo.setVisibility(View.VISIBLE);
			mNumberPickerThree.setVisibility(View.VISIBLE);
			tvUnitOne.setVisibility(View.VISIBLE);
			tvUnitTwo.setVisibility(View.VISIBLE);
			tvUnitThree.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}
	}

	// tungdx added
	public NumberPicker getNumberPickerOne() {
		return mNumberPickerOne;
	}

	public NumberPicker getNumberPickerTwo() {
		return mNumberPickerTwo;
	}

	public NumberPicker getNumberPickerThree() {
		return mNumberPickerThree;
	}
}
