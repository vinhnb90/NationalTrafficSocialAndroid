package com.vn.ntsc.repository.model.chat;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.google.gson.annotations.SerializedName;
import com.tux.socket.models.Gift;
import com.tux.socket.models.Media;
import com.tux.socket.models.Sticker;
import com.tux.socket.models.Text;
import com.vn.ntsc.core.model.BaseBean;
import com.vn.ntsc.utils.LogUtils;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by ThoNh on 9/26/2017.
 * model for display chat list
 */
public class ChatMessage extends BaseBean {
    public static final String AUDIO = "AUDIO";
    /**
     * for video|image
     */
    public static final String MEDIA = "MEDIA";

    public static final String PP = "PP";//Message only text
    //    public static final String PE = "PE";//Message conains text and emoji

    /**
     * file type from history
     */
    public static final String FILE = "FILE";
    public static final String GIFT = "GIFT";
    public static final String STICKER = "STK";
    public static final String TYPING = "TYPING";
    public static final String HEADER_TIMER = "HEADER_TIMER";

    @SerializedName("msg_id")
    public String messageId;
    /**
     * sender id
     */
    @SerializedName("from")
    public String senderId;
    /**
     * receiver id
     */
    @SerializedName("to")
    public String receiverId;

    @SerializedName("msg_type")
    public String mMsgType;

    @SerializedName("is_own")
    public boolean isOwn;

    @SerializedName("content")
    public String mContent;

    @SerializedName("time_stamp")
    public String mTimeStamp;

    @SerializedName("sticker_url")
    public String stickerUrl;

    @SerializedName("cat_id")
    public String catId;

    @SerializedName("files")
    public List<Media.FileBean> listFile = new ArrayList<>();

    @SerializedName("read_time")
    public String readTime;

    public static final int STATUS_UNSET = -1;
    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_SEEN = 3;


    // status play audio

    public static final int STATE_AUDIO_COMPLETE = -1;
    public static final int STATE_AUDIO_PREPARING = 0;
    public static final int STATE_AUDIO_PLAY = 1;
    public static final int STATE_AUDIO_PAUSE = 2;
    public static final int STATE_AUDIO_ERROR = 4;
    public int audioType = STATE_AUDIO_COMPLETE;

    @Retention(SOURCE)
    @IntDef({STATUS_UNSET, STATUS_SENDING, STATUS_SUCCESS, STATUS_ERROR, STATUS_SEEN})
    public @interface SendMessageStatus {
    }

    @SendMessageStatus
    public int sendMesasgeStatus = STATUS_UNSET;
    /**
     * percent upload file
     */
    @IntRange(from = 0, to = 100)
    public int progress = 0;
    /**
     * show/hide ic_progress bar while upload file
     */
    public boolean isLoading;
    /**
     * show/hide typing animation
     */
    public boolean isTyping;

    public boolean isPlaying = true;

    public boolean isAudioPogress = false;

    private ChatMessage(Builder builder) {
        messageId = builder.messageId;
        senderId = builder.senderId;
        receiverId = builder.receiverId;
        mMsgType = builder.mMsgType;
        isOwn = builder.isOwn;
        mContent = builder.mContent;
        mTimeStamp = builder.mTimeStamp;
        listFile = builder.listFile;
        setProgress(builder.progress);
        isLoading = builder.isLoading;
        isTyping = builder.isTyping;
        isPlaying = builder.isPlaying;
    }

    public String getRawMessage() {
        switch (mMsgType) {
            case FILE:
            case MEDIA:
            case AUDIO:
                Media media = new Media(senderId, receiverId, listFile);
                media.setId(messageId);
                return media.toJson();
            case GIFT:
                Gift message = new Gift(mContent, senderId, receiverId);
                message.setId(messageId);
                return message.toJson();
            case STICKER:
                Sticker sticker = new Sticker("", "", senderId, receiverId, stickerUrl);
                sticker.setId(messageId);
                return sticker.toJson();
            case PP:
                Text text = new Text(senderId, receiverId, mContent, PP);
                text.setId(messageId);
                return text.toJson();
            default:
                return null;
        }
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ChatMessage() {
    }

    public ChatMessage(String mMsgType, boolean isOwn, String mContent, String mTimeStamp, String stickerUrl, String catId) {
        this.mMsgType = mMsgType;
        this.isOwn = isOwn;
        this.mContent = mContent;
        this.mTimeStamp = mTimeStamp;
        this.stickerUrl = stickerUrl;
        this.catId = catId;
    }

    public boolean isSticker() {
        return mMsgType.equals(STICKER);
    }

    public String getMessageType() {
        return mMsgType;
    }

    public String getContent() {
        if (mMsgType.equals(PP)) {
            return mContent;
        }
        return mContent;
    }

    public List<Media.FileBean> getListFile() {
        return listFile;
    }

    public String getMsgType() {
        return mMsgType;
    }

    public boolean isOwn() {
        return isOwn;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isTyping() {
        return isTyping;
    }

    /**
     * {@code ChatMessage} builder static inner class.
     */
    public static final class Builder {
        public String messageId;
        public String senderId;
        public String receiverId;
        public String readTime;
        private String mMsgType;
        private boolean isOwn;
        private String mContent;
        private String mTimeStamp;
        private List<Media.FileBean> listFile;
        private int progress;
        private boolean isLoading;
        private boolean isTyping;
        private boolean isPlaying;
        private int sendMesasgeStatus = STATUS_UNSET;

        public Builder() {
        }

        public Builder sendMesasgeStatus(@SendMessageStatus int sendMesasgeStatus) {
            this.sendMesasgeStatus = sendMesasgeStatus;
            return this;
        }

        public Builder setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder setSenderId(String senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder setReceiverId(String receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public Builder isPlaying(boolean val) {
            isPlaying = val;
            return this;
        }

        public Builder messageReadTime(String readTime) {
            this.readTime = readTime;
            return this;
        }

        /**
         * Sets the {@code mMsgType} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code mMsgType} to set
         * @return a reference to this Builder
         */
        public Builder messageType(String val) {
            mMsgType = val;
            return this;
        }

        /**
         * Sets the {@code isOwn} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code isOwn} to set
         * @return a reference to this Builder
         */
        public Builder isOwn(boolean val) {
            isOwn = val;
            return this;
        }

        /**
         * Sets the {@code mContent} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code mContent} to set
         * @return a reference to this Builder
         */
        public Builder content(String val) {
            mContent = val;
            return this;
        }

        /**
         * Sets the {@code mTimeStamp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code mTimeStamp} to set
         * @return a reference to this Builder
         */
        public Builder timeStamp(String val) {
            mTimeStamp = val;
            return this;
        }

        /**
         * Sets the {@code listFile} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code listFile} to set
         * @return a reference to this Builder
         */
        public Builder files(List<Media.FileBean> val) {
            listFile = val;
            LogUtils.d("listFile", " " + listFile.size());
            return this;
        }

        /**
         * Sets the {@code ic_progress} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code ic_progress} to set
         * @return a reference to this Builder
         */
        public Builder progress(int val) {
            progress = val;
            return this;
        }

        /**
         * Sets the {@code isLoading} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code isLoading} to set
         * @return a reference to this Builder
         */
        public Builder isLoading(boolean val) {
            isLoading = val;
            return this;
        }

        /**
         * Sets the {@code isTyping} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param val the {@code isTyping} to set
         * @return a reference to this Builder
         */
        public Builder isTyping(boolean val) {
            isTyping = val;
            return this;
        }

        /**
         * Returns a {@code ChatMessage} built from the parameters previously set.
         *
         * @return a {@code ChatMessage} built with parameters of this {@code ChatMessage.Builder}
         */
        public ChatMessage build() {
            return new ChatMessage(this);
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessage)) return false;

        ChatMessage message = (ChatMessage) o;

        return messageId.equals(message.messageId);
    }

    @Override
    public int hashCode() {
        return messageId.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageId);
        dest.writeString(this.senderId);
        dest.writeString(this.receiverId);
        dest.writeString(this.mMsgType);
        dest.writeByte(this.isOwn ? (byte) 1 : (byte) 0);
        dest.writeString(this.mContent);
        dest.writeString(this.mTimeStamp);
        dest.writeString(this.stickerUrl);
        dest.writeString(this.catId);
        dest.writeTypedList(this.listFile);
        dest.writeString(this.readTime);
        dest.writeInt(this.audioType);
        dest.writeInt(this.sendMesasgeStatus);
        dest.writeInt(this.progress);
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTyping ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPlaying ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isAudioPogress ? (byte) 1 : (byte) 0);
    }

    protected ChatMessage(Parcel in) {
        this.messageId = in.readString();
        this.senderId = in.readString();
        this.receiverId = in.readString();
        this.mMsgType = in.readString();
        this.isOwn = in.readByte() != 0;
        this.mContent = in.readString();
        this.mTimeStamp = in.readString();
        this.stickerUrl = in.readString();
        this.catId = in.readString();
        this.listFile = in.createTypedArrayList(Media.FileBean.CREATOR);
        this.readTime = in.readString();
        this.audioType = in.readInt();
        this.sendMesasgeStatus = in.readInt();
        this.progress = in.readInt();
        this.isLoading = in.readByte() != 0;
        this.isTyping = in.readByte() != 0;
        this.isPlaying = in.readByte() != 0;
        this.isAudioPogress = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}
