package com.vn.ntsc.ui.chat.adapter.holder;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tux.socket.models.Media;
import com.vn.ntsc.R;
import com.vn.ntsc.core.views.BaseActivity;
import com.vn.ntsc.repository.model.chat.ChatMessage;
import com.vn.ntsc.ui.chat.adapter.ChatAdapter;
import com.vn.ntsc.ui.chat.audio.SequenceAudioPlayer;
import com.vn.ntsc.utils.ImagesUtils;
import com.vn.ntsc.utils.LogUtils;
import com.vn.ntsc.utils.Utils;
import com.vn.ntsc.utils.chats.ChatUtils;
import com.vn.ntsc.widget.views.textview.TimerTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by ThoNh on 9/26/2017.
 */

public class AudioReceiverHolder extends BaseChatHolder<ChatMessage> {

    public static final String TAG = AudioReceiverHolder.class.getSimpleName();

    @BindView(R.id.avatar)
    ImageView imgAvatar;

    @BindView(R.id.tv_time_receiver_record)
    TextView tvTime;

    @BindView(R.id.tv_time_record_leng)
    TimerTextView tvDuration;

    @BindView(R.id.image_media_state)
    ImageView imageMediaState;

    @BindView(R.id.progress_bar_audio)
    ProgressBar mProgressBar;

    private SequenceAudioPlayer audioPlayer;

    private BaseActivity activity;
    private ChatAdapter chatAdapter;


    public AudioReceiverHolder(int gender, BaseActivity activity, MessageOnEventListener mMessageOnEventListener, String avatarUrl, View itemView, ChatAdapter chatAdapter, SequenceAudioPlayer audioPlayer) {
        super(itemView, mMessageOnEventListener);
        ImagesUtils.loadRoundedAvatar(avatarUrl, gender, imgAvatar);
        this.audioPlayer = audioPlayer;
        this.activity = activity;
        this.chatAdapter = chatAdapter;
    }

    @Override
    public void bindView(final ChatMessage message, final int position) {

        SimpleDateFormat sdfDay = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String currenTime = sdfDay.format(new Date(Utils.convertTimeToMilisecond(Utils.convertGMTtoLocale(message.mTimeStamp))));
        tvTime.setText(currenTime);

        mProgressBar.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.grayDark), PorterDuff.Mode.SRC_IN);

        switch (message.audioType) {
            case ChatMessage.STATE_AUDIO_PREPARING:
                updateDuration(View.VISIBLE, 0);
                tvDuration.resetCount();

                imageMediaState.setVisibility(View.GONE);
                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                mProgressBar.setVisibility(View.VISIBLE);
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

                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                imageMediaState.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                tvDuration.setText(ChatUtils.formatSeconds(message.getListFile().get(0).fileDuration));
                break;

            case ChatMessage.STATE_AUDIO_ERROR:
                updateDuration(View.VISIBLE, message.getListFile().get(0).fileDuration);
                tvDuration.resetCount();

                imageMediaState.setVisibility(View.VISIBLE);
                imageMediaState.setImageResource(R.drawable.ic_record_pause);
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(activity, R.string.msg_common_no_connection, Toast.LENGTH_SHORT).show();
                tvDuration.setText(ChatUtils.formatSeconds(message.getListFile().get(0).fileDuration));
                break;
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.audioType == ChatMessage.STATE_AUDIO_PREPARING) return;

                final List<Media.FileBean> mArrayFileAudio = message.getListFile();
                final String audioSource = mArrayFileAudio.get(0).fileUrl;


                // Set cái vị trí đang playing thành NONE
                int positionAudioNowPlaying = findLastAudioPlaying();
                LogUtils.e(TAG, "positionAudioNowPlaying: " + positionAudioNowPlaying + "position: " + position);

                if (positionAudioNowPlaying >= 0) {
                    chatAdapter.getData(positionAudioNowPlaying).audioType = ChatMessage.STATE_AUDIO_COMPLETE;
                    chatAdapter.notifyItemChanged(positionAudioNowPlaying);
                }

                // Set vị trí vừa được click thành play
                LogUtils.e(TAG, "layoutSendRecord");
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
}
