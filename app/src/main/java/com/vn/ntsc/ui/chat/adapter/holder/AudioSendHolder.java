package com.vn.ntsc.ui.chat.adapter.holder;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tux.socket.models.Media;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.ui.chat.adapter.ChatAdapter;
import com.vn.ntsc.ui.chat.audio.SequenceAudioPlayer;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.widget.views.textview.TimerTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class AudioSendHolder extends BaseChatHolder<ChatMessage> {
    public static final String TAG = AudioSendHolder.class.getSimpleName();

    @BindView(R.id.tv_time_record_leng)
    TimerTextView tvDuration;

    @BindView(R.id.tv_status_send_record)
    TextView tvStatus;

    @BindView(R.id.tv_time_send_record)
    TextView tvTime;

    @BindView(R.id.image_media_state)
    ImageView imageMediaState;

    @BindView(R.id.layout_send_record)
    RelativeLayout layoutSendRecord;

    @BindView(R.id.progress_bar_audio)
    ProgressBar mProgressBar;

    @BindView(R.id.image_chat_error)
    ImageView imageChatError;

    private final SequenceAudioPlayer audioPlayer;
    private Timer timer;

    private BaseActivity activity;
    private int recorderSecondsElapsed;
    private int duration;

    private ChatAdapter chatAdapter;

    public AudioSendHolder(BaseActivity activity, MessageOnEventListener mMessageOnEventListener, View itemView, ChatAdapter chatAdapter, SequenceAudioPlayer audioPlayer) {
        super(itemView, mMessageOnEventListener);
        this.chatAdapter = chatAdapter;
        this.audioPlayer = audioPlayer;
        this.activity = activity;
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {
        if (message.sendMesasgeStatus == ChatMessage.STATUS_ERROR) {
            imageChatError.setVisibility(View.VISIBLE);
        } else {
            imageChatError.setVisibility(View.GONE);
        }
        imageChatError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessageOnEventListener != null) {
                    mMessageOnEventListener.onItemClickSenMessageError(message.messageId, getAdapterPosition());
                }

            }
        });

        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());

        if (message.mTimeStamp != null) {
            String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
            tvTime.setText(currenTime);
        }
        setMessageStatus(tvStatus, message);

        mProgressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.grayDark), PorterDuff.Mode.SRC_IN);

        switch (message.audioType) {
            case ChatMessage.STATE_AUDIO_PREPARING:
                updateDuration(View.VISIBLE, 0);
                tvDuration.resetCount();
                mProgressBar.setVisibility(View.VISIBLE);
                imageMediaState.setVisibility(View.GONE);
                break;

            case ChatMessage.STATE_AUDIO_PLAY:
                updateDuration(View.VISIBLE, audioPlayer.getCurrentPosition());
                tvDuration.startCount(audioPlayer.getCurrentPosition());
                imageMediaState.setVisibility(View.VISIBLE);
                imageMediaState.setImageResource(R.drawable.ic_record_play);
                mProgressBar.setVisibility(View.GONE);
                break;
            case ChatMessage.STATE_AUDIO_PAUSE:
                updateDuration(View.VISIBLE, audioPlayer.getCurrentPosition());
                tvDuration.stopCount();
                imageMediaState.setVisibility(View.VISIBLE);
                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                mProgressBar.setVisibility(View.GONE);
                break;
            case ChatMessage.STATE_AUDIO_COMPLETE:
                updateDuration(View.VISIBLE, message.getListFile().get(0).fileDuration);
                tvDuration.resetCount();

                imageMediaState.setVisibility(View.VISIBLE);
                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                mProgressBar.setVisibility(View.GONE);
                break;

            case ChatMessage.STATE_AUDIO_ERROR:
                updateDuration(View.VISIBLE, message.getListFile().get(0).fileDuration);
                tvDuration.resetCount();

                imageMediaState.setVisibility(View.VISIBLE);
                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.msg_common_no_connection, Toast.LENGTH_SHORT).show();
                break;
        }

        layoutSendRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.audioType == ChatMessage.STATE_AUDIO_PREPARING) return;

                final List<Media.FileBean> mArrayFileAudio = message.getListFile();
                final String audioSource = mArrayFileAudio.get(0).fileUrl;
                duration = mArrayFileAudio.get(0).fileDuration;

                // Set cái vị trí đang playing thành NONE

                int positionAudioNowPlaying = findLastAudioPlaying();
                LogUtils.e(TAG, "positionAudioNowPlaying: " + positionAudioNowPlaying + " position: " + position);
                if (positionAudioNowPlaying >= 0) {
                    chatAdapter.getData(positionAudioNowPlaying).audioType = ChatMessage.STATE_AUDIO_COMPLETE;
                    chatAdapter.notifyItemChanged(positionAudioNowPlaying);
                }

                // Set vị trí vừa được click thành play
//                LogUtils.e(TAG, "layoutSendRecord");
                audioPlayer.toggle(getAdapterPosition(), audioSource);


            }
        });

    }

    /**
     * set duration
     *
     * @param visibleState visible|gone
     * @param duration     to show in format time
     */
    private void updateDuration(int visibleState, int duration) {
        tvDuration.setVisibility(visibleState);
        tvDuration.setFormatText(duration);
    }

    /**
     * Tìm kiếm vị trị đang playing trên listChat
     * Vì chỉ play được duy nhất 1 audio trong cùng 1 thời điểm nên khi tìm được vị trí của audio ấy, return luôn
     *
     * @return : vị trí đang play audio
     */
    private int findLastAudioPlaying() {
        for (int position = 0; position < chatAdapter.getData().size(); position++) {
            ChatMessage msg = chatAdapter.getData(position);
            if (msg.getMsgType().equals(ChatMessage.AUDIO) && msg.audioType != ChatMessage.STATE_AUDIO_COMPLETE) {
                return position;
            }
        }
        return -1;
    }

    private void startTimer() {
        LogUtils.e(TAG, "start timer");
        recorderSecondsElapsed = 0;
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
            LogUtils.e(TAG, "stop timer");
            timer.cancel();
            timer.purge();
            timer = null;
        }

    }

    private void updateTimer() {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (recorderSecondsElapsed <= duration) {
                    LogUtils.e(TAG, "updateTimer timer");
                    tvDuration.setText(ChatUtils.formatSeconds(recorderSecondsElapsed));
                    recorderSecondsElapsed++;
                }
            }
        });
    }

    public void resetTimer() {
        recorderSecondsElapsed = 0;
    }
}
