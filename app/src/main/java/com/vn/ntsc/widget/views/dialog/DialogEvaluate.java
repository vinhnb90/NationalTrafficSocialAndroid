package com.vn.ntsc.widget.views.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nankai.designlayout.BorderView;
import com.vn.ntsc.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ducng on 11/7/2017.
 */

public class DialogEvaluate extends AlertDialog {
    public static final int GENTLY = 1;
    public static final int FUNNY = 2;
    public static final int SWEET = 3;
    public static final int POWERFUL = 4;
    public static final int FRIENDLY = 5;
    public static final int WARM = 6;
    public static final int TOUGH = 7;
    public static final int SEXY = 8;
    public static final int COOL = 9;
    public static final int SOFT = 10;

    @BindView(R.id.tv_gently)
    BorderView mTvGently;

    @BindView(R.id.tv_funny)
    BorderView mTvFunny;

    @BindView(R.id.tv_sweet)
    BorderView mTvSweet;

    @BindView(R.id.tv_powerful)
    BorderView mTvPowerful;

    @BindView(R.id.tv_friendly)
    BorderView mTvFriendly;

    @BindView(R.id.tv_warm)
    BorderView mTvWarm;

    @BindView(R.id.tv_tough)
    BorderView mTvTough;

    @BindView(R.id.tv_sexy)
    BorderView mTvSexy;

    @BindView(R.id.tv_cool)
    BorderView mTvCool;

    @BindView(R.id.tv_soft)
    BorderView mTvSoft;

    @BindView(R.id.dialog_button)
    TextView dialogButton;

    private int evaluateType = 0;

    private OnSaveEvaluateListener mSaveEvaluateListener;


    public DialogEvaluate(Context context, OnSaveEvaluateListener listener) {
        super(context);
        mSaveEvaluateListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_evaluate);
        ButterKnife.bind(this, this);
    }

    /**
     * onClick
     *
     * @param view
     */
    @OnClick({R.id.tv_gently, R.id.tv_funny, R.id.tv_sweet, R.id.tv_powerful, R.id.tv_friendly, R.id.tv_warm, R.id.tv_tough, R.id.tv_sexy, R.id.tv_cool, R.id.tv_soft, R.id.dialog_button})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.tv_gently:
                onClickSelect(evaluateType);
                evaluateType = GENTLY;
                mTvGently.setBackgroundColor(getContext().getResources().getColor(R.color.color_gently));
                mTvGently.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_funny:
                onClickSelect(evaluateType);
                evaluateType = FUNNY;
                mTvFunny.setBackgroundColor(getContext().getResources().getColor(R.color.color_funny));
                mTvFunny.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_sweet:
                onClickSelect(evaluateType);
                evaluateType = SWEET;
                mTvSweet.setBackgroundColor(getContext().getResources().getColor(R.color.color_sweet));
                mTvSweet.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_powerful:
                onClickSelect(evaluateType);
                evaluateType = POWERFUL;
                mTvPowerful.setBackgroundColor(getContext().getResources().getColor(R.color.color_pơwerful));
                mTvPowerful.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_friendly:
                onClickSelect(evaluateType);
                evaluateType = FRIENDLY;
                mTvFriendly.setBackgroundColor(getContext().getResources().getColor(R.color.color_friendly));
                mTvFriendly.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_warm:
                onClickSelect(evaluateType);
                evaluateType = WARM;
                mTvWarm.setBackgroundColor(getContext().getResources().getColor(R.color.color_warm));
                mTvWarm.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_tough:
                onClickSelect(evaluateType);
                evaluateType = TOUGH;
                mTvTough.setBackgroundColor(getContext().getResources().getColor(R.color.color_tough));
                mTvTough.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_sexy:
                onClickSelect(evaluateType);
                evaluateType = SEXY;
                mTvSexy.setBackgroundColor(getContext().getResources().getColor(R.color.color_sexy));
                mTvSexy.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_cool:
                onClickSelect(evaluateType);
                evaluateType = COOL;
                mTvCool.setBackgroundColor(getContext().getResources().getColor(R.color.color_cool));
                mTvCool.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.tv_soft:
                onClickSelect(evaluateType);
                evaluateType = SOFT;
                mTvSoft.setBackgroundColor(getContext().getResources().getColor(R.color.color_soft));
                mTvSoft.setTextColor(getContext().getResources().getColor(R.color.white));
                break;
            case R.id.dialog_button:
                if (mSaveEvaluateListener != null && evaluateType != 0) {
                    mSaveEvaluateListener.onSaveEvaluate(evaluateType);
                }
                dismiss();
                break;
        }
    }
    /**
     * set selected in item view
     *
     * @param type
     */
    private void onClickSelect(int type) {
        switch (type) {
            case GENTLY:
                mTvGently.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvGently.setTextColor(getContext().getResources().getColor(R.color.color_gently));
                break;
            case FUNNY:
                mTvFunny.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvFunny.setTextColor(getContext().getResources().getColor(R.color.color_funny));
                break;
            case SWEET:
                mTvSweet.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvSweet.setTextColor(getContext().getResources().getColor(R.color.color_sweet));
                break;
            case POWERFUL:
                mTvPowerful.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvPowerful.setTextColor(getContext().getResources().getColor(R.color.color_pơwerful));
                break;
            case FRIENDLY:
                mTvFriendly.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvFriendly.setTextColor(getContext().getResources().getColor(R.color.color_friendly));
                break;
            case WARM:
                mTvWarm.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvWarm.setTextColor(getContext().getResources().getColor(R.color.color_warm));
                break;
            case TOUGH:
                mTvTough.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvTough.setTextColor(getContext().getResources().getColor(R.color.color_tough));
                break;
            case SEXY:
                mTvSexy.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvSexy.setTextColor(getContext().getResources().getColor(R.color.color_sexy));
                break;
            case COOL:
                mTvCool.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvCool.setTextColor(getContext().getResources().getColor(R.color.color_cool));
                break;
            case SOFT:
                mTvSoft.setBackgroundColor(getContext().getResources().getColor(R.color.transparent));
                mTvSoft.setTextColor(getContext().getResources().getColor(R.color.color_soft));
                break;
        }
    }

    public interface OnSaveEvaluateListener {
        void onSaveEvaluate(int value);
    }
}
