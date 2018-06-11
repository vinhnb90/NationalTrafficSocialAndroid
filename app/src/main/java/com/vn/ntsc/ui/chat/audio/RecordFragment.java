package com.vn.ntsc.ui.chat.audio;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nankai.designlayout.dialog.DialogMaterial;
import com.nankai.designlayout.dialog.enums.Style;
import com.vn.ntsc.R;
import com.vn.ntsc.ui.chat.ChatActivity;
import com.vn.ntsc.ui.chat.listener.AudioSendListener;
import com.vn.ntsc.ui.chat.listener.OnSendAudioListener;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.chats.ChatUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Doremon on 12/25/2017.
 * record audio for chat
 */
public class RecordFragment extends Fragment implements AudioSendListener, MediaScannerConnection.OnScanCompletedListener {
    public static final String TAG = RecordFragment.class.getSimpleName();
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 30;
    private static final int OPEN_SETTING = 31;

    //    private MediaRecorder mRecorder;
    private File mOutputFile;
    private boolean isPlay = false;
    private OnSendAudioListener mOnSendAudioListener;

    @BindView(R.id.fragment_record_img_record_delete)
    ImageView imgDelete;
    @BindView(R.id.fragment_record_imageView)
    ImageView imageView;
    @BindView(R.id.fragment_record_tv_time_recording)
    TextView tvTimeRecording;
    @BindView(R.id.fragment_record_img_record_play)
    ImageView imgRecordPlay;
    @BindView(R.id.fragment_record_img_record_save)
    ImageView imgRecordSave;

    // timer
    private Runnable countRunnable;
    private Timer timer;
    private int count = 0;

    private int recorderSecondsElapsed;
    private MediaRecorder mRecorder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatActivity)
            ((ChatActivity) context).setListener(this);
    }

    public static RecordFragment newInstance(OnSendAudioListener listener) {
        Bundle args = new Bundle();
        RecordFragment fragment = new RecordFragment();
        fragment.mOnSendAudioListener = listener;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this, view);
//        initializationRecord();
//        initTimer();
        return view;
    }

    /**
     * init handler and runnable count
     */
//    private void initTimer() {
//        timer = new Handler();
//        countRunnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "count: " + count);
//                count++;
//                tvTimeRecording.setText(ChatUtils.formatSeconds(count));
//                timer.postDelayed(countRunnable, 1000);
//            }
//        };
//    }
    private void updateUiTimer(int count) {
        this.count = count;
        tvTimeRecording.setText(ChatUtils.formatSeconds(count));

        Log.d(TAG, "updateUiTimer: " + count);
    }

//    /**
//     * pause timer
//     *
//     * @see #startTimer(long)
//     */
//    public void pauseTimer() {
////        timer.removeCallbacks(countRunnable);
//    }

    /**
     * start timer
     *
     * @param delay delay before timer start
     * @see #pauseTimer()
     * @see #resetTimer()
     */
//    public void startTimer() {
//        timer.post(countRunnable);
//    }

    /**
     * stop and reset count to default
     */
    public void resetTimer() {
//        timer.removeCallbacks(countRunnable);
        count = recorderSecondsElapsed;
        recorderSecondsElapsed = 0;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.e(TAG, "onViewCreated");
        requestPermission();
    }

    /**
     * request all needed permissions for record
     */
    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING) {
            int resContentMessage = -1;
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                resContentMessage = R.string.request_permission_open_setting_denied;
            } else {
                resContentMessage = R.string.request_permission_open_setting_granted_all;
            }
            Toast.makeText(getContext(), resContentMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
                boolean StorageWPermission = grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean StorageRPermission = grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED;
                boolean RecordPermission = grantResults[2] ==
                        PackageManager.PERMISSION_GRANTED;

                boolean isNeverAsk = false;
                for (String permission : permissions) {
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        isNeverAsk = true;
                        break;
                    }
                }

                if (!StorageWPermission || !StorageRPermission || !RecordPermission) {
                    // check if contain at least 1 never ask
                    if (isNeverAsk) {
                        DialogMaterial.Builder builder = new DialogMaterial.Builder(getActivity());
                        builder.setStyle(Style.HEADER_WITH_TITLE)
                                .setTitle(R.string.application_notification_title)
                                .setCancelable(false)
                                .setContent(R.string.request_permission_open_setting)
                                .onPositive(R.string.common_settings, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // open setting
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                        intent.setData(uri);
                                        getActivity().startActivityForResult(intent, OPEN_SETTING);

                                    }
                                })
                                .onNegative(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.show();
                    } else {
                        // show permission
                        DialogMaterial.Builder builder = new DialogMaterial.Builder(getActivity());
                        builder.setStyle(Style.HEADER_WITH_TITLE)
                                .setTitle(R.string.application_notification_title)
                                .setCancelable(false)
                                .setContent(R.string.request_permission_again)
                                .onPositive(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermission();
                                    }
                                })
                                .onNegative(R.string.common_cancel_2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder.show();
                    }
                }
            }
        }
    }

    @OnClick({R.id.fragment_record_imageView, R.id.fragment_record_img_record_play, R.id.fragment_record_img_record_save, R.id.fragment_record_img_record_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_record_imageView:
                break;
            case R.id.fragment_record_img_record_play:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                    return;
                }
                actionToggleRecord();

                break;
            case R.id.fragment_record_img_record_save:
                if (mRecorder != null && mOutputFile != null && mOutputFile.exists()) {
                    imgRecordSave.setEnabled(false);
                    resetTimer();
                    stopRecording();
                    updateUI();
                    showDialog(getResources().getString(R.string.save_file));
                }
                break;

            case R.id.fragment_record_img_record_delete:
                resetTimer();
                if (mOutputFile != null && mOutputFile.exists()) {
                    imgRecordSave.setEnabled(true);
                    stopRecord();
                    updateUI();
                    if (mOutputFile.delete()) {
                        mOutputFile = null;
                        showDialog(getResources().getString(R.string.delete_file));
                    }
                }
                break;
        }
    }

    /**
     * stop record
     */
    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * record|pause audio record
     */
    @RequiresPermission(allOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
    private void actionToggleRecord() {
        if (mRecorder == null) {
            recorderSecondsElapsed = 0;
            isPlay = true;
            startRecording();
            startTimer();
            imgRecordSave.setEnabled(true);
            imageView.setImageResource(R.drawable.ic_recording);
            imgRecordPlay.setImageResource(R.drawable.ic_record_play);
            LogUtils.e(TAG, "Start recording");
        } else {
            resetTimer();
            stopRecording();
            imageView.setImageResource(R.drawable.ic_record);
            imgRecordPlay.setImageResource(R.drawable.ic_record_pause);
            LogUtils.e(TAG, "Stop recording : " + recorderSecondsElapsed);
        }
    }

    /**
     * init and start record
     */
    private void startRecording() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MMdd_HH_mm_ss", Locale.US);
        mOutputFile = new File(Environment.getExternalStorageDirectory() + "/vrecordnts/RECORDING_" + dateFormat.format(new Date()) + ".mp4");

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mOutputFile.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
            e.printStackTrace();
        }
        mRecorder.start();
    }

    private void stopRecord() {
        stopRecording();
        recorderSecondsElapsed = 0;
    }


    @Override
    public void onAudioSend() {
        LogUtils.e(TAG, "onAudioSend :" + recorderSecondsElapsed + " count : " + count);
        if (mRecorder != null) {
            if (recorderSecondsElapsed <= 1) return;
        } else {
            if (count <= 1) return;
        }

        stopRecord();
        resetTimer();
        updateUI();
        imgRecordSave.setEnabled(true);
        // notify audio added, see {@link #onScanCompleted(String, Uri)}
        if (mOutputFile != null && getActivity() != null) {
            LogUtils.e(TAG, "mOutputFile: " + getContext() + "-->>" + getActivity() + "---->" + mOutputFile.getAbsolutePath());
            MediaScannerConnection.scanFile(getActivity(), new String[]{mOutputFile.getAbsolutePath()}, null, this);
        }
    }

    @Override
    public void onAudioSendSucces() {
        if (mOutputFile == null) return;
//        mOutputFile.delete();
        mOutputFile = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.e(TAG, "onStop");
        releaseRecorder();
    }

    @Override
    public void onDestroy() {
        LogUtils.e(TAG, "onDestroy");
        releaseRecorder();
        super.onDestroy();
    }

    /**
     * release recorder on stop
     */
    private void releaseRecorder() {
        stopRecord();
//        stopRecording();
        if (mRecorder != null) {
            mRecorder = null;
        }
        resetTimer();
    }

    // update ui stop record
    private void updateUI() {
        tvTimeRecording.setText(ChatUtils.formatSeconds(count));
        imageView.setImageResource(R.drawable.ic_record);
        imgRecordPlay.setImageResource(R.drawable.ic_record_pause);
    }

    // show dialog delete or save file success
    private void showDialog(String content) {
        DialogMaterial.Builder builder = new DialogMaterial.Builder(getActivity());
        builder.setStyle(Style.HEADER_WITH_TITLE)
                .setTitle(R.string.application_notification_title)
                .setCancelable(false)
                .setContent(content)
                .onNegative(R.string.common_ok_2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        LogUtils.e(TAG, "onAudioSend ---> onScanCompleted:" + recorderSecondsElapsed);
        if (mOnSendAudioListener != null && mOutputFile != null && mOutputFile.exists()) {
            mOnSendAudioListener.onRecordSendClick(mOutputFile.getAbsolutePath());
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void updateTimer() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRecorder != null) {
                    tvTimeRecording.setText(ChatUtils.formatSeconds(recorderSecondsElapsed));
                    recorderSecondsElapsed++;
                }
            }
        });
    }

}
